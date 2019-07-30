package script.db

databaseChangeLog(logicalFilePath: "2016-06-09-init-table-migration.groovy") {
    changeSet(author: "hailor", id: "20160609-hailor-2") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_CODE_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_CODE_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "CODE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "代码ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_B_PK")
                }
            } else {
                column(name: "CODE_ID", type: "bigint", remarks: "代码ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_B_PK")
                }
            }

            if (!helper.isPostgresql()) {
                column(name: "CODE", type: "varchar(30)", remarks: "快码类型") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_CODE_B_U1")
                }
            } else {
                column(name: "CODE", type: "varchar(30)", remarks: "快码类型") {
                    constraints(nullable: "false")
                }
            }
            column(name: "DESCRIPTION", type: 'varchar(240)', remarks: "快码类型描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(30)")
            column(name: "ATTRIBUTE1", type: "varchar(240)")
            column(name: "ATTRIBUTE2", type: "varchar(240)")
            column(name: "ATTRIBUTE3", type: "varchar(240)")
            column(name: "ATTRIBUTE4", type: "varchar(240)")
            column(name: "ATTRIBUTE5", type: "varchar(240)")
            column(name: "ATTRIBUTE6", type: "varchar(240)")
            column(name: "ATTRIBUTE7", type: "varchar(240)")
            column(name: "ATTRIBUTE8", type: "varchar(240)")
            column(name: "ATTRIBUTE9", type: "varchar(240)")
            column(name: "ATTRIBUTE10", type: "varchar(240)")
            column(name: "ATTRIBUTE11", type: "varchar(240)")
            column(name: "ATTRIBUTE12", type: "varchar(240)")
            column(name: "ATTRIBUTE13", type: "varchar(240)")
            column(name: "ATTRIBUTE14", type: "varchar(240)")
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "CODE_ID,CODE", tableName: "SYS_CODE_B", constraintName: "SYS_CODE_B_U1")
        }

        createTable(tableName: "SYS_CODE_TL") {
            column(name: "CODE_ID", type: "bigint", remarks: "代码ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "快码描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(30)")
            column(name: "ATTRIBUTE1", type: "varchar(240)")
            column(name: "ATTRIBUTE2", type: "varchar(240)")
            column(name: "ATTRIBUTE3", type: "varchar(240)")
            column(name: "ATTRIBUTE4", type: "varchar(240)")
            column(name: "ATTRIBUTE5", type: "varchar(240)")
            column(name: "ATTRIBUTE6", type: "varchar(240)")
            column(name: "ATTRIBUTE7", type: "varchar(240)")
            column(name: "ATTRIBUTE8", type: "varchar(240)")
            column(name: "ATTRIBUTE9", type: "varchar(240)")
            column(name: "ATTRIBUTE10", type: "varchar(240)")
            column(name: "ATTRIBUTE11", type: "varchar(240)")
            column(name: "ATTRIBUTE12", type: "varchar(240)")
            column(name: "ATTRIBUTE13", type: "varchar(240)")
            column(name: "ATTRIBUTE14", type: "varchar(240)")
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }

    }

    changeSet(author: "hailor", id: "20160609-hailor-3") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_CODE_VALUE_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_CODE_VALUE_B") {

            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "CODE_VALUE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_VALUE_B_PK")
                }
            } else {
                column(name: "CODE_VALUE_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_VALUE_B_PK")
                }
            }

            column(name: "CODE_ID", type: "bigint")
            column(name: "VALUE", type: "varchar(150)", remarks: "快码值")
            column(name: "MEANING", type: "varchar(150)", remarks: "快码意思")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "快码描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(30)")
            column(name: "ATTRIBUTE1", type: "varchar(240)")
            column(name: "ATTRIBUTE2", type: "varchar(240)")
            column(name: "ATTRIBUTE3", type: "varchar(240)")
            column(name: "ATTRIBUTE4", type: "varchar(240)")
            column(name: "ATTRIBUTE5", type: "varchar(240)")
            column(name: "ATTRIBUTE6", type: "varchar(240)")
            column(name: "ATTRIBUTE7", type: "varchar(240)")
            column(name: "ATTRIBUTE8", type: "varchar(240)")
            column(name: "ATTRIBUTE9", type: "varchar(240)")
            column(name: "ATTRIBUTE10", type: "varchar(240)")
            column(name: "ATTRIBUTE11", type: "varchar(240)")
            column(name: "ATTRIBUTE12", type: "varchar(240)")
            column(name: "ATTRIBUTE13", type: "varchar(240)")
            column(name: "ATTRIBUTE14", type: "varchar(240)")
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "CODE_ID,VALUE", tableName: "SYS_CODE_VALUE_B", constraintName: "SYS_CODE_VALUE_B_U1")
        } else {
            addUniqueConstraint(columnNames: "CODE_VALUE_ID,CODE_ID,VALUE", tableName: "SYS_CODE_VALUE_B", constraintName: "SYS_CODE_VALUE_B_U1")
        }

        createTable(tableName: "SYS_CODE_VALUE_TL") {
            column(name: "CODE_VALUE_ID", type: "bigint", remarks: "代码ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "MEANING", type: "varchar(150)", remarks: "快码意思")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "快码描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(30)")
            column(name: "ATTRIBUTE1", type: "varchar(240)")
            column(name: "ATTRIBUTE2", type: "varchar(240)")
            column(name: "ATTRIBUTE3", type: "varchar(240)")
            column(name: "ATTRIBUTE4", type: "varchar(240)")
            column(name: "ATTRIBUTE5", type: "varchar(240)")
            column(name: "ATTRIBUTE6", type: "varchar(240)")
            column(name: "ATTRIBUTE7", type: "varchar(240)")
            column(name: "ATTRIBUTE8", type: "varchar(240)")
            column(name: "ATTRIBUTE9", type: "varchar(240)")
            column(name: "ATTRIBUTE10", type: "varchar(240)")
            column(name: "ATTRIBUTE11", type: "varchar(240)")
            column(name: "ATTRIBUTE12", type: "varchar(240)")
            column(name: "ATTRIBUTE13", type: "varchar(240)")
            column(name: "ATTRIBUTE14", type: "varchar(240)")
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }
    }

    changeSet(author: "hailor", id: "20160609-hailor-7") {
        createTable(tableName: "SYS_LANG_B") {
            column(name: "LANG_CODE", type: "varchar(10)", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_LANG_B_PK")
            }
            column(name: "BASE_LANG", type: "varchar(10)", remarks: "基语言")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }


    changeSet(author: "hailor", id: "20160609-hailor-8") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_LOV_S', startValue: "10001")
        }
        createTable(tableName: "SYS_LOV") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "LOV_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_LOV_PK")
                }
            } else {
                column(name: "LOV_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_LOV_PK")
                }
            }

            if (!helper.isPostgresql()) {
                column(name: "CODE", type: "varchar(80)", remarks: "LOV的code") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_LOV_U1")
                }
            } else {
                column(name: "CODE", type: "varchar(80)", remarks: "LOV的code") {
                    constraints(nullable: "false")
                }
            }

            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "SQL_ID", type: "varchar(225)", remarks: "SQL ID")
            column(name: "VALUE_FIELD", type: "varchar(80)", remarks: " VALUE_FIELD")
            column(name: "TEXT_FIELD", type: "varchar(80)", remarks: "TEXT_FIELD")
            column(name: "TITLE", type: "varchar(225)", remarks: "标题")
            column(name: "WIDTH", type: "decimal(20,0)", remarks: "宽度")
            column(name: "HEIGHT", type: "decimal(20,0)", remarks: "高度")
            column(name: "PLACEHOLDER", type: "varchar(80)", remarks: "提示")
            column(name: "DELAY_LOAD", type: "varchar(1)", defaultValue: "N", remarks: "是否延迟加载")
            column(name: "NEED_QUERY_PARAM", type: "varchar(1)", defaultValue: "N", remarks: "是否需要查询条件")
            column(name: "EDITABLE", type: "varchar(1)", defaultValue: "N", remarks: "是否可编辑")
            column(name: "CAN_POPUP", type: "varchar(1)", defaultValue: "Y", remarks: "是否可弹出")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "LOV_ID,CODE", tableName: "SYS_LOV", constraintName: "SYS_LOV_U1")
        }
    }

    changeSet(author: "hailor", id: "20160609-hailor-9") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_LOV_ITEM_S', startValue: "10001")
        }
        createTable(tableName: "SYS_LOV_ITEM") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "LOV_ITEM_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_LOV_ITEM_PK")
                }
            } else {
                column(name: "LOV_ITEM_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_LOV_ITEM_PK")
                }
            }

            column(name: "LOV_ID", type: "bigint", remarks: "头表ID")
            column(name: "DISPLAY", type: "varchar(255)", remarks: "描述字段")
            column(name: "IS_AUTOCOMPLETE", type: "varchar(1)", defaultValue: "N", remarks: "是否autocomplete")
            column(name: "CONDITION_FIELD", type: "varchar(80)", defaultValue: "N", remarks: " 是否查询字段")
            column(name: "CONDITION_FIELD_WIDTH", type: "decimal(20,0)", remarks: "查询字段宽度")
            column(name: "CONDITION_FIELD_TYPE", type: "varchar(30)", remarks: "查询字段组件类型")
            column(name: "CONDITION_FIELD_NAME", type: "varchar(80)", remarks: "查询字段名")
            column(name: "CONDITION_FIELD_NEWLINE", type: "varchar(1)", remarks: "查询字段新一行")
            column(name: "CONDITION_FIELD_SELECT_CODE", type: "varchar(80)", remarks: "查询字段combobox对应的快码值")
            column(name: "CONDITION_FIELD_LOV_CODE", type: "varchar(80)", remarks: "查询字段lov对应的通用lov编码")
            column(name: "CONDITION_FIELD_SEQUENCE", type: "decimal(20,0)", remarks: "查询字段排序号")
            column(name: "CONDITION_FIELD_SELECT_URL", type: "varchar(255)", remarks: "查询字段combobox对应的URL")
            column(name: "CONDITION_FIELD_SELECT_VF", type: "varchar(80)", remarks: "查询字段combobox对应的valueField")
            column(name: "CONDITION_FIELD_SELECT_TF", type: "varchar(80)", remarks: "查询字段combobox对应的textField")
            column(name: "CONDITION_FIELD_TEXTFIELD", type: "varchar(80)", remarks: "查询字段对应的textField")
            column(name: "AUTOCOMPLETE_FIELD", type: "varchar(1)", defaultValue: "Y", remarks: "autocomplete显示列")
            column(name: "GRID_FIELD", type: "varchar(1)", defaultValue: "Y", remarks: "是否显示表格列")
            column(name: "GRID_FIELD_NAME", type: "varchar(80)", remarks: "表格列字段名")
            column(name: "GRID_FIELD_SEQUENCE", type: "decimal(20,0)", defaultValue: "1", remarks: "表格列排序号")
            column(name: "GRID_FIELD_WIDTH", type: "decimal(20,0)", remarks: "表格列宽")
            column(name: "GRID_FIELD_ALIGN", type: "varchar(10)", defaultValue: "center", remarks: "表格列布局")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "SYS_LOV_ITEM", indexName: "SYS_LOV_ITEM_N1") { column(name: "LOV_ID", type: "bigint") }

    }

    changeSet(author: "hailor", id: "20160609-hailor-14") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PROMPTS_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PROMPTS") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "PROMPT_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PROMPTS_PK")
                }
            } else {
                column(name: "PROMPT_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PROMPTS_PK")
                }
            }

            column(name: "PROMPT_CODE", type: "varchar(255)", remarks: "文本编码")
            column(name: "LANG", type: "varchar(10)", remarks: "语言")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "PROMPT_CODE,LANG", tableName: "SYS_PROMPTS", constraintName: "SYS_PROMPTS_U1")
        } else {
            addUniqueConstraint(columnNames: "PROMPT_ID,PROMPT_CODE,LANG", tableName: "SYS_PROMPTS", constraintName: "SYS_PROMPTS_U1")
        }
    }
}

databaseChangeLog(logicalFilePath: "patch.groovy") {
    changeSet(author: "zhizheng.yang", id: "20161025-zhizheng.yang-1") {
        addColumn(tableName: "SYS_LOV") {
            column(name: "CUSTOM_SQL", type: "clob", remarks: "自定义sql")
        }
    }
    changeSet(author: "zhizheng.yang", id: "20161104-zhizheng.yang") {
        addColumn(tableName: "SYS_LOV") {
            column(name: "QUERY_COLUMNS", type: "int", defaultValue: "1", remarks: "查询框列数")
        }
    }

    changeSet(author: "jialong.zuo", id: "20161129-jialongzuo-1") {
        addColumn(tableName: "SYS_CODE_VALUE_B") {
            column(name: "ORDER_SEQ", type: "int", defaultValue: "10")
        }
    }


    changeSet(author: "zhizheng.yang", id: "20161221-yangzhizheng-1") {
        addColumn(tableName: "SYS_LOV_ITEM") {
            column(name: "CONDITION_FIELD_LABEL_WIDTH", type: "decimal")
        }
    }
    changeSet(author: "zhizhengyang", id: "20170106-yangzhizheng-1") {
        addColumn(tableName: "SYS_LOV") {
            column(name: "CUSTOM_URL", type: "varchar(255)", remarks: "自定义URL")
        }
    }

    changeSet(author: "qiangzeng", id: "20170317-qiangzeng-1") {
        addColumn(tableName: "SYS_CODE_B") {
            column(name: "TYPE", type: "varchar(10)", remarks: "代码类型", defaultValue: "USER")
        }
        addColumn(tableName: "SYS_CODE_B") {
            column(name: "ENABLED_FLAG", type: "varchar(1)", remarks: "是否启用", defaultValue: "Y")
        }
        addColumn(tableName: "SYS_CODE_VALUE_B") {
            column(name: "TAG", type: "varchar(255)", remarks: "标记")
        }
        addColumn(tableName: "SYS_CODE_VALUE_B") {
            column(name: "ENABLED_FLAG", type: "varchar(1)", remarks: "是否启用", defaultValue: "Y")
        }
    }

    changeSet(author: "qiangzeng", id: "20170426-qiangzeng-2") {
        addColumn(tableName: "SYS_CODE_B") {
            column(name: "PARENT_CODE_ID", type: "bigint", remarks: "父级快码")
        }
    }

    changeSet(author: "qiangzeng", id: "20170426-qiangzeng-3") {
        addColumn(tableName: "SYS_CODE_VALUE_B") {
            column(name: "PARENT_CODE_VALUE_ID", type: "bigint", remarks: "父级快码值ID")
        }
    }

    changeSet(author: "niujiaqing", id: "20170607-sys-code-b") {
        if (helper.isMysql()) {
            renameColumn(tableName: "SYS_CODE_B", oldColumnName: "CODE", newColumnName: "CODE", columnDataType: "varchar(30)", remarks: "编码")
            addNotNullConstraint(tableName: "SYS_CODE_B", columnName: "CODE", columnDataType: "VARCHAR(30)")
            renameColumn(tableName: "SYS_CODE_B", oldColumnName: "DESCRIPTION", newColumnName: "DESCRIPTION", columnDataType: "varchar(250)", remarks: "编码描述")
        }
    }

    changeSet(author: "niujiaqing", id: "20170607-sys-code-tl-index") {
        createIndex(tableName: "SYS_CODE_TL", indexName: "SYS_CODE_TL_N1") {
            column(name: "DESCRIPTION")
        }
    }

    changeSet(author: "niujiaqing", id: "20170607-sys-code-value-b") {
        if (helper.isMysql()) {
            renameColumn(tableName: "SYS_CODE_VALUE_B", oldColumnName: "CODE_ID", newColumnName: "CODE_ID", columnDataType: "BIGINT", remarks: "编码ID")
            addNotNullConstraint(tableName: "SYS_CODE_VALUE_B", columnName: "CODE_ID", columnDataType: "BIGINT")
            renameColumn(tableName: "SYS_CODE_VALUE_B", oldColumnName: "VALUE", newColumnName: "VALUE", columnDataType: "varchar(150)", remarks: "编码值")
            renameColumn(tableName: "SYS_CODE_VALUE_B", oldColumnName: "MEANING", newColumnName: "MEANING", columnDataType: "varchar(150)", remarks: "编码值名")
            renameColumn(tableName: "SYS_CODE_VALUE_B", oldColumnName: "DESCRIPTION", newColumnName: "DESCRIPTION", columnDataType: "varchar(250)", remarks: "编码值描述")
        }
    }
    changeSet(author: "niujiaqing", id: "20170607-sys-lov-index") {
        createIndex(tableName: "SYS_LOV", indexName: "SYS_LOV_N1") {
            column(name: "DESCRIPTION")
        }
    }


    changeSet(author: "niujiaqing", id: "20170607-sys-prompts-index") {
        createIndex(tableName: "SYS_PROMPTS", indexName: "SYS_PROMPTS_N1") {
            column(name: "DESCRIPTION")
        }
    }

    changeSet(author: "jialong.zuo", id: "20170705-sys_code_b-add-required") {
        if (!helper.isHana()) {
            addNotNullConstraint(tableName: "SYS_CODE_B", columnName: "DESCRIPTION", columnDataType: "varchar(250)")
        }
    }

    changeSet(author: "jialong.zuo", id: "20170705-sys_code_value_b-add-required") {
        if (!helper.isHana()) {
            dropUniqueConstraint(tableName: "SYS_CODE_VALUE_B", constraintName: "SYS_CODE_VALUE_B_U1")
            addNotNullConstraint(tableName: "SYS_CODE_VALUE_B", columnName: "CODE_ID", columnDataType: "bigint")
            addNotNullConstraint(tableName: "SYS_CODE_VALUE_B", columnName: "VALUE", columnDataType: "varchar(150)")
            if (!helper.isPostgresql()) {
                addUniqueConstraint(columnNames: "CODE_ID,VALUE", tableName: "SYS_CODE_VALUE_B", constraintName: "SYS_CODE_VALUE_B_U1")
            } else {
                addUniqueConstraint(columnNames: "CODE_VALUE_ID,CODE_ID,VALUE", tableName: "SYS_CODE_VALUE_B", constraintName: "SYS_CODE_VALUE_B_U1")
            }

            addNotNullConstraint(tableName: "SYS_CODE_VALUE_B", columnName: "MEANING", columnDataType: "varchar(150)")
        }
    }

    changeSet(author: "yangzhizheng", id: "20171030-sys_lov") {
        addColumn(tableName: "SYS_LOV") {
            column(name: "LOV_PAGE_SIZE", type: "VARCHAR(3)", remarks: "每页页数", defaultValue: "10", afterColumn: "HEIGHT")
        }
    }

    changeSet(author: "yangzhizheng", id: "20180131-sys-lov") {
        addColumn(tableName: "SYS_LOV") {
            column(name: "TREE_FLAG", type: "varchar(1)", defaultValue: "N", remarks: "是否为树形结构")
        }
        addColumn(tableName: "SYS_LOV") {
            column(name: "ID_FIELD", type: "varchar(80)", remarks: "树形结构ID字段")
        }
        addColumn(tableName: "SYS_LOV") {
            column(name: "PARENT_ID_FIELD", type: "varchar(80)", remarks: "树形结构parentID字段")
        }
    }
    changeSet(author: "qiang.zeng", id: "20180817-sys-code-value-b") {
        addColumn(tableName: "SYS_CODE_VALUE_B") {
            column(name: "PARENT_CODE_VALUE", type: "varchar(150)", remarks: "父级快码值")
        }
    }

    changeSet(author: "qiang.zeng", id: "20181217-qiangzeng-1") {
        addColumn(tableName: "SYS_PROMPTS") {
            column(name: "MODULE_CODE", type: "VARCHAR(50)", remarks: "所属模块", afterColumn: "DESCRIPTION")
        }
    }

    if (!helper.isHana()) {
        changeSet(author: "qiang.zeng", id: "20190328-alter-url-length") {
            modifyDataType(tableName: "SYS_LOV", columnName: "CUSTOM_URL", newDataType: "VARCHAR(2000)")
            modifyDataType(tableName: "SYS_LOV_ITEM", columnName: "CONDITION_FIELD_SELECT_URL", newDataType: "VARCHAR(2000)")
        }
    }

    if (helper.isHana()) {
        changeSet(author: "qiang.zeng", id: "20190328-patch-for-hana") {
            sqlFile(path: helper.dataPath("script/db/data/hana/patch/20190328-12162-1.sql"), encoding: "UTF-8")
        }
    }

}
