package io.choerodon.hap.util.dto;

import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * 语言DTO.
 *
 * @author shengyang.zhou@hand-china.com
 */
@Table(name = "sys_lang_b")
public class Language extends BaseDTO {

    private static final long serialVersionUID = -2978619661646886631L;

    public static final String FIELD_LANG_CODE = "langCode";
    public static final String FIELD_BASE_LANG = "baseLang";
    public static final String FIELD_DESCRIPTION = "description";

    @Id
    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    private String langCode;

    @Where(exclude = true)
    @Length(max = 10)
    private String baseLang = "zh_CN";

    @Where(comparison = Comparison.LIKE)
    @Length(max = 240)
    private String description;

    public String getBaseLang() {
        return baseLang;
    }

    public String getDescription() {
        return description;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setBaseLang(String baseLang) {
        this.baseLang = StringUtils.trimWhitespace(baseLang);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLangCode(String langCode) {
        this.langCode = StringUtils.trimWhitespace(langCode);
    }

}