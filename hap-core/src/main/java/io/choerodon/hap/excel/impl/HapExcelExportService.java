package io.choerodon.hap.excel.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.choerodon.hap.excel.ExcelException;
import io.choerodon.hap.excel.service.IHapExcelExportService;
import io.choerodon.hap.excel.util.TableUtils;
import io.choerodon.web.dto.ResponseData;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/11/20.
 */
@Component
public class HapExcelExportService implements IHapExcelExportService {
    private static final Logger logger = LoggerFactory.getLogger(HapExcelExportService.class);
    private ThreadLocal<Long> index = ThreadLocal.withInitial(() -> 1L);
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private DataSource dataSource;

    @Override
    @SuppressWarnings("unchecked")
    public void exportAndDownloadExcel(Object responseData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, List<String> columns) throws ExcelException, IOException {
        ResponseData rs = (ResponseData) responseData;

        if (rs.getRows().size() == 0) {
            return;
        }
        SXSSFWorkbook wb = new SXSSFWorkbook();

        HapExcelExportImpl hapExcelExport = null;

        try {
            hapExcelExport = new HapExcelExportImpl(wb, dataSource, columns);
            Object r = rs.getRows().get(0);
            HapExcelExportImpl.setExcelHeader(httpServletResponse, httpServletRequest, "exportFile");
            hapExcelExport.createExcelTemplate(TableUtils.getTable(r.getClass()).getName());
            hapExcelExport.fillSheet((List<Object>) rs.getRows(), r.getClass());
            hapExcelExport.write(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ExcelException("导出失败");
        } finally {
            if (hapExcelExport != null) {
                hapExcelExport.close();
            }
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void exportAndDownloadExcelByDataSet(Object responseData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Map<String, String> columns) throws ExcelException, IOException {
        ResponseData rs = (ResponseData) responseData;

        if (rs.getRows().size() == 0) {
            return;
        }
        DataSetExcelExportImpl hapExcelExport = null;
        SXSSFWorkbook wb = new SXSSFWorkbook();
        try {
            hapExcelExport = new DataSetExcelExportImpl(wb, dataSource, columns);
            Object r = rs.getRows().get(0);
            HapExcelExportImpl.setExcelHeader(httpServletResponse, httpServletRequest, "exportFile");
            hapExcelExport.createExcelTemplate(columns);
            hapExcelExport.fillSheet((List<Object>) rs.getRows(), r.getClass());
            hapExcelExport.write(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ExcelException("导出失败");
        } finally {
            if (hapExcelExport != null) {
                hapExcelExport.close();
            }
        }
    }

    public ArrayNode getAllExportColumn(String tableName, HttpServletRequest request) {
        Class parentDto = TableUtils.getTableClass(tableName);
        ObjectMapper columnTree = new ObjectMapper();
        ArrayNode jsonNodes = columnTree.createArrayNode();

        parseClass(parentDto, columnTree, request, jsonNodes, index.get());
        parentDto.getSimpleName();
        index.set(1L);
        return jsonNodes;
    }

    private void parseClass(Class dto, ObjectMapper columnTree, HttpServletRequest request, ArrayNode rootNode, Long parentId) {

        Long id = index.get();

        EntityTable entityTable = TableUtils.getTable(dto);
        String dtoName = dto.getSimpleName();
        Set<EntityColumn> columns = entityTable.getEntityClassColumns();
        ObjectNode objectNode = columnTree.createObjectNode();


        objectNode.put("text", dtoName);
        objectNode.put("value", dtoName);
        objectNode.put("ischecked", false);
        objectNode.put("hasChildren", false);
        objectNode.put("id", id);
        objectNode.put("parentId", id - 1 == 0 ? null : parentId);
        rootNode.add(objectNode);

        int j = 1;
        for (EntityColumn column : columns) {
            String columnName = StringUtil.underlineToCamelhump(column.getColumn().toLowerCase());
            String description = messageSource.getMessage((dtoName + "." + columnName).toLowerCase(), null, RequestContextUtils.getLocale(request));
            if (description.equalsIgnoreCase(dtoName + "." + columnName)) {
                continue;
            }
            ObjectNode node = columnTree.createObjectNode();
            node.put("text", description);
            node.put("value", dtoName + "." + columnName);
            node.put("ischecked", false);
            node.put("id", id + j);
            node.put("parentId", id);
            rootNode.add(node);
            j += 1;
        }
        index.set(id + j);
        for (Class clz : TableUtils.getChildren(dto)) {
            parseClass(clz, columnTree, request, rootNode, id);
        }
    }


}
