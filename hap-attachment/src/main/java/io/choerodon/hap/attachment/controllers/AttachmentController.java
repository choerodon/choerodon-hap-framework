package io.choerodon.hap.attachment.controllers;

import io.choerodon.hap.attachment.FileInfo;
import io.choerodon.hap.attachment.UpConstants;
import io.choerodon.hap.attachment.Uploader;
import io.choerodon.hap.attachment.UploaderFactory;
import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.dto.Attachment;
import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.exception.AttachmentException;
import io.choerodon.hap.attachment.exception.FileReadIOException;
import io.choerodon.hap.attachment.exception.UniqueFileMutiException;
import io.choerodon.hap.attachment.service.IAttachCategoryService;
import io.choerodon.hap.attachment.service.ISysFileService;
import io.choerodon.hap.attachment.util.UploadUtil;
import io.choerodon.hap.core.exception.TokenException;
import io.choerodon.hap.core.util.FormatUtil;
import io.choerodon.hap.security.TokenUtils;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 附件管理器.
 *
 * @author xiaohua
 */
@Controller
public class AttachmentController extends BaseController {

    /**
     * 提示信息名.
     */
    private static final String MESSAGE_NAME = "message";

    /**
     * 提示只能上传一个文件
     */
    private static final String MESG_UNIQUE = "Unique";
    /**
     * 提示成功.
     */
    private static final String MESG_SUCCESS = "success";
    /**
     * 提示信息 name.
     **/
    private static final String INFO_NAME = "info";
    /**
     * 附件上传存储目录未分配.
     **/
    private static final String TYPEORKEY_EMPTY = "TYPEORKEY_EMPTY";
    /**
     * sourceType 错误.
     */
    private static final String TYPE_ERROR = "SOURCETYPE_ERROR";
    /**
     * 数据库 错误.
     */
    private static final String DATABASE_ERROR = "DATABASE_ERROR";
    /**
     * 图片mime前缀.
     */
    private static final String IMAGE_MIME_PREFIX = "image";
    /**
     * file对象名.
     */
    private static final String FILE_NAME = "file";
    /**
     * buffer 大小.
     */
    private static final Integer BUFFER_SIZE = 1024;

    /**
     * 图片压缩大小.
     */
    private static final Float COMPRESS_SIZE = 40f;

    /**
     * 进制单位.
     */
    private static final Float BYTE_TO_KB = 1024f;
    /**
     * 文件下载默认编码.
     */
    private static final String ENC = "UTF-8";

    /**
     * 日志记录.
     **/
    private static Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    private IAttachCategoryService categoryService;
    @Autowired
    private ISysFileService fileService;

    @Autowired
    private MessageSource messageSource;

    /**
     * 附件列表.
     *
     * @param request    HttpServletRequest
     * @param sourceType 关联业务code
     * @param sourceKey  关联业务表主健
     * @return ModelAndView 视图(/attach/sys_attach_manage)
     */
    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/sys/attach/manage")
    public ModelAndView manager(HttpServletRequest request, String sourceType, String sourceKey) {
        request.setAttribute("sourceType", sourceType);
        request.setAttribute("sourceKey", sourceKey);
        IRequest requestContext = createRequestContext(request);
        AttachCategory category = categoryService.selectAttachByCode(requestContext, sourceType);
        if (category != null) {
            request.setAttribute("files", fileService.selectFilesByTypeAndKey(requestContext, sourceType, sourceKey));
            request.setAttribute(MESSAGE_NAME, MESG_SUCCESS);
        }
        return new ModelAndView(getViewPath() + "/attach/sys_attach_manage");
    }

    /**
     * 附件上传提交页面.
     *
     * @param request     HttpServletRequest
     * @param locale      当前语言
     * @param contextPath 上下文路径
     * @return 上传结果信息
     * @throws Exception 上传异常
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attach/upload", produces = "application/json;charset=UTF-8")
    public ResponseData upload(HttpServletRequest request, Locale locale, String contextPath) throws Exception {
        Uploader uploader = UploaderFactory.getMutiUploader();
        uploader.init(request);

        String status = uploader.getStatus();
        if (UpConstants.NOT_FILE_ERROR.equals(status)) {
            throw new AttachmentException(UpConstants.ERROR_UPLOAD_NOT_FILE_FORM, UpConstants.ERROR_UPLOAD_NOT_FILE_FORM, new Object[0]);
        }
        if (UpConstants.ALL_SIZE_MAX_ERROR.equals(status)) {
            throw new AttachmentException(UpConstants.ERROR_UPLOAD_TOTAL_SIZE_LIMIT_EXCEEDED, UpConstants.ERROR_UPLOAD_TOTAL_SIZE_LIMIT_EXCEEDED, new Object[0]);
        }
        if (UpConstants.LIMIT_UPLOAD_NUM_ERROR.equals(status)) {
            throw new AttachmentException(UpConstants.ERROR_UPLOAD_TOTAL_NUM_LIMIT_EXCEEDED, UpConstants.ERROR_UPLOAD_TOTAL_NUM_LIMIT_EXCEEDED, new Object[0]);
        }
        if (UpConstants.UPLOAD_ERROR.equals(status)) {
            throw new AttachmentException(UpConstants.ERROR_UPLOAD_UNKNOWN, UpConstants.ERROR_UPLOAD_UNKNOWN, new Object[0]);
        }

        String sourceType = uploader.getParams(UpConstants.ATTACHMENT_SOURCE_TYPE);
        String sourceKey = uploader.getParams(UpConstants.ATTACHMENT_SOURCE_KEY);

        ResponseData response = new ResponseData();
        response.setMessage(messageSource.getMessage("hap.upload_success", null, locale));


        if (StringUtils.isBlank(sourceType)) {
            throw new AttachmentException(UpConstants.ERROR_UPLOAD_SOURCE_TYPE_EMPTY, UpConstants.ERROR_UPLOAD_SOURCE_TYPE_EMPTY, new Object[0]);
        }
        IRequest requestContext = createRequestContext(request);

        AttachCategory category = categoryService.selectAttachByCode(requestContext, sourceType);
        if (category == null) {
            throw new AttachmentException(UpConstants.ERROR_UPLOAD_SOURCE_TYPE_FOLDER_NOT_FOUND, UpConstants.ERROR_UPLOAD_SOURCE_TYPE_FOLDER_NOT_FOUND, new String[]{sourceType});
        }

        // 设置上传参数
        UploadUtil.initUploaderParams(uploader, category);

        List<FileInfo> fileInfoList = uploader.upload();
        //上传过后 重新获取状态
        status = uploader.getStatus();
        if (UpConstants.FILE_PROCESS_ERROR.equals(status)) {
            throw new AttachmentException(UpConstants.ERROR_UPLOAD_FILE_PROCESS, UpConstants.ERROR_UPLOAD_FILE_PROCESS, new Object[0]);
        }

        Attachment attach = UploadUtil.genAttachment(category, sourceKey, requestContext.getUserId(), requestContext.getUserId());
        List<SysFile> sysFileList = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            try {
                SysFile sysFile = UploadUtil.genSysFile(fileInfo, requestContext.getUserId(), requestContext.getUserId());
                // 分类如果是唯一类型
                if (BaseConstants.YES.equals(category.getIsUnique())) {
                    sysFile = fileService.updateOrInsertFile(requestContext, attach, sysFile);
                } else {
                    fileService.insertFileAndAttach(requestContext, attach, sysFile);
                }
                sysFile.setFilePath(null);
                sysFile.setFileSizeDesc(FormatUtil.formatFileSize(sysFile.getFileSize()));
                TokenUtils.generateAndSetToken(TokenUtils.getSecurityKey(request.getSession(false)), sysFile);
                sysFileList.add(sysFile);
            } catch (UniqueFileMutiException ex) {
                response.setSuccess(false);
                response.setMessage(messageSource.getMessage("hap.mesg_unique", null, locale));
                break;
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("database error", e);
                }
                File file = fileInfo.getFile();
                if (file.exists()) {
                    file.delete();
                }
                response.setSuccess(false);
                break;
            }
        }
        response.setRows(sysFileList);
        return response;
    }

    /**
     * 文件删除.
     *
     * @param request HttpServletRequest
     * @param fileId  文件id
     * @param token   token
     * @return Map 结果对象
     * @throws TokenException 乐观锁检查异常
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attach/remove")
    public Map<String, Object> remove(HttpServletRequest request, String fileId, String token) throws TokenException {

        Map<String, Object> response = new HashMap<>(16);
        IRequest requestContext = createRequestContext(request);
        SysFile file = new SysFile();
        file.setFileId(Long.valueOf(fileId));
        file.set_token(token);
        TokenUtils.checkToken(request.getSession(false), file);
        fileService.delete(requestContext, file);
        response.put(MESSAGE_NAME, MESG_SUCCESS);
        return response;
    }

    /**
     * 具体查看某个附件.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param fileId   文件id
     * @throws FileReadIOException 文件读取IO异常
     */
    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/sys/attach/file/download")
    public void download(HttpServletRequest request, HttpServletResponse response, String fileId, String token) throws Exception {
        IRequest requestContext = createRequestContext(request);
        SysFile sysFile = fileService.selectByPrimaryKey(requestContext, Long.valueOf(fileId));
        sysFile.set_token(token);
        TokenUtils.checkToken(request.getSession(false), sysFile);
        File file = new File(sysFile.getFilePath());
        if (file.exists()) {
            response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(sysFile.getFileName(), ENC) + "\"");
            response.setContentType(sysFile.getFileType() + ";charset=" + ENC);
            response.setHeader("Accept-Ranges", "bytes");
            int fileLength = (int) file.length();
            response.setContentLength(fileLength);
            if (fileLength > 0) {
                writeFileToResp(response, file);
            }
        } else {
            throw new AttachmentException(UpConstants.ERROR_DOWNLOAD_FILE_ERROR, UpConstants.ERROR_DOWNLOAD_FILE_ERROR, new Object[0]);
        }
    }

    /**
     * 查看某个附件.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param fileId   文件id
     * @param compress 是否压缩
     * @throws FileReadIOException IO exception
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attach/file/view")
    public void view(HttpServletRequest request, HttpServletResponse response, String fileId, String compress) throws Exception {
        IRequest requestContext = createRequestContext(request);
        SysFile sysFile = fileService.selectByPrimaryKey(requestContext, Long.valueOf(fileId));
        try {
            // 在contextPath和path之间多一个File.separator
            File file = new File(sysFile.getFilePath());
            if (file.exists()) {
                response.setHeader("cache-control", "must-revalidate");
                response.setHeader("pragma", "public");
                response.setHeader("Content-Type", sysFile.getFileType());
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-disposition",
                        "attachment;" + processFileName(request, sysFile.getFileName()));

                int fileLength = (int) file.length();
                response.setContentLength(fileLength);
                if (fileLength > 0) {
                    if (StringUtils.isNotBlank(compress) && BaseConstants.YES.equals(compress)) {
                        try (OutputStream os = response.getOutputStream()) {
                            Thumbnails.of(file).scale(getCompressPercent(fileLength)).toOutputStream(os);
                            os.flush();
                        }
                    } else {
                        writeFileToResp(response, file);
                    }
                }
            } else {
                throw new AttachmentException(UpConstants.ERROR_DOWNLOAD_FILE_ERROR, UpConstants.ERROR_DOWNLOAD_FILE_ERROR, new Object[0]);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new FileReadIOException();
        }
    }

    private String processFileName(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        String userAgent = request.getHeader("User-Agent");
        String new_filename = URLEncoder.encode(filename, "UTF8");
        String rtn = "filename=\"" + new_filename + "\"";
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if (userAgent.contains("msie")) {
                rtn = "filename=\"" + new String(filename.getBytes("GB2312"), "ISO-8859-1") + "\"";
            } else if (userAgent.contains("safari") || userAgent.contains("applewebkit")) {
                rtn = "filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
            } else if (userAgent.contains("opera") || userAgent.contains("mozilla")) {
                rtn = "filename*=UTF-8''" + new_filename;
            }
        }
        return rtn;
    }

    /**
     * 将文件对象的流写入Response对象.
     *
     * @param response HttpServletResponse
     * @param file     File
     * @throws FileNotFoundException 找不到文件异常
     * @throws IOException           IO异常
     */
    private void writeFileToResp(HttpServletResponse response, File file) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        try (InputStream inStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int readLength;
            while (((readLength = inStream.read(buf)) != -1)) {
                outputStream.write(buf, 0, readLength);
            }
            outputStream.flush();

        }
    }

    /**
     * 得到压缩比压缩大约至40KB的样子.
     *
     * @param len 图片文件长度
     * @return float 图片压缩比
     */
    private float getCompressPercent(long len) {
        return (float) len / (BYTE_TO_KB * COMPRESS_SIZE);
    }

}
