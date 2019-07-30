/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment.util;

import io.choerodon.hap.attachment.ContentTypeFilter;
import io.choerodon.hap.attachment.FileInfo;
import io.choerodon.hap.attachment.Uploader;
import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.dto.Attachment;
import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.exception.StoragePathNotExsitException;
import io.choerodon.hap.attachment.impl.StandardController;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;

/**
 * 上传工具类.
 *
 * @author hua.xiao@hand-china.com 2016年2月21日
 */
public class UploadUtil {

    public static final String BASE_PATH = "${basePath}";

    protected UploadUtil() {

    }

    public static void initUploaderParams(Uploader uploader, AttachCategory category) throws StoragePathNotExsitException {

        uploader.setCategory(category);
        // 设置文件上传大小0 是默认
        if (category.getAllowedFileSize() != null) {
            uploader.setSingleFileSize(category.getAllowedFileSize());
        }
        String path = category.getCategoryPath();
        // 设置路径
        uploader.setController(new StandardController() {
            @Override
            public String getFileDir(HttpServletRequest request, String orginalName) {
                String newPath = path;
                // 支持配置占位符${bastPath}
                if (newPath.contains(BASE_PATH)) {
                    newPath = newPath.replace(BASE_PATH, request.getSession().getServletContext().getRealPath("/"));
                }
                // 在contextpath和path之间多一个File.separator
                File f = new File(newPath);
                if (!f.exists()) {
                    f.mkdirs();
                }
                return f.getAbsolutePath() + File.separator;
            }

            @Override
            public String urlFix(HttpServletRequest request, File file) {
                return file.getAbsolutePath();
            }
        });
        // 设置可上传后缀名
        if (StringUtils.isNotBlank(category.getAllowedFileType())) {
            String[] allowedExts = category.getAllowedFileType().toLowerCase().split(AttachCategory.TYPE_SEPARATOR);
            uploader.setFilter(new ContentTypeFilter() {
                @Override
                public boolean isAccept(String orginalName, String contentType) {
                    for (String ext : allowedExts) {
                        if (orginalName.toLowerCase().endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 生成SysFile.
     *
     * @param f            FileInfo
     * @param createdBy    创建人
     * @param lastUpdateBy 上次更新人
     * @return SysFile
     */
    public static SysFile genSysFile(FileInfo f, Long createdBy, Long lastUpdateBy) {
        SysFile sysFile = new SysFile();
        sysFile.setFileName(f.getOriginalName());
        sysFile.setFileSize(f.getFile().length());
        sysFile.setUploadDate(new Date());
        sysFile.setCreatedBy(createdBy);
        sysFile.setLastUpdatedBy(lastUpdateBy);
        sysFile.setFilePath(f.getUrl());
        sysFile.setFileType(f.getContentType());
        return sysFile;

    }

    /**
     * 生成附件.
     *
     * @param category  AttachCategory
     * @param sourceKey 业务id
     * @param creatBy   创建者
     * @param updateBy  更新者
     * @return Attachment
     */
    public static Attachment genAttachment(AttachCategory category, String sourceKey, Long creatBy, Long updateBy) {
        Attachment attach = new Attachment();
        attach.setCategoryId(category.getCategoryId());
        attach.setName(category.getSourceType());
        attach.setSourceType(category.getSourceType());
        attach.setStartActiveDate(new Date());
        attach.setEndActiveDate(new Date());
        attach.setSourceKey(sourceKey);
        attach.setCreatedBy(creatBy);
        attach.setLastUpdatedBy(updateBy);
        attach.setStatus(Attachment.NORMAL);
        return attach;
    }

    /**
     * 删除文件.
     *
     * @param path 文件物理路径
     */
    public static void deleteFile(String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
