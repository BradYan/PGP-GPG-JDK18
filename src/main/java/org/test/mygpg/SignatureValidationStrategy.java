package org.test.mygpg;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Map;

public interface SignatureValidationStrategy {

    void validateSignatures(PGPObjectFactory factory,
                            Map<Long, PGPOnePassSignature> onePassSignatures)
            throws SignatureException, PGPException, IOException;


    /**
     * @return Iff a signature is required for a document. false: All, even broken(!) signatures are
     * ignored.
     */
    boolean isRequireSignatureCheck();
}
