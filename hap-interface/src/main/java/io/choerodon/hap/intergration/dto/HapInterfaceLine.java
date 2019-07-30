package io.choerodon.hap.intergration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

/**
 * @author xiangyu.qi
 * @since 2016/10/31.
 */
@MultiLanguage
@Table(name = "sys_if_config_line_b")
public class HapInterfaceLine extends BaseDTO {

    @Id
    private String lineId;

    private String headerId;

    @NotEmpty
    @Length(max = 30)
    private String lineCode;

    @NotEmpty
    @Length(max = 200)
    private String iftUrl;

    @NotEmpty
    @Length(max = 1)
    private String enableFlag;

    @MultiLanguageField
    @Length(max = 50)
    private String lineName;

    @MultiLanguageField
    @Length(max = 255)
    private String lineDescription;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String lang;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getIftUrl() {
        return iftUrl;
    }

    public void setIftUrl(String iftUrl) {
        this.iftUrl = iftUrl;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineDescription() {
        return lineDescription;
    }

    public void setLineDescription(String lineDescription) {
        this.lineDescription = lineDescription;
    }
}