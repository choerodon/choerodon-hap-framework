package io.choerodon.hap.attachment.service.impl;

import io.choerodon.hap.attachment.UpConstants;
import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.service.IAttachCategoryService;
import io.choerodon.hap.attachment.service.IAttachmentProvider;
import io.choerodon.hap.attachment.service.ISysFileService;
import io.choerodon.hap.core.util.FormatUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.choerodon.web.core.impl.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @author jinqin.ma@hand-china.com
 */
@Component
public class AttachmentProvider implements IAttachmentProvider {

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IAttachCategoryService attachCategoryService;

    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 生成UUID.
     *
     * @return UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param sourceType  附件来源类型
     * @param sourceKey   附件索引
     * @param locale      当前语言
     * @param contextPath 上下文路径
     * @return 附件列表页面
     * @throws IOException       IO异常
     * @throws TemplateException 模板转换异常
     */
    public String getAttachListHtml(String sourceType, String sourceKey, Locale locale, String contextPath) throws Exception {
        return getAttachHtml(sourceType, sourceKey, locale, contextPath, true, true);
    }

    /**
     * @param sourceType
     * @param sourceKey
     * @param locale
     * @param contextPath
     * @param enableRemove 是否允许删除
     * @param enableUpload 是否允许上传
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    @Override
    public String getAttachHtml(String sourceType, String sourceKey, Locale locale, String contextPath, boolean enableRemove, boolean enableUpload) throws IOException, TemplateException {
        List<SysFile> files = sysFileService.queryFilesByTypeAndKey(RequestHelper.newEmptyRequest(), sourceType, sourceKey);
        AttachCategory category = attachCategoryService.selectAttachByCode(RequestHelper.newEmptyRequest(), sourceType);
        String html = "";
        if (category != null) {
            Template template = getConfiguration().getTemplate("Upload.ftl");

            files.forEach(f -> {
                f.setFileSizeDesc(FormatUtil.formatFileSize(f.getFileSize()));
            });


            try (StringWriter out = new StringWriter()) {
                Map<String, Object> param = new HashMap<>();
                param.put("file", files);
                param.put("fid", uuid());
                param.put("enableRemove", enableRemove);
                param.put("enableUpload", enableUpload);
                param.put("sourceType", sourceType);
                param.put("sourceKey", sourceKey);
                param.put("type", category.getAllowedFileType());
                param.put("size", category.getAllowedFileSize());
                param.put("unique", category.getIsUnique());
                param.put("filename", messageSource.getMessage("sysfile.filename", null, locale));
                param.put("filetype", messageSource.getMessage("sysfile.filetype", null, locale));
                param.put("filesize", messageSource.getMessage("sysfile.filesize", null, locale));
                param.put("upload", messageSource.getMessage("sysfile.uploaddate", null, locale));
                param.put("delete", messageSource.getMessage("hap.delete", null, locale));
                param.put("contextPath", contextPath);
                template.process(param, out);
                out.flush();
                html = out.toString();

            }
        } else {
            return messageSource.getMessage(UpConstants.ERROR_UPLOAD_SOURCE_TYPE_FOLDER_NOT_FOUND, new String[]{sourceType}, locale);
        }
        return html;
    }

}
