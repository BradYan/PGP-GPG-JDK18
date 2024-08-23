package org.test.mygpg;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import java.util.Iterator;

public final class PGPUtilities {

    // No instances
    private PGPUtilities() {
    }

    /**
     * Find secret key.
     *
     * @param pgpSec the pgp sec
     * @param keyID the key id
     * @param pass the pass
     *
     * @return the decrypted secret key
     *
     * @throws PGPException the pGP exception
     */
    @SuppressWarnings({"PMD.UseVarargs", "PMD.OnlyOneReturn"})

    public static PGPPrivateKey findSecretKey(final PGPSecretKeyRingCollection pgpSec,
                                              final long keyID, final char[] pass)
            throws PGPException {

        final PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

        if (pgpSecKey == null) {
            return null;
        }
        return extractPrivateKey(pgpSecKey, pass);
    }

    /**
     * Decrypt an encrypted PGP secret key.
     *
     * @param encryptedKey An encrypted key
     * @param passphrase The passphrase for the key
     *
     * @return the decrypted secret key
     *
     * @throws PGPException E.g. wrong passphrase
     */
    @SuppressWarnings("PMD.UseVarargs")
    public static PGPPrivateKey extractPrivateKey(PGPSecretKey encryptedKey, final char[] passphrase)
            throws PGPException {

        PGPDigestCalculatorProvider calcProvider = new JcaPGPDigestCalculatorProviderBuilder()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME).build();

        PBESecretKeyDecryptor decryptor = new JcePBESecretKeyDecryptorBuilder(
                calcProvider).setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build(passphrase);

        return encryptedKey.extractPrivateKey(decryptor);
    }

    /**
     * Extracts the public key with UID {@code publicKeyUid} from key ring collection {@code
     * publicKeyRings}.
     *
     * @param publicKeyUid the public key uid, e.g. sender@example.com
     * @param publicKeyRings the public key rings
     *
     * @return the PGP public key ring containing the userId
     *
     * @throws PGPException E.g. multiple keyrings for same uid OR key not found in keyrings
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public static PGPPublicKeyRing extractPublicKeyRingForUserId(final String publicKeyUid,
                                                                 final PGPPublicKeyRingCollection publicKeyRings)
            throws PGPException {
        if (publicKeyUid == null) {
            throw new IllegalArgumentException("publicKeyUid must not be null");
        }
        if (publicKeyRings == null) {
            throw new IllegalArgumentException("publicKeyRings must not be null");
        }

        // the true parameter indicates, that partial matching of the publicKeyUid is enough.
        final Iterator<?> keyRings = publicKeyRings.getKeyRings("<" + publicKeyUid + ">", true);
        PGPPublicKeyRing returnKeyRing = null;
        while (keyRings.hasNext()) {
            final Object currentKeyRing = keyRings.next();
            if (currentKeyRing instanceof PGPPublicKeyRing) {
                if (returnKeyRing == null) {
                    returnKeyRing = (PGPPublicKeyRing) currentKeyRing;
                } else {
                    throw new PGPException("Multiple public key rings found for UID '" + publicKeyUid + "'!");
                }
            }
        }
        if (returnKeyRing == null) {
            throw new PGPException("No public key ring found for UID '" + publicKeyUid + "'!");
        }

        return returnKeyRing;
    }


}
