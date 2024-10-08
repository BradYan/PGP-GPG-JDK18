package org.test.mygpg;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.ShortVariable")
final class SignatureValidatingInputStream extends FilterInputStream {

    private final DecryptionState state;
    private final SignatureValidationStrategy signatureValidationStrategy;

    /**
     * Creates a <code>SignatureValidatingInputStream</code> by assigning the  argument
     * <code>inputStream</code> to the field <code>this.inputStream</code> so as to remember it for
     * later use.
     *
     * @param inputStream the underlying input stream, or <code>null</code> if this instance is to be
     * created without an underlying stream.
     */
    SignatureValidatingInputStream(InputStream inputStream, DecryptionState state,
                                   SignatureValidationStrategy signatureValidationStrategy) {
        super(inputStream);
        this.state = state;
        this.signatureValidationStrategy = signatureValidationStrategy;
    }

    @Override
    public int read() throws IOException {
        final int data = super.read();
        final boolean endOfStream = data == -1;
        if (endOfStream) {
            validateSignature();
        } else {
            state.updateOnePassSignatures((byte) data);
        }
        return data;
    }

    @Override
    public int read( byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read( byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);

        final boolean endOfStream = read == -1;
        if (endOfStream) {
            validateSignature();
        } else {
            state.updateOnePassSignatures(b, off, read);
        }
        return read;
    }

    /**
     * Ensure that at least ONE signature is valid.
     *
     * @throws IOException No valid signature found
     */
    private void validateSignature() throws IOException {
        try {
            signatureValidationStrategy
                    .validateSignatures(state.getSignatureFactory(), state.getOnePassSignatures());
        } catch (PGPException | SignatureException e) {
            throw new IOException(e.getMessage(), e);
        }

    }

    @Override
    public long skip(long n) throws IOException {
        throw new UnsupportedOperationException("Skipping not supported");
    }

    @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException("mark not supported");
    }

    @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException("reset not supported");
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    final static class DecryptionState {

        private final Map<Long, PGPOnePassSignature> onePassSignatures = new HashMap<>();
        private PGPObjectFactory signatureFactory;

        @SuppressWarnings("PMD.DefaultPackage")
        PGPObjectFactory getSignatureFactory() {
            return signatureFactory;
        }

        @SuppressWarnings("PMD.DefaultPackage")
        void setSignatureFactory(PGPObjectFactory signatureFactory) {
            this.signatureFactory = signatureFactory;
        }


        @SuppressWarnings("PMD.DefaultPackage")
        void updateOnePassSignatures(byte data) {
            for (PGPOnePassSignature sig : onePassSignatures.values()) {
                sig.update(data);
            }
        }

        @SuppressWarnings("PMD.DefaultPackage")
        void updateOnePassSignatures(byte[] b, int off, int len) {
            for (PGPOnePassSignature sig : onePassSignatures.values()) {
                sig.update(b, off, len);
            }
        }

        @SuppressWarnings("PMD.DefaultPackage")
        Map<Long, PGPOnePassSignature> getOnePassSignatures() {
            return onePassSignatures;
        }

        /*
         * @pre: the public key for the keyId is in our keyring.
         */
        @SuppressWarnings("PMD.DefaultPackage")
        void addSignature(PGPOnePassSignature signature) {
            onePassSignatures.put(signature.getKeyID(), signature);
        }

        @SuppressWarnings("PMD.DefaultPackage")
        int numVerifiableSignatures() {
            return onePassSignatures.size();
        }

        @SuppressWarnings("PMD.DefaultPackage")
        boolean hasVerifiableSignatures() {
            return numVerifiableSignatures() > 0;
        }
    }
}

