package io.choerodon.hap.excel.impl;

import io.choerodon.hap.excel.util.TableUtils;
import tk.mybatis.mapper.common.BaseMapper;

import java.sql.Connection;
import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/11/21.
 */
public class ExportStrategy extends ExcelSheetStrategy {

    private BaseMapper queryMapper;

    public ExportStrategy(Connection connection, String tableName) throws ClassNotFoundException {
        super(connection, tableName);
        queryMapper = TableUtils.getBaseMapperByType(TableUtils.getTableClass(tableName), MapperType.Select);

    }

    @SuppressWarnings("unchecked")
    public List<Object> query(Object dto) {
        return queryMapper.select(dto);
    }

    public String getCellType(String column) {

        if (typeMapping.containsKey(column)) {
            return typeMapping.get(column).getSimpleName();
        }

        return null;
    }

    @Override
    protected void cleanData() {
        super.cleanData();
        queryMapper = null;
    }
}
