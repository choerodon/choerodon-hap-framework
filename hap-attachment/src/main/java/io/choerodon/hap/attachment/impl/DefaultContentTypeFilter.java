/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment.impl;

import io.choerodon.hap.attachment.ContentTypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的文件过滤器.
 * 
 * @author xiaohua
 *
 */
public class DefaultContentTypeFilter implements ContentTypeFilter {

    private Map<String, String> extMaps = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(getClass());

    public DefaultContentTypeFilter() {
        init();
    }

    private void init() {
        // image
        extMaps.put("png", "image/png");
        extMaps.put("gif", "image/gif");
        extMaps.put("bmp", "image/bmp");
        extMaps.put("ico", "image/x-ico");
        extMaps.put("jpeg", "image/jpeg");
        extMaps.put("jpg", "image/jpeg");
        // 压缩文件
        extMaps.put("zip", "application/zip");
        extMaps.put("rar", "application/x-rar");
        // doc
        extMaps.put("pdf", "application/pdf");
        extMaps.put("ppt", "application/vnd.ms-powerpoint");
        extMaps.put("xls", "application/vnd.ms-excel");
        extMaps.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        extMaps.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        extMaps.put("doc", "application/msword");
        extMaps.put("doc", "application/wps-office.doc");
        extMaps.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        extMaps.put("txt", "text/plain");
        // 视频
        extMaps.put("mp4", "video/mp4");
        extMaps.put("flv", "video/x-flv");
    }

    public boolean isAccept(String orginalName, String contentType) {
        String ext = null;
        try {
            ext = orginalName.substring(orginalName.lastIndexOf('.') + 1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return extMaps.get(ext) != null 
                    && extMaps.get(ext).equals(contentType);
    }

}
