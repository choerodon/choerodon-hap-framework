package io.choerodon.hap.security.crypto.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.util.EncodingUtils;

/**
 * @author shiliyan
 *
 */
final class AesBytesEncryptor implements BytesEncryptor {
    private final SecretKey secretKey;
    private final Cipher encryptor;
    private final Cipher decryptor;
    private final BytesKeyGenerator ivGenerator;
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int DEFAULT_KEY_SIZE = 128;
    private static final BytesKeyGenerator NULL_IV_GENERATOR = new BytesKeyGenerator() {
        private final byte[] VALUE = new byte[16];

        @Override
        public int getKeyLength() {
            return this.VALUE.length;
        }

        @Override
        public byte[] generateKey() {
            return this.VALUE;
        }
    };

    AesBytesEncryptor(String password, CharSequence salt) {
        this(password, salt, null, DEFAULT_KEY_SIZE);
    }

    AesBytesEncryptor(String password, CharSequence salt, int keysize) {
        this(password, salt, null, keysize);
    }

    AesBytesEncryptor(String password, CharSequence salt, BytesKeyGenerator ivGenerator) {
        this(password, salt, ivGenerator, DEFAULT_KEY_SIZE);
    }

    AesBytesEncryptor(String password, CharSequence salt, BytesKeyGenerator ivGenerator, int keysize) {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), Hex.decode(salt), 1024, keysize);
        SecretKey secretKey = CipherUtils.newSecretKey(KEY_ALGORITHM, keySpec);
        this.secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
        this.encryptor = CipherUtils.newCipher(AES_ALGORITHM);
        this.decryptor = CipherUtils.newCipher(AES_ALGORITHM);
        this.ivGenerator = (ivGenerator != null ? ivGenerator : NULL_IV_GENERATOR);
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        synchronized (this.encryptor) {
            byte[] iv = this.ivGenerator.generateKey();
            CipherUtils.initCipher(this.encryptor, Cipher.ENCRYPT_MODE, this.secretKey, new IvParameterSpec(iv));
            byte[] encrypted = CipherUtils.doFinal(this.encryptor, bytes);
            return this.ivGenerator != NULL_IV_GENERATOR ? EncodingUtils.concatenate(new byte[][] { iv, encrypted })
                    : encrypted;
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes) {
        synchronized (this.decryptor) {
            byte[] iv = iv(encryptedBytes);
            CipherUtils.initCipher(this.decryptor, Cipher.DECRYPT_MODE, this.secretKey, new IvParameterSpec(iv));
            return CipherUtils.doFinal(this.decryptor,
                    this.ivGenerator != NULL_IV_GENERATOR ? encrypted(encryptedBytes, iv.length) : encryptedBytes);
        }
    }

    private byte[] iv(byte[] encrypted) {
        return this.ivGenerator != NULL_IV_GENERATOR
                ? EncodingUtils.subArray(encrypted, 0, this.ivGenerator.getKeyLength())
                : NULL_IV_GENERATOR.generateKey();
    }

    private byte[] encrypted(byte[] encryptedBytes, int ivLength) {
        return EncodingUtils.subArray(encryptedBytes, ivLength, encryptedBytes.length);
    }
}