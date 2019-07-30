package io.choerodon.hap.security.permission.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import io.choerodon.mybatis.common.query.Where;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author jialong.zuo@hand-china.com
 */
@Table(name = "sys_permission_table")
public class DataPermissionTable extends BaseDTO {

    public static final String FIELD_TABLE_ID = "tableId";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_DESCRIPTION = "description";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;

    @NotEmpty
    @Length(max = 250)
    @Where
    private String tableName;

    @Length(max = 250)
    @Where
    private String description;

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
