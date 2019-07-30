package io.choerodon.hap.system.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 系统首选项.
 *
 * @author zhangYang
 */
@Table(name = "sys_preferences")
public class SysPreferences extends BaseDTO {

    private static final long serialVersionUID = 1L;

    public static final String FIELD_PREFERENCES_ID = "preferencesId";
    public static final String FIELD_PREFERENCES = "preferences";
    public static final String FIELD_PREFERENCES_VALUE = "preferencesValue";
    public static final String FIELD_USER_ID = "userId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferencesId;

    @NotNull
    private String preferences;

    private String preferencesValue;

    private Long userId;

    public Long getPreferencesId() {
        return preferencesId;
    }

    public void setPreferencesId(Long preferencesId) {
        this.preferencesId = preferencesId;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences == null ? null : preferences.trim();
    }

    public String getPreferencesValue() {
        return preferencesValue;
    }

    public void setPreferencesValue(String preferencesValue) {
        this.preferencesValue = preferencesValue == null ? null : preferencesValue.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}