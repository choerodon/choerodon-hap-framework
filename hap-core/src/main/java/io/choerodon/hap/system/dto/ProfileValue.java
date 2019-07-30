package io.choerodon.hap.system.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 配置文件 行对象.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/3/2.
 */
@Table(name = "sys_profile_value")
public class ProfileValue extends BaseDTO {

    private static final long serialVersionUID = -6967222521922622857L;

    public static final String FIELD_PROFILE_VALUE_ID = "profileValueId";
    public static final String FIELD_PROFILE_ID = "profileId";
    public static final String FIELD_LEVEL_ID = "levelId";
    public static final String FIELD_LEVEL_VALUE = "levelValue";
    public static final String FIELD_LEVEL_NAME = "levelName";
    public static final String FIELD_PROFILE_VALUE = "profileValue";

    /**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileValueId;

    /**
     * 配置文件ID.
     */
    private Long profileId;

    /**
     * 层次ID值.
     */
    @NotNull
    private Long levelId;

    /**
     * 层次值.
     */
    @NotEmpty
    @Length(max = 40)
    private String levelValue;

    @Transient
    @Length(max = 40)
    private String levelName;

    /**
     * 配置文件值.
     */
    @Length(max = 80)
    private String profileValue;

    public Long getProfileValueId() {
        return profileValueId;
    }

    public void setProfileValueId(Long profileValueId) {
        this.profileValueId = profileValueId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue == null ? null : levelValue.trim();
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName == null ? null : levelName.trim();
    }

    public String getProfileValue() {
        return profileValue;
    }

    public void setProfileValue(String profileValue) {
        this.profileValue = profileValue == null ? null : profileValue.trim();
    }

}