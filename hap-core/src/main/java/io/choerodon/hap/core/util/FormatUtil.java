package io.choerodon.hap.core.util;

import java.text.DecimalFormat;

/**
 * @author njq.niu@hand-china.com
 */
public class FormatUtil {

    public static String formatFileSize(Long size) {
        if (size == null || size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
