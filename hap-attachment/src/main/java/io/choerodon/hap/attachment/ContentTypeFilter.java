/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment;

/**
 * contentType 过滤器.
 * 
 * @author xiaohua
 *
 */
public interface ContentTypeFilter {

    /**
     * 是否上传此文件.<br>
     * * 默认可以上传audio，zip，rar，image，video，doc. <br>
     * . 并且必须后缀名和contentType必须都匹配.
     * 
     * @param orginalName
     *            源文件名
     * @param contentType
     *            上传文件的mimeType
     * @return boolean 是否允许
     */
    boolean isAccept(String orginalName, String contentType);

}
