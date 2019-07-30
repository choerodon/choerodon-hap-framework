package io.choerodon.hap.flexfield.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.flexfield.dto.ColumnName;
import io.choerodon.hap.flexfield.dto.FlexModelColumn;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

public interface IFlexModelColumnService extends IBaseService<FlexModelColumn>, ProxySelf<IFlexModelColumnService> {

    /**
     * 通过表名获取表中列.
     *
     * @param tableName 表名
     * @return 表所包含的列
     */
    List<String> getTableColumn(String tableName);

    List<ColumnName> getTableColumnObj(String tableName);

}