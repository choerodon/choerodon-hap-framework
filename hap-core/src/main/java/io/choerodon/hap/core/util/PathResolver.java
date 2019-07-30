package io.choerodon.hap.core.util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 路径解析器.
 *
 * @author chenjingxiong
 */
public final class PathResolver {

    private PathResolver() {

    }

    public static final String BASE_PATH = "${basePath}";

    public static final String EMPTY_STRING = "";


    /**
     * 解析路径.
     *
     * @param request 请求上下文
     * @param path    路径
     * @return 真实路径
     */
    public static String getRealPath(HttpServletRequest request, String path) {
        if (path.contains(BASE_PATH)) {
            path = path.replace(BASE_PATH, request.getSession().getServletContext().getRealPath("/")).replace("//",
                    File.separator);
        }
        return path;
    }

    /**
     * 解析路径.
     *
     * @param basePath webapp的路径
     * @param path     路径
     * @return 真实路径
     */
    public static String getRealPath(String basePath, String path) {
        if (path.contains(BASE_PATH)) {
            path = path.replace(BASE_PATH, basePath).replace("//", File.separator);
        }
        return path;
    }

    /**
     * 解析路径.
     *
     * @param path 路径
     * @return 相对路径
     */
    public static String getRelativePath(String path) {
        if (path.contains(BASE_PATH)) {
            path = path.replace(BASE_PATH, EMPTY_STRING);
        }
        return path;
    }
}
