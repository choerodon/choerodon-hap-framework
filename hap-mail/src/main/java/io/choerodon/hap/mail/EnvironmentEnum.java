package io.choerodon.hap.mail;

/**
 * 系统环境枚举.
 *
 * @author Clerifen Li
 */
public enum EnvironmentEnum {
    //系统集成
    SIT("SIT"),
    //用户验收
    UAT("UAT"),
    //生产环境
    PROD("PROD");

    private String code;

    EnvironmentEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}