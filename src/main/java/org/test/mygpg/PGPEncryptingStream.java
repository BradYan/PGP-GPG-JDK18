package org.test.mygpg;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Date;
import java.util.Iterator;

@SuppressWarnings("PMD.ExcessiveImports")
public final class PGPEncryptingStream extends OutputStream {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(PGPEncryptingStream.class);

    private final KeyRings config;
    private final PGPAlgorithms algorithmSuite;
    private boolean isDoSign;
    /**
     * The signature uid.
     */
    private OutputStream encryptionDataStream;
    private PGPSignatureGenerator signatureGenerator;

    private ArmoredOutputStream armoredOutputStream;
    private OutputStream outerEncryptionStream;
    private BCPGOutputStream compressionStream;
    private PGPLiteralDataGenerator encryptionDataStreamGenerator;
    private PGPCompressedDataGenerator compressionStreamGenerator;

    /*
     * true --> This stream is _already_ closed
     */
    private boolean isClosed = false;

    private PGPEncryptingStream(final KeyRings config, final PGPAlgorithms algorithmSuite) {
        super();
        this.config = config;
        this.algorithmSuite = algorithmSuite;
    }

    //Return a stream that, when written plaintext into, writes the ciphertext into cipherTextSink.
    public static OutputStream create(final KeyRings config,
                                      final PGPAlgorithms algorithmSuite,
                                      final String signingUid,
                                      final OutputStream cipherTextSink,
                                      final PGPPublicKey signingPublicKey,
                                      final boolean armor,
                                      final PGPPublicKey pubEncKey)
            throws IOException, PGPException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

        if (config == null) {
            throw new IllegalArgumentException("No config");
        }

        if (cipherTextSink == null) {
            throw new IllegalArgumentException("no cipherTextSink");
        }

        if (!pubEncKey.isEncryptionKey()) {
            throw new PGPException(String
                    .format("This public key (0x%x) is not suitable for encryption", pubEncKey.getKeyID()));
        }

        final PGPEncryptingStream encryptingStream = new PGPEncryptingStream(config, algorithmSuite);
        encryptingStream.setup(cipherTextSink, signingUid, pubEncKey, signingPublicKey, armor);
        return encryptingStream;
    }


    /**
     * @param cipherTextSink Where the ciphertext goes
     * @param signingUid Sign with this uid. null: do not sign
     * @param pubEncKey the pub enc keys
     * @param
     * @param armor if OutputStream should be "armored", that means base64 encoded
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws PGPException the pGP exception
     *
     *
     * {@link org.bouncycastle.bcpg.HashAlgorithmTags} {@link
     * org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags}
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    private void setup(final OutputStream cipherTextSink,
                        final String signingUid,
                       final PGPPublicKey pubEncKey,
                       final PGPPublicKey signingPublicKey,
                       final boolean armor) throws
            IOException, PGPException {
        isDoSign = (signingUid != null);

        final OutputStream sink;
        if (armor) {
            this.armoredOutputStream = new ArmoredOutputStream(cipherTextSink);
            sink = this.armoredOutputStream;
        } else {
            sink = cipherTextSink;
        }

        final BcPGPDataEncryptorBuilder dataEncryptorBuilder = new BcPGPDataEncryptorBuilder(
                algorithmSuite.symmetricKeyAlgorithmTags);
        dataEncryptorBuilder.setWithIntegrityPacket(true);

        final PGPEncryptedDataGenerator cPk =
                new PGPEncryptedDataGenerator(dataEncryptorBuilder);

        cPk.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(pubEncKey));

        // this wraps the output stream in an encrypting output stream
        outerEncryptionStream = cPk.open(sink, new byte[1 << 16]);

        if (isDoSign) {

            if (signingPublicKey == null) {
                throw new PGPException(
                        "No suitable public key found for signing with uid: '" + signingUid + "'");
            }
            LOGGER.trace("Signing for uid '{}' with key 0x{}.", signingUid,
                    Long.toHexString(signingPublicKey.getKeyID()));

            final PGPSecretKey pgpSec = config.getSecretKeyRings()
                    .getSecretKey(signingPublicKey.getKeyID());
            if (pgpSec == null) {
                throw new PGPException(
                        "No suitable private key found for signing with uid: '" + signingUid
                                + "' (although found pubkey: " + signingPublicKey.getKeyID() + ")");
            }

            final PGPPrivateKey pgpPrivKey = PGPUtilities.extractPrivateKey(pgpSec,
                    config.getPassphrase(pgpSec.getKeyID())
            );
            signatureGenerator = new PGPSignatureGenerator(
                    new BcPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(),
                            algorithmSuite.hashAlgorithmTags));

            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

            final Iterator<?> userIDs = pgpSec.getPublicKey().getUserIDs();
            if (userIDs.hasNext())

            {
                final PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

                spGen.setSignerUserID(false, (String) userIDs.next());
                signatureGenerator.setHashedSubpackets(spGen.generate());
            }
        }

        compressionStreamGenerator = new PGPCompressedDataGenerator(
                algorithmSuite.compressionAlgorithmTags);
        compressionStream = new BCPGOutputStream(
                compressionStreamGenerator.open(outerEncryptionStream));

        if (isDoSign) {
            signatureGenerator.generateOnePassVersion(false).encode(compressionStream);
        }

        encryptionDataStreamGenerator = new PGPLiteralDataGenerator();
        encryptionDataStream = encryptionDataStreamGenerator
                .open(compressionStream, PGPLiteralData.BINARY, "", new Date(), new byte[1 << 16]);
    }

    @Override
    public void write(int data) throws IOException {
        encryptionDataStream.write(data);

        if (isDoSign) {
            final byte asByte = (byte) (data & 0xff);
            signatureGenerator.update(asByte);
        }
    }


    @Override
    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }


    @Override
    public void write(byte[] buffer, int off, int len) throws IOException {
        encryptionDataStream.write(buffer, 0, len);
        if (isDoSign) {
            signatureGenerator.update(buffer, 0, len);
        }
    }

    @Override
    public void flush() throws IOException {
        encryptionDataStream.flush();
    }

    @Override
    public void close() throws IOException {
        if (!isClosed) {

            encryptionDataStream.flush();
            encryptionDataStream.close();
            encryptionDataStreamGenerator.close();
            if (isDoSign) {

                try {
                    signatureGenerator.generate().encode(compressionStream);  // NOPMD:  Demeter (BC API)
                } catch (PGPException e) {
                    throw new IOException(e);
                }
            }
            compressionStreamGenerator.close();

            outerEncryptionStream.flush();
            outerEncryptionStream.close();

            if (armoredOutputStream != null) {
                armoredOutputStream.flush();
                armoredOutputStream.close();
            }
            isClosed = true;
        }
    }
}
