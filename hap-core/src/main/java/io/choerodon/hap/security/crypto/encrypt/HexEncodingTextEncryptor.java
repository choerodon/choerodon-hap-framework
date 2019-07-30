package io.choerodon.hap.security.crypto.encrypt;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;

/**
 * @author shiliyan
 */
final class HexEncodingTextEncryptor implements TextEncryptor {

    private final BytesEncryptor encryptor;

    HexEncodingTextEncryptor(BytesEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String encrypt(String text) {
        return new String(Hex.encode(this.encryptor.encrypt(Utf8.encode(text))));
    }

    @Override
    public String decrypt(String encryptedText) {
        return Utf8.decode(this.encryptor.decrypt(Hex.decode(encryptedText)));
    }

}
