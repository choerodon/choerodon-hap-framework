package io.choerodon.hap.mail;

/**
 * 优先级枚举.
 *
 * @author Clerifen Li
 */
public enum PriorityLevelEnum {
    //VIP优先级
    VIP("VIP"),
    //普通优先级
    NORMAL("NORMAL");

    private String code;

     PriorityLevelEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}