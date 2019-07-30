package io.choerodon.hap.system.controllers.sys;


import io.choerodon.hap.core.components.SysConfigManager;
import io.choerodon.hap.core.exception.TokenException;
import io.choerodon.hap.core.util.FormatUtil;
import io.choerodon.hap.system.dto.SysConfig;
import io.choerodon.hap.system.dto.SystemInfo;
import io.choerodon.hap.system.service.ISysConfigService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.redis.Cache;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 系统配置信息.
 *
 * @author hailin.xu@hand-china.com
 */
@Controller
@RequestMapping(value = {"/sys/config", "/api/sys/config"})
public class SysConfigController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SysConfigController.class);

    private static final String SYS_LOGO_FILE_NAME = "logo.png";
    private static final String SYS_FAVICON_FILE_NAME = "favicon.png";
    private static final String SYS_LOGO_PARAMETER_NAME = "logo";
    private static final String SYS_FAVICON_PARAMETER_NAME = "favicon";
    private static final String SYS_LOGO_CUSTOM_PATH = "/resources/upload/image/logo.png";
    private static final String SYS_FAVICON_CUSTOM_PATH = "/resources/upload/image/favicon.png";
    public static final String SYS_FAVICON_CONFIG_CODE = "SYS_FAVICON";
    public static final String SYS_LOGO_CONFIG_CODE = "SYS_LOGO";
    public static final String SYS_TITLE = "SYS_TITLE";


    private static final String MSG_ALERT_UPLOAD_FILE_DIRECTORY_EMPTY = "hap.upload.file.directory.empty";
    private static final String MSG_ALERT_UPLOAD_FILE_TYPE_MISMATCH = "hap.upload.file.type.mismatch";
    private static final String MSG_ALERT_UPLOAD_FILE_SIZE_LIMIT_EXCEEDED = "hap.upload.file.size.limit.exceeded";
    private static final String MSG_ALERT_UPLOAD_SUCCESS = "hap.upload.success";
    private static final String MSG_ALERT_UPLOAD_ERROR = "hap.upload.error";

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysConfigManager sysConfigManager;

    @Value("${file.imageUploadDirectory}")
    private String imageUploadDirectory;

    /**
     * 配置信息查询.
     *
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    @ResponseBody
    public ResponseData getConfig(HttpServletRequest request) {
        return new ResponseData(configService.selectAll());
    }


    /**
     * 配置信息保存.
     *
     * @param config  config
     * @param result  BindingResult
     * @param request HttpServletRequest
     * @return ResponseData
     * @throws TokenException 防篡改token校验异常
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitConfig(@RequestBody List<SysConfig> config, BindingResult result, HttpServletRequest request) throws TokenException {
        getValidator().validate(config, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        for (SysConfig aConfig : config) {
            if (aConfig.getConfigCode().equals("PASSWORD_MIN_LENGTH")) {

                if (Integer.parseInt(aConfig.getConfigValue()) < 6) {
                    aConfig.setConfigValue("6");
                } else if (Integer.parseInt(aConfig.getConfigValue()) > 16) {
                    aConfig.setConfigValue("16");
                }
            }
        }

        return new ResponseData(configService.batchUpdate(config));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/logo/upload", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uploadLogo(HttpServletRequest request) {
        return uploadSystemImage(request, 500, SYS_LOGO_FILE_NAME, SYS_LOGO_PARAMETER_NAME, SysConfigManager.KEY_SYS_LOGO_VERSION);
    }

    @PostMapping(value = "/favicon/upload", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String uploadFaviconLogo(HttpServletRequest request) {
        return uploadSystemImage(request, 100, SYS_FAVICON_FILE_NAME, SYS_FAVICON_PARAMETER_NAME, SysConfigManager.KEY_SYS_FAVICON_VERSION);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/system/info")
    @ResponseBody
    public SystemInfo systemInfo(final HttpServletRequest request) {
        return sysConfigManager.getSystemInfo();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/system/logo/upload")
    @ResponseBody
    ResponseData uploadSystemLogo(HttpServletRequest request) {
        return uploadSystemImages(request, 500, SYS_LOGO_FILE_NAME, SYS_LOGO_PARAMETER_NAME, SysConfigManager.KEY_SYS_LOGO_VERSION, SYS_LOGO_CONFIG_CODE, SYS_LOGO_CUSTOM_PATH);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/system/favicon/upload")
    @ResponseBody
    ResponseData uploadSystemFavicon(HttpServletRequest request) {
        return uploadSystemImages(request, 100, SYS_FAVICON_FILE_NAME, SYS_FAVICON_PARAMETER_NAME, SysConfigManager.KEY_SYS_FAVICON_VERSION, SYS_FAVICON_CONFIG_CODE, SYS_FAVICON_CUSTOM_PATH);
    }

    private ResponseData uploadSystemImages(HttpServletRequest request, int fileSizeWithKB, String fileName, String parameterName, String type, String configCode, String configValue) {
        ResponseData response = new ResponseData();
        Locale locale = LocaleUtils.toLocale(createRequestContext(request).getLocale());
        if (StringUtils.isEmpty(imageUploadDirectory)) {
            response.setSuccess(false);
            response.setMessage(getMessageSource().getMessage(MSG_ALERT_UPLOAD_FILE_DIRECTORY_EMPTY, null, locale));
            return response;
        }
        File uploadFolder = new File(imageUploadDirectory);
        long fileSize = fileSizeWithKB * 1024;
        boolean isMultiPartFiled = request instanceof MultipartHttpServletRequest;
        List<FileItem> items;
        try {
            FileUtils.forceMkdir(uploadFolder);
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(fileSize);
            if (isMultiPartFiled) {
                MultipartFile multipartFile = ((MultipartHttpServletRequest) request).getFile(parameterName);
                if (multipartFile != null) {
                    items = Collections.singletonList(new MultipartFiledByFileItem((multipartFile)));
                } else {
                    items = null;
                }

            } else {
                items = upload.parseRequest(request);
            }
            if (CollectionUtils.isNotEmpty(items)) {
                for (FileItem fi : items) {
                    if (!fi.isFormField()) {
                        String contentType = fi.getContentType();
                        if (StringUtils.contains(contentType, "image/")) {
                            try (InputStream is = fi.getInputStream(); FileOutputStream os = new FileOutputStream(new File(uploadFolder, fileName))) {
                                IOUtils.copy(is, os);
                            }
                            SysConfig sysConfig = configService.selectByCode(configCode);
                            if (sysConfig != null) {
                                sysConfig.setConfigValue(configValue);
                                configService.updateByPrimaryKeySelective(sysConfig);
                                response.setMessage(getMessageSource().getMessage(MSG_ALERT_UPLOAD_SUCCESS, null, locale));
                                return response;
                            }
                        } else {
                            response.setSuccess(Boolean.FALSE);
                            response.setMessage(getMessageSource().getMessage(MSG_ALERT_UPLOAD_FILE_TYPE_MISMATCH, null, locale));
                            return response;
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("upload error", e);
            }
            if (e instanceof FileUploadBase.FileSizeLimitExceededException) {
                response.setSuccess(Boolean.FALSE);
                response.setMessage(getMessageSource().getMessage(MSG_ALERT_UPLOAD_FILE_SIZE_LIMIT_EXCEEDED, new String[]{FormatUtil.formatFileSize(fileSize)}, locale));
                return response;
            }
        }
        response.setSuccess(Boolean.FALSE);
        response.setMessage(getMessageSource().getMessage(MSG_ALERT_UPLOAD_ERROR, null, locale));
        return response;
    }

    private String uploadSystemImage(HttpServletRequest request, int fileSizeWithKB, String fileName, String parameterName, String type) {
        File uploadFolder = new File(request.getServletContext().getRealPath("/") + "/resources/upload");
        Locale locale = RequestContextUtils.getLocale(request);
        long fileSize = fileSizeWithKB * 1024;
        boolean isMultiPartFiled = request instanceof MultipartHttpServletRequest;
        List<FileItem> items;
        try {
            FileUtils.forceMkdir(uploadFolder);
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(fileSize);
            if (isMultiPartFiled) {
                MultipartFile multipartFile = ((MultipartHttpServletRequest) request).getFile(parameterName);
                if (multipartFile != null) {
                    items = Collections.singletonList(new MultipartFiledByFileItem((multipartFile)));
                } else {
                    items = null;
                }

            } else {
                items = upload.parseRequest(request);
            }
            if (CollectionUtils.isNotEmpty(items)) {
                for (FileItem fi : items) {
                    if (!fi.isFormField()) {
                        String contentType = fi.getContentType();
                        if (StringUtils.contains(contentType, "image/")) {
                            try (InputStream is = fi.getInputStream(); FileOutputStream os = new FileOutputStream(new File(uploadFolder, fileName))) {
                                IOUtils.copy(is, os);
                            }
                            String logoVersion = configService.updateSystemImageVersion(type);
                            WebUtils.setSessionAttribute(request, SysConfigManager.SYS_LOGO_VERSION, logoVersion);

                            return "<script>window.parent.uploadSuccess('" + getMessageSource().getMessage(MSG_ALERT_UPLOAD_SUCCESS, null, locale) + "')</script>";
                        } else {
                            return "<script>window.parent.showUploadMessage('" + getMessageSource().getMessage(MSG_ALERT_UPLOAD_FILE_TYPE_MISMATCH, null, locale) + "')</script>";
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("upload error", e);
            }
            if (e instanceof FileUploadBase.FileSizeLimitExceededException) {
                return "<script>window.parent.showUploadMessage('" + getMessageSource().getMessage(MSG_ALERT_UPLOAD_FILE_SIZE_LIMIT_EXCEEDED, new String[]{FormatUtil.formatFileSize(fileSize)}, locale) + "')</script>";
            }
        }
        return "<script>window.parent.showUploadMessage('" + getMessageSource().getMessage(MSG_ALERT_UPLOAD_ERROR, null, locale) + "')</script>";
    }

    public static class MultipartFiledByFileItem implements FileItem {
        MultipartFile multipartFile = null;

        MultipartFiledByFileItem(MultipartFile multipartFile) {
            this.multipartFile = multipartFile;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return multipartFile.getInputStream();
        }

        @Override
        public String getContentType() {
            return multipartFile.getContentType();
        }

        @Override
        public String getName() {
            return multipartFile.getOriginalFilename();
        }

        @Override
        public boolean isInMemory() {
            return false;
        }

        @Override
        public long getSize() {
            return multipartFile.getSize();
        }

        @Override
        public byte[] get() {
            return new byte[0];
        }

        @Override
        public String getString(String s) throws UnsupportedEncodingException {
            return null;
        }

        @Override
        public String getString() {
            return null;
        }

        @Override
        public void write(File file) throws Exception {
            multipartFile.transferTo(file);
        }

        @Override
        public void delete() {

        }

        @Override
        public String getFieldName() {
            return multipartFile.getName();
        }

        @Override
        public void setFieldName(String s) {

        }

        @Override
        public boolean isFormField() {
            return false;
        }

        @Override
        public void setFormField(boolean b) {

        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public FileItemHeaders getHeaders() {
            return null;
        }

        @Override
        public void setHeaders(FileItemHeaders fileItemHeaders) {

        }
    }
}
