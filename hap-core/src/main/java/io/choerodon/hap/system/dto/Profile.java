package io.choerodon.hap.system.dto;

import io.choerodon.base.annotation.Children;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 配置文件对象.
 *
 * @author frank.li
 * @since 2016/6/9.
 */
@Table(name = "sys_profile")
public class Profile extends BaseDTO {

    private static final long serialVersionUID = -3284239490993804271L;

    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_PROFILE_ID = "profileId";
    public static final String FIELD_PROFILE_NAME = "profileName";
    public static final String FIELD_PROFILE_VALUES = "profileValues";

    /**
     * 配置文件描述.
     */
    @NotEmpty
    @Length(max = 240)
    @Where(comparison = Comparison.LIKE)
    private String description;

    /**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    /**
     * 配置文件.
     */
    @Where(comparison = Comparison.LIKE)
    @NotEmpty
    @Length(max = 40)
    private String profileName;

    @Children
    @Transient
    private List<ProfileValue> profileValues;

    public String getDescription() {
        return description;
    }

    public Long getProfileId() {
        return profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public List<ProfileValue> getProfileValues() {
        return profileValues;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setProfileValues(List<ProfileValue> profileValues) {
        this.profileValues = profileValues;
    }
}