/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 文件控制器.
 * 
 * @author xiaohua
 */
public interface Controller {

    /**
     * 得到保存的文件的Dir.<br>
     * 比如 /home/work/tomcat803/webapps/dsis/attachment/xxxxx/.
     * 
     * @param request
     *            HttpServletRequest
     * @param orginalName
     *            源文件名
     * @return String 文件存放路径
     */
    String getFileDir(HttpServletRequest request, String orginalName);

    /**
     * 重新命名接口.
     * 
     * @param orginalName
     *            比如 test.doc
     * @return String 比如b7fbc509ae6b529e002b697cd3e5d2995.doc
     */
    String newName(String orginalName);

    /**
     *  数据库存储定制方法.
     * 
     * @param request
     *            HttpServletRequest
     * @param file
     *            File
     * @return String 返回数据库存储的路径
     */
    String urlFix(HttpServletRequest request, File file);

}
