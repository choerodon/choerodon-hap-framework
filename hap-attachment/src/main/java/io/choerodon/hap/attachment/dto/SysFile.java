package io.choerodon.hap.attachment.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 文件DTO.
 *
 * @author hua.xiao
 */
@Table(name = "sys_file")
public class SysFile extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    /**
     * 对应的附件id.
     */
    @Column
    private Long attachmentId;

    /**
     * 文件名称.
     */
    @Column
    @NotEmpty
    @OrderBy
    private String fileName;

    /**
     * 文件相对项目的虚拟路径.
     */
    @Column
    private String filePath;

    /**
     * 文件大小.
     */
    @Column
    @OrderBy
    private Long fileSize;

    @Transient
    private String fileSizeDesc;

    /**
     * 文件类型.
     */
    @Column
    @OrderBy
    private String fileType;

    /**
     * 上传日期.
     */
    @Column
    @OrderBy()
    private Date uploadDate;

    public SysFile() {
        super();
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFileSizeDesc() {
        return fileSizeDesc;
    }

    public void setFileSizeDesc(String fileSizeDesc) {
        this.fileSizeDesc = fileSizeDesc;
    }
}
