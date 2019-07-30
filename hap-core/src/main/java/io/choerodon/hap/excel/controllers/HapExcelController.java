package io.choerodon.hap.excel.controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.excel.ExcelException;
import io.choerodon.hap.excel.impl.HapExcelExportService;
import io.choerodon.hap.excel.service.IHapExcelImportService;
import io.choerodon.web.controller.BaseController;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2016/11/30
 */
@Controller
@RequestMapping(value = {"/sys", "/api/sys"})
public class HapExcelController extends BaseController {

    @Autowired
    private IHapExcelImportService iImportService;

    @Autowired
    private HapExcelExportService newExportService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/export/template/{tableName}")
    public void exportImportTemplate(@PathVariable String tableName, HttpServletRequest request, HttpServletResponse response) throws ExcelException, SQLException, IOException {
        iImportService.exportExcelTemplate(tableName, response, request);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/import/template/{tableName}")
    public ArrayNode chooseExportColumn(@PathVariable String tableName, HttpServletRequest request, HttpServletResponse response) {
        return newExportService.getAllExportColumn(tableName, request);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/import/{tableName}")
    public void importXLS(HttpServletRequest request, @PathVariable String tableName) throws IOException, ExcelException, SQLException, FileUploadException {
        // 文件上传处理工厂
        FileItemFactory factory = new DiskFileItemFactory();
        // 创建文件上传处理器
        ServletFileUpload upload = new ServletFileUpload(factory);
        for (List<FileItem> items : upload.parseParameterMap(request).values()) {
            for (FileItem item : items) {
                try (InputStream stream = item.getInputStream()) {
                    iImportService.loadExcel(stream, tableName);
                }
            }
        }
    }

}
