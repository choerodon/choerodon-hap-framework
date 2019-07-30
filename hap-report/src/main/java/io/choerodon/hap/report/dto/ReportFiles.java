package io.choerodon.hap.report.dto;

import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * 报表文件.
 *
 * @author qiang.zeng
 * @since 2017/9/21
 */
@Table(name = "sys_report_file")
public class ReportFiles extends BaseDTO {

    public static final String FIELD_FILE_ID = "fileId";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_PARAMS = "params";
    public static final String FIELD_SOURCE_TYPE = "sourceType";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column
    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    @Length(max = 50)
    private String name;

    @Column
    private String content;

    @Column
    @Length(max = 255)
    private String params;

    @Column
    @Length(max = 20)
    private String sourceType;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}