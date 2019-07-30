package io.choerodon.hap.security.crypto.encrypt;

/**
 * @author shiliyan
 *
 */
public interface TextEncryptor {

    /**
     * 加密.
     *
     * @param  paramString 密码
     * @return 加密后密码
     */
    String encrypt(String paramString);

    /**
     * 解密.
     *
     * @param paramString 密码
     * @return 解密后密码
     */
    String decrypt(String paramString);
}
