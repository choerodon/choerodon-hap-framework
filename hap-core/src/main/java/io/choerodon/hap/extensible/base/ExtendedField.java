/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.extensible.base;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class ExtendedField {

    private String fieldName;

    private String jdbcType;

    private String javaType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
}
