/*
 * #{copyright}#
 */
package io.choerodon.hap.security.dto;

/**
 * @author shiliyan
 *
 */
public class Encrypt {
    private String text;
    private String decrypt;

    public String getDecrypt() {
        return decrypt;
    }

    public void setDecrypt(String decrypt) {
        this.decrypt = decrypt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
