/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment;

import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.exception.AttachmentException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 上传文件规范.
 *
 * @author xiaohua
 */
public interface Uploader {

    /**
     * 设置 文件过滤器.
     *
     * @param filter 文件后缀名过滤器
     */
    void setFilter(ContentTypeFilter filter);

    /**
     * 设置 文件类别.
     *
     * @param category
     */
    void setCategory(AttachCategory category);

    /**
     * 设置控制器.
     *
     * @param controller 文件命名存放控制接口
     */
    void setController(Controller controller);

    /**
     * 添加文件处理器.
     *
     * @param processor 文件后续处理器
     */
    void addFileProcessor(FileProcessor processor);

    /**
     * 初始化上传组件，解析参数，分离文件类型，初始化相关子组件.<br>
     * 使用的时候必须先初始化.
     *
     * @param request HttpServletRequest
     */
    void init(HttpServletRequest request);

    /**
     * 上传接口.
     *
     * @return 返回FileInfo类型list
     * @throws AttachmentException 文件上传异常
     */
    List<FileInfo> upload() throws AttachmentException;

    /**
     * 设置参数.
     *
     * @param key Param的key
     * @return String Param的value
     */
    String getParams(String key);

    /**
     * 设置参数.
     *
     * @param key   RequestAttribute的key
     * @param value RequestAttribute的value
     */
    void setParams(String key, String value);

    /**
     * 单个可上传文件大小.
     *
     * @param singleFileSize 文件大小
     */
    void setSingleFileSize(long singleFileSize);

    /**
     * 可上传总文件大小.
     *
     * @param allFileSize 一次性总文件大小
     */
    void setAllFileSize(long allFileSize);

    /**
     * 一次上传最大文件数量.
     *
     * @param maxFileNum 一次性文件上传数量
     */
    void setMaxFileNum(int maxFileNum);

    /**
     * 返回此次操作结果，成功则是success.
     *
     * @return String 结果状态 UpConstants
     */
    String getStatus();

    /**
     * 返回请求对象.
     *
     * @return HttpServletRequest
     */
    HttpServletRequest getRequest();
}