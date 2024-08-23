package org.test.mygpg;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.util.io.Streams;
import org.test.mygpg.impl.RequireSpecificSignatureValidationForUserIdsStrategy;
import org.test.mygpg.impl.Rfc4880KeySelectionStrategy;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PGPEncryptDecryptUtil {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider()); // 此处需要类加载,并初始化.
        }
    }

    private static KeyRings keyring(String passphrase,
                                    KeyRings.ArmoredKeyPair armoredKeyPair,
                                    String... recipientsArmoredPublicKey)
            throws IOException, PGPException {
        KeyRings keyring = new KeyRings(passphrase);
        keyring.addSecretKey(armoredKeyPair.privateKey().getBytes(UTF_8));
        keyring.addPublicKey(armoredKeyPair.publicKey().getBytes(UTF_8));
        for (String recipientArmoredPublicKey : recipientsArmoredPublicKey) {
            keyring.addPublicKey(recipientArmoredPublicKey.getBytes(UTF_8));
        }
        return keyring;
    }

    public static String encryptAndSign(String unencryptedMessage,
                                        String senderUserIdEmail,
                                        String senderPassphrase,
                                        KeyRings.ArmoredKeyPair senderArmoredKeyPair,
                                        String receiverUserId,
                                        String receiverArmoredPublicKey)
            throws IOException, PGPException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException {

        KeyRings keyring = keyring(senderPassphrase, senderArmoredKeyPair, receiverArmoredPublicKey);


        KeySelectionStrategy keySelectionStrategy = new Rfc4880KeySelectionStrategy(Instant.now());
        PGPPublicKey recipientEncryptionKey = keySelectionStrategy.selectPublicKey(KeySelectionStrategy.PURPOSE.FOR_ENCRYPTION, receiverUserId, keyring);

        PGPPublicKey signingPublicKey = keySelectionStrategy
                .selectPublicKey(KeySelectionStrategy.PURPOSE.FOR_SIGNING, senderUserIdEmail, keyring);

        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();

        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(encryptedOutputStream);
                OutputStream bouncyGPGOutputStream = PGPEncryptingStream.create(
                        keyring,
                        new PGPAlgorithms(HashAlgorithmTags.SHA256,SymmetricKeyAlgorithmTags.AES_128, CompressionAlgorithmTags.ZLIB),
                        senderUserIdEmail,
                        bufferedOutputStream,
                        signingPublicKey,
                        true,
                        recipientEncryptionKey
                );
        ) {
            Streams.pipeAll(new ByteArrayInputStream(unencryptedMessage.getBytes()), bouncyGPGOutputStream);
        }

        return encryptedOutputStream.toString(UTF_8.name());

    }


    public static String decryptAndVerify(String encryptedMessage,
                                          String receiverPassphrase,
                                          KeyRings.ArmoredKeyPair receiverArmoredKeyPair,
                                          String senderUserIdEmail,
                                          String senderArmoredPublicKey)
            throws IOException, PGPException, NoSuchProviderException {

        KeyRings keyring = keyring(receiverPassphrase, receiverArmoredKeyPair, senderArmoredPublicKey);

        KeySelectionStrategy keySelectionStrategy = new Rfc4880KeySelectionStrategy(Instant.now());
        Set<PGPPublicKey> availableKeys = keySelectionStrategy.validPublicKeysForVerifyingSignatures(senderUserIdEmail, keyring);
        Set<Long> keysForUid = availableKeys.stream().map(key -> key.getKeyID())
                .collect(Collectors.toSet());
        final Map<String, Set<Long>> keyIdsByUid = new HashMap<>();
        keyIdsByUid.put(senderUserIdEmail, keysForUid);
        RequireSpecificSignatureValidationForUserIdsStrategy requireSpecificSignatureValidationForUserIdsStrategy = new RequireSpecificSignatureValidationForUserIdsStrategy(keyIdsByUid);

        DecryptionStreamFactory pgpInputStreamFactory =
                DecryptionStreamFactory.create(
                        keyring,
                        requireSpecificSignatureValidationForUserIdsStrategy);

        ByteArrayOutputStream unencryptedOutputStream = new ByteArrayOutputStream();
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(unencryptedOutputStream);

        ) {

            InputStream bouncyGPGInputStream = pgpInputStreamFactory.wrapWithDecryptAndVerify(new ByteArrayInputStream(encryptedMessage.getBytes(UTF_8)));
            Streams.pipeAll(bouncyGPGInputStream, bufferedOutputStream);
        }

        return unencryptedOutputStream.toString(UTF_8.name());
    }
}
