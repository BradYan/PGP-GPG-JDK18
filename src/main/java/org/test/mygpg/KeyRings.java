package org.test.mygpg;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.util.Arrays;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Collections.EMPTY_LIST;

public class KeyRings {

    private final KeyFingerPrintCalculator keyFingerPrintCalculator = new BcKeyFingerprintCalculator();
    private PGPPublicKeyRingCollection publicKeyRings;
    private PGPSecretKeyRingCollection secretKeyRings;

    private char[] passphrase;


    KeyRings(final String password) throws IOException, PGPException {
        if (password == null) {
            throw new IllegalArgumentException("password must not be null");
        }
        passphrase = Arrays.clone(password.toCharArray());

        this.publicKeyRings = new PGPPublicKeyRingCollection(EMPTY_LIST);
        this.secretKeyRings = new PGPSecretKeyRingCollection(EMPTY_LIST);
    }

    public void addPublicKey(byte[] encodedPublicKey) throws IOException, PGPException {

        if (encodedPublicKey == null) {
            throw new IllegalArgumentException("encodedPublicKey must not be null");
        }

        try (
                final InputStream raw = new ByteArrayInputStream(encodedPublicKey);
                final InputStream decoded = org.bouncycastle.openpgp.PGPUtil.getDecoderStream(raw)
        ) {
            PGPPublicKeyRing pgpPub = new PGPPublicKeyRing(decoded, keyFingerPrintCalculator);
            this.publicKeyRings = PGPPublicKeyRingCollection
                    .addPublicKeyRing(this.publicKeyRings, pgpPub);
        }
    }


    public void addSecretKey(byte[] encodedPrivateKey) throws IOException, PGPException {

        if (encodedPrivateKey == null) {
            throw new IllegalArgumentException("encodedPrivateKey must not be null");
        }

        try (
                final InputStream raw = new ByteArrayInputStream(encodedPrivateKey);
                final InputStream decoded = org.bouncycastle.openpgp.PGPUtil
                        .getDecoderStream(raw)
        ) {
            PGPSecretKeyRing pgpPRivate = new PGPSecretKeyRing(decoded, keyFingerPrintCalculator);
            this.secretKeyRings =
                    PGPSecretKeyRingCollection
                            .addSecretKeyRing(this.secretKeyRings, pgpPRivate);
        }
    }

    public KeyFingerPrintCalculator getKeyFingerPrintCalculator() {
        return keyFingerPrintCalculator;
    }

    public PGPPublicKeyRingCollection getPublicKeyRings() {
        return this.publicKeyRings;
    }

    public PGPSecretKeyRingCollection getSecretKeyRings()  {
        return this.secretKeyRings;
    }

    public char[] getPassphrase(long keyId){
        return  Arrays.clone(passphrase);
    }


    public static class ArmoredKeyPair {

        private final String privateKey;
        private final String publicKey;

        private ArmoredKeyPair(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }

        public String privateKey() {
            return privateKey;
        }

        public String publicKey() {
            return publicKey;
        }

        public static ArmoredKeyPair of(String privateKey, String publicKey) {
            return new ArmoredKeyPair(privateKey, publicKey);
        }
    }
}
