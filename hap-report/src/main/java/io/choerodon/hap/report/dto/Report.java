package io.choerodon.hap.report.dto;

import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.JoinColumn;
import io.choerodon.mybatis.common.query.JoinOn;
import io.choerodon.mybatis.common.query.JoinTable;
import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.JoinType;
import javax.validation.constraints.NotEmpty;

/**
 * 报表.
 *
 * @author qiang.zeng
 * @since 2017/9/21
 */
@Table(name = "sys_report")
public class Report extends BaseDTO {

    public static final String FIELD_REPORT_ID = "reportId";
    public static final String FIELD_REPORT_CODE = "reportCode";
    public static final String FIELD_FILE_ID = "fileId";
    public static final String FIELD_FILE_NAME = "fileName";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_DEFAULT_QUERY = "defaultQuery";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Where
    @OrderBy("desc")
    private Long reportId;
    @Column
    @NotEmpty
    @Where(comparison = Comparison.LIKE)
    @Length(max = 50)
    @OrderBy
    private String reportCode;
    @Column
    @JoinTable(name = "reportFileJoin", target = ReportFiles.class, type = JoinType.LEFT, on = {@JoinOn(joinField = ReportFiles.FIELD_FILE_ID)})
    private Long fileId;
    @Transient
    @JoinColumn(joinName = "reportFileJoin", field = ReportFiles.FIELD_NAME)
    private String fileName;
    @Transient
    @JoinColumn(joinName = "reportFileJoin", field = ReportFiles.FIELD_SOURCE_TYPE)
    private String sourceType;
    @Column
    @Where(comparison = Comparison.LIKE)
    @NotEmpty
    @Length(max = 50)
    private String name;
    @Column
    @Length(max = 255)
    private String description;
    @Column
    @Length(max = 1)
    private String defaultQuery;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultQuery() {
        return defaultQuery;
    }

    public void setDefaultQuery(String defaultQuery) {
        this.defaultQuery = defaultQuery;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
