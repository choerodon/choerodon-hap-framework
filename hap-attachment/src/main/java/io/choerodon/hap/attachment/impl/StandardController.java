/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment.impl;

import io.choerodon.hap.attachment.Controller;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * standard controller.
 * @author xiaohua
 *
 */
public class StandardController implements Controller {

    private  static final String TOP_PATH = "attachment";
    

    public StandardController() {
    }

    public String getFileDir(HttpServletRequest request, String orginalName) {
        File f = new File(request.getSession().getServletContext()
                .getRealPath(TOP_PATH + File.separator + FileUtils.formatYMD(new Date())) + File.separator);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath() + File.separator;
    }

    public String newName(String orginalName) {
        return UUID.randomUUID().toString();
    }

    @Override
    public String urlFix(HttpServletRequest request, File file) {
        String projectName = request.getContextPath().substring(1, request.getContextPath().length());
        return file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(projectName) + projectName.length());
    }
}
