package org.test.mygpg;


import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.util.Iterator;

@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.AccessorMethodGeneration", "PMD.LawOfDemeter"})
public final class DecryptionStreamFactory {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(DecryptionStreamFactory.class);


    
    private final PGPContentVerifierBuilderProvider pgpContentVerifierBuilderProvider = new BcPGPContentVerifierBuilderProvider();

    
    private final KeyRings config;

    
    private final SignatureValidationStrategy signatureValidationStrategy;

    private DecryptionStreamFactory(final KeyRings config,
                                    final SignatureValidationStrategy signatureValidationStrategy) {
        this.signatureValidationStrategy = signatureValidationStrategy;
        this.config = config;
    }

    public static DecryptionStreamFactory create(final KeyRings config, final SignatureValidationStrategy signatureValidationStrategy) {
        if (config == null) {
            throw new IllegalArgumentException("keyring config must not be null");
        }
        if (signatureValidationStrategy == null) {
            throw new IllegalArgumentException("signatureValidationStrategy config must not be null");

        }
        return new DecryptionStreamFactory(config, signatureValidationStrategy);
    }

    public InputStream wrapWithDecryptAndVerify(InputStream inputStream)
            throws IOException, NoSuchProviderException {
        LOGGER.trace("Trying to decrypt and verify PGP Encryption.");
        if (inputStream == null) {
            throw new IllegalArgumentException("in config must not be null");
        }

        try {
            final PGPObjectFactory factory = new PGPObjectFactory(PGPUtil.getDecoderStream(inputStream),
                    config.getKeyFingerPrintCalculator());

            return nextDecryptedStream(factory, new SignatureValidatingInputStream.DecryptionState());

        } catch (PGPException e) {
            throw new IOException("Failure decrypting", e);
        }
    }

    /**
     * Handles PGP objects in decryption process by recursively calling itself.
     *
     * @param factory PGPObjectFactory to access the next objects, might be recreated within this
     * method
     * @param state Decryption state, e.g. used for signature validation
     * @throws PGPException the pGP exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws NoSuchProviderException BC provider not registered
     */
    private InputStream nextDecryptedStream(PGPObjectFactory factory,
                                            SignatureValidatingInputStream.DecryptionState state)
            throws PGPException, IOException, NoSuchProviderException {

        Object pgpObj;

        //
        while ((pgpObj = factory.nextObject()) != null) { //NOPMD

            if (pgpObj instanceof PGPEncryptedDataList) {
                LOGGER.trace("Found instance of PGPEncryptedDataList");
                final PGPEncryptedDataList enc = (PGPEncryptedDataList) pgpObj;
                final Iterator<?> encryptedDataObjects = enc.getEncryptedDataObjects();

                if (!encryptedDataObjects.hasNext()) {
                    throw new PGPException("Decryption failed - No encrypted data found!");
                }
                //
                // find the secret key
                //
                PGPPrivateKey sKey = null;
                PGPPublicKeyEncryptedData pbe = null; // NOPMD: mus initialize pbe
                while (sKey == null && encryptedDataObjects.hasNext()) {
                    pbe = (PGPPublicKeyEncryptedData) encryptedDataObjects.next();
                    sKey = PGPUtilities.findSecretKey(config.getSecretKeyRings(), pbe.getKeyID(),
                            config.getPassphrase(pbe.getKeyID()));
                }
                if (pbe == null) {
                    throw new PGPException(
                            "Decryption failed - No public key encrypted data found, aborting");
                }
                if (sKey == null) {
                    // Wrong passphrases already trigger a throw in PGPUtilities.findSecretKey.
                    throw new PGPException(
                            "Decryption failed - No secret key was found in the key ring matching the public key used "
                                    + "to encrypt the file, aborting");
                }

                // decrypt the data

                final InputStream plainText = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));
                final PGPObjectFactory nextFactory = new PGPObjectFactory(plainText,
                        new BcKeyFingerprintCalculator());
                return nextDecryptedStream(nextFactory, state); // NOPMD
            } else if (pgpObj instanceof PGPCompressedData) {
                LOGGER.trace("Found instance of PGPCompressedData");
                final PGPObjectFactory nextFactory = new PGPObjectFactory(
                        ((PGPCompressedData) pgpObj).getDataStream(), config.getKeyFingerPrintCalculator());
                return nextDecryptedStream(nextFactory, state);  // NOPMD
            } else if (pgpObj instanceof PGPOnePassSignatureList) {
                LOGGER.trace("Found instance of PGPOnePassSignatureList");

                if (signatureValidationStrategy.isRequireSignatureCheck()) {

                    state.setSignatureFactory(factory);

                    // verify the signature
                    final PGPOnePassSignatureList onePassSignatures = (PGPOnePassSignatureList) pgpObj;
                    for (PGPOnePassSignature signature : onePassSignatures) {
                        final PGPPublicKey pubKey = config.getPublicKeyRings()
                                .getPublicKey(signature.getKeyID());

                        final boolean isHavePublicKeyForSignatureInKeyring = pubKey != null;
                        if (isHavePublicKeyForSignatureInKeyring) {
                            LOGGER.trace("Signature found, and the matching public key '0x{}' was also found in the keyring.",
                                    Long.toHexString(signature.getKeyID()));
                            signature.init(pgpContentVerifierBuilderProvider, pubKey);
                            state.addSignature(signature);
                        } else {
                            LOGGER.info("Found signature but public key '0x{}' was not found in the keyring.",
                                    Long.toHexString(signature.getKeyID()));
                        }
                    }
                    if (!state.hasVerifiableSignatures()) {
                        throw new PGPException(
                                "Signature checking is required but none of the public keys used to sign the data were found in the keyring'!");
                    }
                } else {
                    LOGGER.trace("Signature check disabled - ignoring contained signature");
                }
            } else if (pgpObj instanceof PGPLiteralData) {
                LOGGER.trace("Found instance of PGPLiteralData");

                final InputStream literalDataInputStream = ((PGPLiteralData) pgpObj).getInputStream();

                if (signatureValidationStrategy.isRequireSignatureCheck()) {
                    if (!state.hasVerifiableSignatures()) {
                        throw new PGPException("Signature checking is required but message was not signed!");
                    }
                    final SignatureValidatingInputStream signatureValidatingStream = new SignatureValidatingInputStream(
                            literalDataInputStream,
                            state, signatureValidationStrategy);
                    return signatureValidatingStream;  // NOPMD: OnlyOneReturn

                } else {
                    return literalDataInputStream; // NOPMD: OnlyOneReturn
                }
            } else {// keep on searching...
                LOGGER.trace("Skipping pgp Object of Type {}", pgpObj.getClass().getSimpleName());
            }
        }
        throw new PGPException("No data found");
    }
}
