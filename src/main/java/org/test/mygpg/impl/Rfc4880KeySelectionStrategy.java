package org.test.mygpg.impl;

import org.bouncycastle.openpgp.*;
import org.test.mygpg.KeyRings;
import org.test.mygpg.KeySelectionStrategy;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Rfc4880KeySelectionStrategy  implements KeySelectionStrategy {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(Rfc4880KeySelectionStrategy.class);

    private final Instant dateOfTimestampVerification;

    /**
     * The date used for key expiration date checks as "now".
     *
     * @return dateOfTimestampVerification
     */
    protected Instant getDateOfTimestampVerification() {
        return dateOfTimestampVerification;
    }

    public Rfc4880KeySelectionStrategy(final Instant dateOfTimestampVerification) {
        this.dateOfTimestampVerification = dateOfTimestampVerification;
    }

    @Override
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.ShortVariable"})
    public Set<PGPPublicKey> validPublicKeysForVerifyingSignatures(String uid,
                                                                   KeyRings keyringConfig) throws PGPException, IOException {

        final Set<PGPPublicKeyRing> publicKeyrings = this
                .publicKeyRingsForUid(PURPOSE.FOR_SIGNING, uid, keyringConfig);

        return publicKeyrings.stream()
                .flatMap(keyring -> StreamSupport.stream(keyring.spliterator(), false))
                .filter(this::isVerificationKey)
                .filter(this::isNotRevoked)
                .filter(this::isNotExpired)
                .collect(Collectors.toSet());
    }

    @Override
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.ShortVariable", "PMD.OnlyOneReturn"})
    public PGPPublicKey selectPublicKey(PURPOSE purpose, String uid, KeyRings keyringConfig)
            throws PGPException, IOException {

        final Set<PGPPublicKeyRing> publicKeyrings = this
                .publicKeyRingsForUid(purpose, uid, keyringConfig);

        final PGPSecretKeyRingCollection secretKeyRings = keyringConfig.getSecretKeyRings();

        switch (purpose) {
            case FOR_SIGNING:
                return publicKeyrings.stream()
                        .flatMap(keyring -> StreamSupport.stream(keyring.spliterator(), false))
                        .filter(this::isVerificationKey)
                        .filter(this::isNotRevoked)
                        .filter(this::isNotExpired)
                        .filter(hasPrivateKey(secretKeyRings))
                        .reduce((a, b) -> b)
                        .orElse(null);

            case FOR_ENCRYPTION:
                return publicKeyrings.stream()
                        .flatMap(keyring -> StreamSupport.stream(keyring.spliterator(), false))
                        .filter(this::isEncryptionKey)
                        .filter(this::isNotRevoked)
                        .filter(this::isNotExpired)
                        .reduce((a, b) -> b)
                        .orElse(null);

            default:
                return null;
        }
    }


    protected Set<PGPPublicKeyRing> publicKeyRingsForUid(final PURPOSE purpose, final String uid,
                                                         KeyRings keyringConfig)
            throws IOException, PGPException {

        Set<PGPPublicKeyRing> keyringsForUid = new HashSet<>();

        final String uidQuery;
        final boolean uidAlreadyInBrackets = uid.matches(".*<.*>.*");
        if (uidAlreadyInBrackets) {
            uidQuery = uid;
        } else {
            uidQuery = "<" + uid + ">";
        }

        final Iterator<PGPPublicKeyRing> keyRings = keyringConfig.getPublicKeyRings()
                .getKeyRings(uidQuery, true, true);

        while (keyRings.hasNext()) {
            keyringsForUid.add(keyRings.next());
        }

        return keyringsForUid;
    }



    protected Predicate<PGPPublicKey> hasPrivateKey(final PGPSecretKeyRingCollection secretKeyRings) {
        return pubKey -> {
            try {
                final boolean hasPrivateKey = secretKeyRings.contains(pubKey.getKeyID());

                if (!hasPrivateKey) {
                    LOGGER.trace("Skipping pubkey {} (no private key found)",
                            Long.toHexString(pubKey.getKeyID()));
                }

                return hasPrivateKey;
            } catch (Exception e) {
                // ignore this for filtering
                LOGGER.debug("Failed to test for private key for pubkey " + pubKey.getKeyID());
                return false;
            }
        };
    }


    protected boolean isNotMasterKey(PGPPublicKey pubKey) {
        return !pubKey.isMasterKey();
    }

    @SuppressWarnings({"PMD.LawOfDemeter"})
    protected boolean isNotExpired(PGPPublicKey pubKey) {
        return !isExpired(pubKey);
    }

    @SuppressWarnings({"PMD.LawOfDemeter"})
    protected boolean isExpired(PGPPublicKey pubKey) {
        // getValidSeconds == 0 means: no expiration date
        boolean hasExpiryDate = pubKey.getValidSeconds() > 0;

        final boolean isExpired;

        if (hasExpiryDate) {
            final Instant expiryDate = pubKey.getCreationTime().toInstant()
                    .plusSeconds(pubKey.getValidSeconds());
            isExpired = expiryDate
                    .isBefore(getDateOfTimestampVerification());

            if (isExpired) {
                LOGGER.trace("Skipping pubkey {} (expired since {})",
                        Long.toHexString(pubKey.getKeyID()), expiryDate.toString());
            }
        } else {
            isExpired = false;
        }

        return isExpired;
    }


    protected boolean isEncryptionKey(PGPPublicKey publicKey) {
        final long keyFlags = extractPublicKeyFlags(publicKey);

        final boolean canEncryptCommunication =
                (keyFlags & PGPKeyFlags.CAN_ENCRYPT_COMMS) == PGPKeyFlags.CAN_ENCRYPT_COMMS;

        final boolean canEncryptStorage =
                (keyFlags & PGPKeyFlags.CAN_ENCRYPT_STORAGE) == PGPKeyFlags.CAN_ENCRYPT_STORAGE;

        return canEncryptCommunication || canEncryptStorage;
    }

    protected boolean isVerificationKey(PGPPublicKey pubKey) {
        final boolean isVerficationKey =
                (extractPublicKeyFlags(pubKey) & PGPKeyFlags.CAN_SIGN) == PGPKeyFlags.CAN_SIGN;

        if (!isVerficationKey) {
            LOGGER.trace("Skipping pubkey {} (no signing key)",
                    Long.toHexString(pubKey.getKeyID()));
        }
        return isVerficationKey;
    }


    public boolean isRevoked(PGPPublicKey pubKey) {
        final boolean hasRevocation = pubKey.hasRevocation();
        if (hasRevocation) {
            LOGGER.trace("Skipping pubkey {} (revoked)",
                    Long.toHexString(pubKey.getKeyID()));
        }
        return hasRevocation;
    }

    protected boolean isNotRevoked(PGPPublicKey publicKey) {
        return !isRevoked(publicKey);
    }

    protected long extractPublicKeyFlags(PGPPublicKey publicKey) {
        long aggregatedKeyFlags = 0;

        final Iterator<PGPSignature> directKeySignatures = publicKey.getSignatures();

        while (directKeySignatures.hasNext()) {
            final PGPSignature signature = directKeySignatures.next();
            final PGPSignatureSubpacketVector hashedSubPackets = signature.getHashedSubPackets();

            final int keyFlags = hashedSubPackets.getKeyFlags();
            aggregatedKeyFlags |= keyFlags;
        }
        return aggregatedKeyFlags;
    }
}
