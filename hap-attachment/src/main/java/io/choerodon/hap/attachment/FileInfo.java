/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment;

import java.io.File;

/**
 * 文件信息.
 * 
 * @author xiaohua
 *
 */
public interface FileInfo {
    /**
     * 得到已经上传的文件.
     * 
     * @return File File对象
     */
    File getFile();

    /**
     * 得到此文件的虚拟路径.
     * 
     * @return 相对于项目的虚拟路径
     */
    String getUrl();

    /**
     * 此文件状态信息.
     * 
     * @return String 状态信息
     */
    String getStatus();

    /**
     * 得到源文件的信息.
     * 
     * @return String 源文件名字
     */
    String getOriginalName();

    /**
     * 得到contenType.
     * @return String contentType
     */
    String getContentType();

}
