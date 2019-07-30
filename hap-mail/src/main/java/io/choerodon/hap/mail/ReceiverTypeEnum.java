package io.choerodon.hap.mail;

/**
 * 邮件接收人类型枚举.
 *
 * @author Clerifen Li
 */
public enum ReceiverTypeEnum {
    //普通收件人
    NORMAL("NORMAL"),
    //抄送
    CC("CC"),
    //密送
    BCC("BCC");

    private String code;

    ReceiverTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}