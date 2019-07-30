package io.choerodon.hap.mail;

/**
 * 邮箱状态枚举.
 *
 * @author Clerifen Li
 */
public enum EmailStatusEnum {
    //成功
    SUCCESS("SUCCESS"),
    //失败
    ERROR("ERROR");

    private String code;

    EmailStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}