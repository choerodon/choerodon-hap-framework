package io.choerodon.hap.mail;

/**
 * 消息类型枚举.
 *
 * @author Clerifen Li
 */
public enum MessageTypeEnum {
    //邮件
    EMAIL("EMAIL"),
    //短信
    SMS("SMS"),
    //站内信
    SITE("SITE");

    private String code;

    MessageTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}