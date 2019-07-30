/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment;

/**
 * 上传常量.
 *
 * @author xiaohua
 */
public class UpConstants {

    protected UpConstants() {
    }

    /**
     * 上传成功 SUCCESS.
     */
    public static final String SUCCESS = "UPLOAD_SUCCESS";
    /**
     * IO异常 UPLOAD_IO_ERROR.
     */
    public static final String UPLOAD_IO_ERROR = "UPLOAD_IO_ERROR";
    /**
     * 分片异常 FILE_CHUNK_ERROR.
     */
    public static final String FILE_CHUNK_ERROR = "FILE_CHUNK_ERROR";
    /**
     * 文件上传总个数超出异常 LIMIT_UPLOAD_NUM_ERROR.
     */
    public static final String LIMIT_UPLOAD_NUM_ERROR = "LIMIT_UPLOAD_NUM_ERROR";
    /**
     * 文件上传总大小超出异常 LIMIT_SIZE_MAX_ERROR.
     */
    public static final String ALL_SIZE_MAX_ERROR = "LIMIT_SIZE_MAX_ERROR";


    /**
     * 空文件异常 FILE_NULL_ERROR.
     **/
    public static final String FILE_NULL_ERROR = "FILE_NULL_ERROR";
    /**
     * 文件上传目录异常 FILE_DIR_ERROR.
     **/
    public static final String FILE_DIR_ERROR = "FILE_DIR_ERROR";
    /**
     * 文件上传错误.
     */
    public static final String UPLOAD_ERROR = "UPLOAD_ERROR";
    /**
     * 文件处理错误.
     */
    public static final String FILE_PROCESS_ERROR = "FILE_PROCESS_ERROR";
    /**
     * 空文件异常.
     */
    public static final String FILE_EMPTY_ERROR = "FILE_EMPTY_ERROR";

    /**
     * 文件上传总 单位byte 默认10G ALL_FILE_SIZE = 10737418240.
     **/
    public static final long ALL_FILE_SIZE = 10737418240L;
    /**
     * 单个文件上传 单位byte默认10M SINGLE_FILE_SIZE_MAX = 10485760.
     **/
    public static final long SINGLE_FILE_SIZE_MAX = 10485760;

    /**
     * 没有可用的处理器 NO_PROCESSOR_ERROR.
     **/
    public static final String NO_PROCESSOR_ERROR = null;

    /**
     * 不是文件上传表单.
     **/
    public static final String NOT_FILE_ERROR = "NOT_FILE_ERROR";

    /**
     * 文件分片状态 此次上传只是分片上传 FILE_CHUNK.
     **/
    public static final String FILE_CHUNK = "FILE_CHUNK";
    /**
     * 文件上传编码 UTF-8.
     **/
    public static final String CHARSET_UTF = "UTF-8";

    /**
     * 上传之前 BEFORE_UPLOAD.
     **/
    public static final String BEFORE_UPLOAD = "BEFORE_UPLOAD";
    /**
     * 文件上传之后.<p>
     * 处理之前 .<p>
     * BEFORE_PROCESS
     **/
    public static final String BEFORE_PROCESS = "BEFORE_PROCESS";
    /**
     * 文件处理之后.<p>
     * 文件信息存入数据库之前 .<p>
     * BEFORE_SAVEFILE_TO_DB
     **/
    public static final String BEFORE_SAVEFILE_TO_DB = "BEFORE_SAVEFILE_TO_DB";
    /**
     * 文件信息存入数据库之后. <p>
     * 文件上传到分布式文件系统之前. <p>
     * BEFORE_FILE_TO_NFS
     **/
    public static final String BEFORE_FILE_TO_NFS = "BEFORE_FILE_TO_NFS";
    /**
     * 文件发送到分布式之后.<p>
     * 处理文件 信息存入数据库之前. <p>
     * BEFORE_SAVE_PROFILE_TO_DB
     **/
    public static final String BEFORE_SAVE_PROFILE_TO_DB = "BEFORE_SAVE_PROFILE_TO_DB";
    /**
     * 处理文件信息存入数据库之后.<p>
     * 处理文件上传到文件系统之前. <p>
     * BEFORE_PROFILE_TO_NFS
     **/
    public static final String BEFORE_PROFILE_TO_NFS = "BEFORE_PROFILE_TO_NFS";
    /**
     * 处理文件上传到文件系统之后.<p>
     * BEFORE_FINISHED
     **/
    public static final String BEFORE_FINISHED = "BEFORE_FINISHED";
    /**
     * 默认一次可上传的最大文件数量 MAX_FILE_NUM = 9999.
     **/
    public static final int MAX_FILE_NUM = 9999;


    public static final String ATTACHMENT_SOURCE_TYPE = "sourceType";
    public static final String ATTACHMENT_SOURCE_KEY = "sourceKey";
    /**
     * 附件业务编码为空.
     */
    public static final String ERROR_UPLOAD_SOURCE_TYPE_EMPTY = "msg.warning.upload.sourcetype.empty";
    /**
     * 附件业务编码目录不存在.
     */
    public static final String ERROR_UPLOAD_SOURCE_TYPE_FOLDER_NOT_FOUND = "msg.warning.upload.folder.notfound";
    /**
     * 上传文件为空.
     */
    public static final String ERROR_UPLOAD_FILE_EMPTY_ERROR = "msg.warning.upload.file.empty";
    /**
     * 上传文件类型不正确.
     */
    public static final String ERROR_UPLOAD_FILE_TYPE_MISMATCH = "hap.upload.file.type.mismatch";
    /**
     * 单个文件大小超出错误.
     */
    public static final String ERROR_UPLOAD_FILE_SIZE_ERROR = "hap.upload.file.size.limit.exceeded";
    /**
     * 下载文件不存在.
     */
    public static final String ERROR_DOWNLOAD_FILE_ERROR = "msg.warning.download.file.error";
    /**
     * 不是文件上传表单.
     */
    public static final String ERROR_UPLOAD_NOT_FILE_FORM = "error.upload.not.file.form";
    /**
     * 文件上传总大小超出限制.
     */
    public static final String ERROR_UPLOAD_TOTAL_SIZE_LIMIT_EXCEEDED = "error.upload.total.size.limit.exceeded";
    /**
     * 文件上传总个数超出限制.
     */
    public static final String ERROR_UPLOAD_TOTAL_NUM_LIMIT_EXCEEDED = "error.upload.total.num.limit.exceeded";
    /**
     * 文件上传发生未知错误.
     */
    public static final String ERROR_UPLOAD_UNKNOWN = "error.upload.unknown";
    /**
     * 文件处理发生未知错误.
     */
    public static final String ERROR_UPLOAD_FILE_PROCESS = "error.upload.file.process";


}
