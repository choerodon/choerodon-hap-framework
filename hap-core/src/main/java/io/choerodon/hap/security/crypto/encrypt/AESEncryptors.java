package io.choerodon.hap.security.crypto.encrypt;

import org.springframework.security.crypto.keygen.KeyGenerators;

/**
 * @author shiliyan
 */
public class AESEncryptors {
    public AESEncryptors() {
    }

    public static TextEncryptor queryableText(CharSequence password, CharSequence salt) {
        return new HexEncodingTextEncryptor(new AesBytesEncryptor(password.toString(), salt));
    }

    public static TextEncryptor queryableText(CharSequence password, CharSequence salt, int keysize) {
        return new HexEncodingTextEncryptor(new AesBytesEncryptor(password.toString(), salt, keysize));
    }

    public static BytesEncryptor standard(CharSequence password, CharSequence salt) {
        return new AesBytesEncryptor(password.toString(), salt, KeyGenerators.secureRandom(16));
    }

    public static BytesEncryptor standard(CharSequence password, CharSequence salt, int keysize) {
        return new AesBytesEncryptor(password.toString(), salt, KeyGenerators.secureRandom(16), keysize);
    }

    public static TextEncryptor text(CharSequence password, CharSequence salt, int keysize) {
        return new HexEncodingTextEncryptor(standard(password, salt, keysize));
    }

    private String password;
    private String salt;
    private int keySize;

    private TextEncryptor textEncryptor;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public static TextEncryptor noOpText() {
        return NO_OP_TEXT_INSTANCE;
    }

    public String encrypt(String text) {
        createTextEncryptor();
        return textEncryptor.encrypt(text);
    }

    public String decrypt(String encryptedText) {
        createTextEncryptor();
        return textEncryptor.decrypt(encryptedText);
    }

    private void createTextEncryptor() {
        if (textEncryptor == null) {
            textEncryptor = queryableText(this.password, this.salt, this.keySize);
        }
    }

    private static final TextEncryptor NO_OP_TEXT_INSTANCE = new NoOpTextEncryptor();

    /**
     * @author shiliyan
     */
    private static final class NoOpTextEncryptor implements TextEncryptor {

        @Override
        public String encrypt(String text) {
            return text;
        }

        @Override
        public String decrypt(String encryptedText) {
            return encryptedText;
        }

    }
}
