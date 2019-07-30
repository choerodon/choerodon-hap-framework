package io.choerodon.hap.security.crypto.encrypt;

/**
 * @author shiliyan
 */
public interface BytesEncryptor {

    /**
     * 加密.
     *
     * @param paramArrayOfByte 二进制密码
     * @return 加密后密码
     */
    byte[] encrypt(byte[] paramArrayOfByte);

    /**
     * 解密.
     *
     * @param paramArrayOfByte 二进制密码
     * @return 解密后密码
     */
    byte[] decrypt(byte[] paramArrayOfByte);
}
