package script.db

databaseChangeLog(logicalFilePath: "2017-10-12-init-migration.groovy") {
    changeSet(author: "qiangzeng", id: "20171106-sys-parameter-config-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PARAMETER_CONFIG_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PARAMETER_CONFIG", remarks: "参数配置") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "PARAMETER_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "参数ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PARAMETER_CONFIG_PK")
                }
            } else {
                column(name: "PARAMETER_ID", type: "BIGINT", remarks: "参数ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PARAMETER_CONFIG_PK")
                }
            }

            column(name: "CODE", type: "VARCHAR(20)", remarks: "参数编码")
            column(name: "TARGET_ID", type: "BIGINT", remarks: "所属编码ID")
            column(name: "DISPLAY", type: "VARCHAR(20)", remarks: "表单控件") {
                constraints(nullable: "false")
            }
            column(name: "TABLE_FIELD_NAME", type: "VARCHAR(80)", remarks: "表字段名") {
                constraints(nullable: "false")
            }
            column(name: "TITLE", type: "VARCHAR(50)", remarks: "标题") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "描述")
            column(name: "LINE_NUMBER", type: "INT", remarks: "行号")
            column(name: "COLUMN_NUMBER", type: "INT", remarks: "列号")
            column(name: "DISPLAY_LENGTH", type: "INT", remarks: "显示长度")
            column(name: "DATA_LENGTH", type: "INT", remarks: "数据长度")
            column(name: "REQUIRED", type: "VARCHAR(1)", remarks: "必输")
            column(name: "READ_ONLY", type: "VARCHAR(1)", remarks: "只读")
            column(name: "ENABLED", type: "VARCHAR(1)", remarks: "启用")
            column(name: "SOURCE_CODE", type: "VARCHAR(80)", remarks: "数据来源")
            column(name: "DEFAULT_TYPE", type: "VARCHAR(20)", remarks: "默认类型")
            column(name: "DEFAULT_VALUE", type: "CLOB", remarks: "默认值")
            column(name: "DEFAULT_TEXT", type: "VARCHAR(50)", remarks: "默认文本")
            column(name: "EXTRA_ATTRIBUTE", type: "CLOB", remarks: "扩展属性")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "CODE,TARGET_ID,TABLE_FIELD_NAME", tableName: "SYS_PARAMETER_CONFIG", constraintName: "SYS_PARAMETER_CONFIG_U1")
        } else {
            addUniqueConstraint(columnNames: "PARAMETER_ID,CODE,TARGET_ID,TABLE_FIELD_NAME", tableName: "SYS_PARAMETER_CONFIG", constraintName: "SYS_PARAMETER_CONFIG_U1")
        }
    }
}


databaseChangeLog(logicalFilePath: "patch.groovy") {

    changeSet(author: "xiangyuqi", id: "20161009-xiangyuqi-1") {
        addColumn(tableName: "SYS_USER") {
            column(name: "LAST_LOGIN_DATE", type: "datetime", remarks: "最后一次登录时间")
        }
        addColumn(tableName: "SYS_USER") {
            column(name: "LAST_PASSWORD_UPDATE_DATE", type: "datetime", remarks: "最后一次修改密码时间")
        }
    }

    changeSet(author: "jialong.zuo", id: "20161110-jialongzuo-1") {
        if (!helper.isPostgresql()) {
            addUniqueConstraint(tableName: "HR_ORG_UNIT_B", columnNames: "UNIT_CODE", constraintName: "HR_ORG_UNIT_U1")
        } else {
            addUniqueConstraint(tableName: "HR_ORG_UNIT_B", columnNames: "UNIT_ID,UNIT_CODE", constraintName: "HR_ORG_UNIT_U1")
        }
    }

    changeSet(author: "xiangyuqi", id: "20161221-xiangyuqi-1") {
        addColumn(tableName: "SYS_USER") {
            column(name: "FIRST_LOGIN", type: "varchar(1)", remarks: "是否第一次登录")
        }
        addDefaultValue(tableName: "SYS_USER", columnName: "FIRST_LOGIN", columnDataType: "varchar", defaultValue: "Y")
    }

    changeSet(author: "qiangzeng", id: "20170320-qiangzeng-1") {
        addColumn(tableName: "SYS_USER") {
            column(name: "DESCRIPTION", type: "varchar(255)", remarks: "说明")
        }
        addColumn(tableName: "SYS_USER") {
            column(name: "EMPLOYEE_ID", type: "BIGINT", remarks: "员工ID")
        }
        addColumn(tableName: "SYS_USER") {
            column(name: "CUSTOMER_ID", type: "BIGINT", remarks: "客户ID")
        }
        addColumn(tableName: "SYS_USER") {
            column(name: "SUPPLIER_ID ", type: "BIGINT", remarks: "供应商ID")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "CERTIFICATE_TYPE", type: "varchar(240)", remarks: "证件类型", defaultValue: "ID")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "EFFECTIVE_START_DATE", type: "DATE", remarks: "有效日期从")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "EFFECTIVE_END_DATE", type: "DATE", remarks: "有效日期至")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE1", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE2", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE3", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE4", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE5", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE6", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE7", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE8", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE9", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE10", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE11", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE12", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE13", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE14", type: "varchar(240)")
        }
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }

    }

    changeSet(author: "qiangzeng", id: "20170426-qiangzeng-1") {
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "UNIT_CATEGORY", type: "varchar(50)", remarks: "组织类别", defaultValue: "G")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "UNIT_TYPE", type: "varchar(50)", remarks: "组织分类", defaultValue: "GROUP")
        }
    }


    changeSet(author: "niujiaqing", id: "20170527-sys_user-index") {
        createIndex(tableName: "SYS_USER", indexName: "SYS_USER_N1") { column(name: "STATUS", type: "varchar(30)") }
    }

    changeSet(author: "niujiaqing", id: "20170530-sys-resource-b-index") {
        createIndex(tableName: "SYS_RESOURCE_B", indexName: "SYS_RESOURCE_B_N1") { column(name: "TYPE", type: "varchar(15)") }
    }
    changeSet(author: "niujiaqing", id: "20170530-sys-resource-tl-index") {
//        createIndex(tableName:"SYS_RESOURCE_TL",  indexName:"SYS_RESOURCE_TL_N1"){column(name: "NAME", type: "varchar(40)")}
    }


    changeSet(author: "niujiaqing", id: "20170602-sys-function-b-index") {
        createIndex(tableName: "SYS_FUNCTION_B", indexName: "SYS_RESOURCE_B_N3") { column(name: "MODULE_CODE", type: "varchar(30)") }
        createIndex(tableName: "SYS_FUNCTION_B", indexName: "SYS_RESOURCE_B_N4") { column(name: "RESOURCE_ID", type: "bigint") }
    }
    changeSet(author: "niujiaqing", id: "20170602-sys-function-tl-index") {
        createIndex(tableName: "SYS_FUNCTION_TL", indexName: "SYS_FUNCTION_TL_N1") { column(name: "FUNCTION_NAME", type: "varchar(150)") }
    }

    changeSet(author: "jialongzuo", id: "20170605-hr-org-unit-b-index") {
        createIndex(tableName: "HR_ORG_UNIT_B", indexName: "HR_ORG_UNIT_B_N2") {
            column(name: "PARENT_ID", type: "bigint")
        }
    }

    changeSet(author: "jialongzuo", id: "20170605-hr-org-unit-tl-index") {
        createIndex(tableName: "HR_ORG_UNIT_TL", indexName: "HR_ORG_UNIT_TL_N1") {
            column(name: "NAME", type: "varchar(100)")
        }
    }

    changeSet(author: "jialongzuo", id: "20170606-position-b-index") {
        createIndex(tableName: "HR_ORG_POSITION_B", indexName: "HR_ORG_POSITION_B_N1") {
            column(name: "position_code", type: "varcher(50)")
        }
        createIndex(tableName: "HR_ORG_POSITION_B", indexName: "HR_ORG_POSITION_B_N2") {
            column(name: "PARENT_POSITION_ID", type: "bigint")
        }
    }

    changeSet(author: "jialongzuo", id: "20170606-position-tl-index") {
        createIndex(tableName: "HR_ORG_POSITION_TL", indexName: "HR_ORG_POSITION_TL_N1") {
            column(name: "NAME", type: "varchar(100)")
        }
    }

    changeSet(author: "jialongzuo", id: "20170606-company-b-change-name-long") {
        if (!helper.isHana()) {
            modifyDataType(tableName: "FND_COMPANY_B", columnName: "COMPANY_FULL_NAME", newDataType: "varchar(250)")
            modifyDataType(tableName: "FND_COMPANY_B", columnName: "COMPANY_SHORT_NAME", newDataType: "varchar(250)")
            modifyDataType(tableName: "FND_COMPANY_B", columnName: "ADDRESS", newDataType: "varchar(250)")
        }
    }

    changeSet(author: "jialongzuo", id: "20170606-fnd-company-b-index") {
        createIndex(tableName: "FND_COMPANY_B", indexName: "FND_COMPANY_B_N2") {
            column(name: "COMPANY_TYPE")
        }
        createIndex(tableName: "FND_COMPANY_B", indexName: "FND_COMPANY_B_N3") {
            column(name: "COMPANY_FULL_NAME")
        }
        createIndex(tableName: "FND_COMPANY_B", indexName: "FND_COMPANY_B_N4") {
            column(name: "PARENT_COMPANY_ID")
        }
    }

    changeSet(author: "jialongzuo", id: "20170606-company-tl-change-name-long") {
        if (!helper.isHana()) {
            modifyDataType(tableName: "FND_COMPANY_TL", columnName: "COMPANY_SHORT_NAME", newDataType: "varchar(250)")
            modifyDataType(tableName: "FND_COMPANY_TL", columnName: "COMPANY_FULL_NAME", newDataType: "varchar(250)")
        }
    }

    changeSet(author: "jialongzuo", id: "20170606-fnd-company-tl-index") {
        createIndex(tableName: "FND_COMPANY_TL", indexName: "FND_COMPANY_TL_N1") {
            column(name: "COMPANY_FULL_NAME")
        }
    }

    changeSet(author: "niujiaqing", id: "20170607-fnd-company-b") {
        if (helper.isMysql()) {
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "company_id", newColumnName: "COMPANY_ID", columnDataType: "BIGINT", remarks: "公司ID")
            addAutoIncrement(columnDataType: "BIGINT", columnName: "COMPANY_ID", tableName: "FND_COMPANY_B")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "company_code", newColumnName: "COMPANY_CODE", columnDataType: "VARCHAR(30)", remarks: "公司编码")
            addNotNullConstraint(tableName: "FND_COMPANY_B", columnName: "COMPANY_CODE", columnDataType: "VARCHAR(30)")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "company_type", newColumnName: "COMPANY_TYPE", columnDataType: "VARCHAR(30)", remarks: "公司类型")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "address", newColumnName: "ADDRESS", columnDataType: "VARCHAR(250)", remarks: "地址")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "company_level_id", newColumnName: "COMPANY_LEVEL_ID", columnDataType: "BIGINT", remarks: "公司级别")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "parent_company_id", newColumnName: "PARENT_COMPANY_ID", columnDataType: "BIGINT", remarks: "母公司")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "chief_position_id", newColumnName: "CHIEF_POSITION_ID", columnDataType: "BIGINT", remarks: "主岗位")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "start_date_active", newColumnName: "START_DATE_ACTIVE", columnDataType: "DATETIME", remarks: "有效时间开始")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "end_date_active", newColumnName: "END_DATE_ACTIVE", columnDataType: "DATETIME", remarks: "有效时间截止")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "company_short_name", newColumnName: "COMPANY_SHORT_NAME", columnDataType: "VARCHAR(250)", remarks: "公司简称")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "company_full_name", newColumnName: "COMPANY_FULL_NAME", columnDataType: "VARCHAR(250)", remarks: "公司全称")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "zipcode", newColumnName: "ZIPCODE", columnDataType: "VARCHAR(100)", remarks: "邮编")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "fax", newColumnName: "FAX", columnDataType: "VARCHAR(100)", remarks: "传真")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "phone", newColumnName: "PHONE", columnDataType: "VARCHAR(100)", remarks: "联系电话")
            renameColumn(tableName: "FND_COMPANY_B", oldColumnName: "contact_person", newColumnName: "CONTACT_PERSON", columnDataType: "VARCHAR(100)", remarks: "联系人")
        }
    }


    changeSet(author: "niujiaqing", id: "20170607-fnd-employee-index") {
        createIndex(tableName: "HR_EMPLOYEE", indexName: "HR_EMPLOYEE_N1") {
            column(name: "NAME")
        }
    }



    changeSet(author: "niujiaqing", id: "20170607-sys-function-resource") {
        if (helper.isMysql()) {
            renameColumn(tableName: "SYS_FUNCTION_RESOURCE", oldColumnName: "FUNCTION_ID", newColumnName: "FUNCTION_ID", columnDataType: "BIGINT", remarks: "功能ID")
            renameColumn(tableName: "SYS_FUNCTION_RESOURCE", oldColumnName: "RESOURCE_ID", newColumnName: "RESOURCE_ID", columnDataType: "BIGINT", remarks: "资源ID")
        }
    }

    changeSet(author: "niujiaqing", id: "20170607-sys-role-tl-index") {
        createIndex(tableName: "SYS_ROLE_TL", indexName: "SYS_ROLE_TL_N1") {
            column(name: "ROLE_NAME")
        }
        createIndex(tableName: "SYS_ROLE_TL", indexName: "SYS_ROLE_TL_N2") {
            column(name: "ROLE_DESCRIPTION")
        }
    }

    changeSet(author: "niujiaqing", id: "20170607-sys-user-shortcut-index") {
        if (!helper.isPostgresql()) {
            addUniqueConstraint(tableName: "SYS_USER_SHORTCUT", columnNames: "USER_ID,FUNCTION_CODE", constraintName: "SYS_USER_SHORTCUT_U1")
        } else {
            addUniqueConstraint(tableName: "SYS_USER_SHORTCUT", columnNames: "SHORTCUT_ID,USER_ID,FUNCTION_CODE", constraintName: "SYS_USER_SHORTCUT_U1")
        }

    }

    changeSet(author: "niujiaqing", id: "20170621-sys-function-b-index-fix") {
        dropIndex(tableName: "SYS_FUNCTION_B", indexName: "SYS_RESOURCE_B_N3")
        dropIndex(tableName: "SYS_FUNCTION_B", indexName: "SYS_RESOURCE_B_N4")

        createIndex(tableName: "SYS_FUNCTION_B", indexName: "SYS_FUNCTION_B_N2") { column(name: "MODULE_CODE", type: "varchar(30)") }
        createIndex(tableName: "SYS_FUNCTION_B", indexName: "SYS_FUNCTION_B_N3") { column(name: "RESOURCE_ID", type: "bigint") }
    }




    changeSet(author: "jialong.zuo", id: "20170703-sys_company_b-add-required") {
        if (!helper.isHana()) {
            dropIndex(tableName: "FND_COMPANY_B", indexName: "FND_COMPANY_B_N3")
            addNotNullConstraint(tableName: "FND_COMPANY_B", columnName: "COMPANY_FULL_NAME", columnDataType: "varchar(250)")
            createIndex(tableName: "FND_COMPANY_B", indexName: "FND_COMPANY_B_N3") {
                column(name: "COMPANY_FULL_NAME")
            }
            addNotNullConstraint(tableName: "FND_COMPANY_B", columnName: "COMPANY_SHORT_NAME", columnDataType: "varchar(250)")
        }
    }

    changeSet(author: "jialong.zuo", id: "20170705-hr_unit_b-add-required") {
        if (!helper.isHana()) {
            addNotNullConstraint(tableName: "HR_ORG_UNIT_B", columnName: "NAME", columnDataType: "varchar(100)")
        }
    }
    changeSet(author: "jialong.zuo", id: "20170705-hr_position_b-add-required") {
        dropIndex(tableName: "HR_ORG_POSITION_B", indexName: "HR_ORG_POSITION_B_N1")
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "POSITION_CODE", tableName: "HR_ORG_POSITION_B", constraintName: "HR_ORG_POSITION_B_U1")
        } else {
            addUniqueConstraint(columnNames: "POSITION_ID,POSITION_CODE", tableName: "HR_ORG_POSITION_B", constraintName: "HR_ORG_POSITION_B_U1")
        }

        if (!helper.isHana()) {
            addNotNullConstraint(tableName: "HR_ORG_POSITION_B", columnName: "NAME", columnDataType: "varchar(100)")
        }
    }

    changeSet(author: "niujiaqing", id: "20170706-sys_function_resource") {
        if (helper.isMysql()) {
            addAutoIncrement(columnDataType: "BIGINT", columnName: "FUNC_SRC_ID", tableName: "SYS_FUNCTION_RESOURCE")
        }
    }


    if (helper.isHana()) {
        changeSet(author: "qixiangyu", id: "20170731-patch-for-hana") {
            sqlFile(path: helper.dataPath("script/db/data/hana/patch/20170731-10990-1.sql"), encoding: "UTF-8")
        }
    }

    changeSet(author: "qiangzeng", id: "20170817-sys_job_running_info") {
        addColumn(tableName: "SYS_JOB_RUNNING_INFO") {
            column(name: "IP_ADDRESS", type: "VARCHAR(50)", remarks: "IP地址", afterColumn: "EXECUTION_SUMMARY")
        }
    }

    changeSet(author: "qixiangyu", id: "20170901-qixiangyu-add_attribute-for-org-1") {

        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE1", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE2", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE3", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE4", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE5", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE6", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE7", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE8", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE9", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE10", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE11", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE12", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE13", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE14", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_UNIT_B") {
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }

        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE1", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE2", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE3", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE4", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE5", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE6", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE7", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE8", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE9", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE10", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE11", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE12", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE13", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE14", type: "varchar(240)")
        }
        addColumn(tableName: "HR_ORG_POSITION_B") {
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }

        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE1", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE2", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE3", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE4", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE5", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE6", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE7", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE8", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE9", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE10", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE11", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE12", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE13", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE14", type: "varchar(240)")
        }
        addColumn(tableName: "FND_COMPANY_B") {
            column(name: "ATTRIBUTE15", type: "varchar(240)")
        }

    }
    if (!helper.isHana()) {
        changeSet(author: "qixiangyu", id: "20170918-alter-url-length") {
            modifyDataType(tableName: "SYS_RESOURCE_CUSTOMIZATION", columnName: "URL", newDataType: "VARCHAR(2000)")
        }
    }
    if (helper.isHana()) {
        changeSet(author: "qixiangyu", id: "20170918-patch-for-hana") {
            sqlFile(path: helper.dataPath("script/db/data/hana/patch/20170918-10990-1.sql"), encoding: "UTF-8")
        }
    }

    changeSet(author: "qiangzeng", id: "20171018-add_attribute-for-employee-1") {
        addColumn(tableName: "HR_EMPLOYEE") {
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(240)", afterColumn: "EFFECTIVE_END_DATE")
        }
    }

    changeSet(author: "yangzhizheng", id: "20170824-sys_forms") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_FORMS_S', startValue: "10001")
        }
        createTable(tableName: "SYS_FORMS") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "FORM_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表单ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FORM_BUILDER_PK")
                }
            } else {
                column(name: "FORM_ID", type: "bigint", remarks: "表单ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FORM_BUILDER_PK")
                }
            }

            if (!helper.isPostgresql()) {
                column(name: "CODE", type: "varchar(30)", remarks: "表单code") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "FORM_BUILDER_U1")
                }
            } else {
                column(name: "CODE", type: "varchar(30)", remarks: "表单code") {
                    constraints(nullable: "false")
                }
            }

            column(name: "DESCRIPTION", type: "varchar(220)", remarks: "表单描述")
            column(name: "CONTENT", type: 'clob', remarks: "表单代码")
            column(name: "IS_PUBLISH", type: 'varchar(1)', defaultValue: "N", remarks: "是否发布")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "ATTRIBUTE_CATEGORY", type: "varchar(30)")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "FORM_ID,CODE", tableName: "SYS_FORMS", constraintName: "FORM_BUILDER_U1")
        }
    }


    changeSet(author: "jialongzuo", id: "20171103-fnd_flex_rule_field_add1") {
        addColumn(tableName: "FND_FLEX_RULE_FIELD") {
            column(name: "DESCRIPTION", type: "VARCHAR(200)", remarks: "描述", afterColumn: "MODEL_COLUMN_ID")
        }
    }
    changeSet(author: "yangzhizheng", id: "20171013-sys-hotkey-1") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_HOTKEY_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_HOTKEY_B", remarks: "系统热键") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "HOTKEY_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_HOTKEY_PK")
                }
            } else {
                column(name: "HOTKEY_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_HOTKEY_PK")
                }
            }

            column(name: "CODE", type: "VARCHAR(50)", remarks: "热键编码") {
                constraints(nullable: "false")
            }
            column(name: "HOTKEY_LEVEL", type: "VARCHAR(50)", defaultValue: "system", remarks: "热键级别") {
                constraints(nullable: "false")
            }
            column(name: "HOTKEY_LEVEL_ID", type: "BIGINT", defaultValue: "0", remarks: "热键级别ID") {
                constraints(nullable: "false")
            }
            column(name: "HOTKEY", type: "VARCHAR(50)", remarks: "热键")
            column(name: "DESCRIPTION", type: "varchar(250)", remarks: "热键描述")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "CODE,HOTKEY_LEVEL,HOTKEY_LEVEL_ID", tableName: "SYS_HOTKEY_B", constraintName: "SYS_HOTKEY_U1")
        } else {
            addUniqueConstraint(columnNames: "HOTKEY_ID,CODE,HOTKEY_LEVEL,HOTKEY_LEVEL_ID", tableName: "SYS_HOTKEY_B", constraintName: "SYS_HOTKEY_U1")
        }



        createTable(tableName: "SYS_HOTKEY_TL") {
            column(name: "HOTKEY_ID", type: "BIGINT", remarks: "PK") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "DESCRIPTION", type: "varchar(250)", remarks: "热键描述")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    if (!helper.isHana()) {
        changeSet(author: "qixiangyu", id: "201701113-qixiangyu-alter-sys_profile_value") {
            modifyDataType(tableName: "SYS_PROFILE_VALUE", columnName: "PROFILE_VALUE", newDataType: "VARCHAR(255)")
        }
    }

    if (helper.isHana()) {
        changeSet(author: "qixiangyu", id: "201701113-qixiangyu-alter-sys_profile_value-hana") {
            sqlFile(path: helper.dataPath("script/db/data/hana/patch/20171113-10990.sql"), encoding: "UTF-8")
        }
    }

    changeSet(author: "jialongzuo", id: "201701113-jialongzuo-alter-data-permission") {
        dropUniqueConstraint(tableName: "SYS_PERMISSION_RULE_DETAIL", constraintName: "SYS_PERMISSION_RULE_DETAIL_U1")
        renameColumn(tableName: "SYS_PERMISSION_RULE_DETAIL", oldColumnName: "PERMISSION_FIELD_VALUE", newColumnName: "PERMISSION_FIELD_VALUE_TEMP", columnDataType: "BIGINT")
        addColumn(tableName: "SYS_PERMISSION_RULE_DETAIL") {
            column(name: "PERMISSION_FIELD_VALUE", type: "VARCHAR(200)")
        }
        sqlFile(path: helper.dataPath("script/db/data/hana/patch/20171030-10893.sql"), encoding: "UTF-8")
        dropColumn(tableName: "SYS_PERMISSION_RULE_DETAIL", columnName: "PERMISSION_FIELD_VALUE_TEMP")
        createIndex(tableName: "SYS_PERMISSION_RULE_DETAIL", indexName: "SYS_PERMISSION_RULE_DETAIL_U1") {
            column(name: "PERMISSION_FIELD_VALUE")
            column(name: "RULE_ID")
        }

    }

    changeSet(author: "qiangzeng", id: "20171115-sys_sys_parameter_config") {
        addColumn(tableName: "SYS_PARAMETER_CONFIG") {
            column(name: "LABEL_WIDTH", type: "INT", remarks: "label宽度", afterColumn: "DISPLAY_LENGTH")
        }
    }

    changeSet(author: "qiangzeng", id: "20180110-sys-parameter-config-1") {
        addColumn(tableName: "SYS_PARAMETER_CONFIG") {
            column(name: "SOURCE_TYPE", type: "varchar(20)", remarks: "数据类型", afterColumn: "ENABLED", defaultValue: 'LOV')
        }
    }

    if (helper.isMysql()) {
        changeSet(author: "qixiangyu", id: "interface-alter-url-length-drop-index") {
            preConditions(onFail: "MARK_RAN") {
                indexExists(indexName: "SYS_IF_INVOKE_INBOUND_N2")
            }
            dropIndex(tableName: "SYS_IF_INVOKE_INBOUND", indexName: "SYS_IF_INVOKE_INBOUND_N2")
        }
    }

    if (helper.isMysql()) {
        changeSet(author: "qixiangyu", id: "interface-alter-url-length-drop-index2") {
            preConditions(onFail: "MARK_RAN") {
                indexExists(indexName: "SYS_IF_INVOKE_OUTBOUND_N2")
            }
            dropIndex(tableName: "SYS_IF_INVOKE_OUTBOUND", indexName: "SYS_IF_INVOKE_OUTBOUND_N2")
        }
    }

    if (!helper.isHana() && !helper.isPostgresql()) {
        changeSet(author: "qixiangyu", id: "interface-alter-url-length") {
            modifyDataType(tableName: "SYS_IF_INVOKE_INBOUND", columnName: "INTERFACE_URL", newDataType: "VARCHAR(2000)")
            modifyDataType(tableName: "SYS_IF_INVOKE_OUTBOUND", columnName: "INTERFACE_URL", newDataType: "VARCHAR(2000)")
        }
    }

    changeSet(author: "yinlijian", id: "20180411-sys-code-rules-line") {
        addColumn(tableName: "SYS_CODE_RULES_LINE") {
            column(name: "STEP_NUMBER", type: "bigint", remarks: "更新数据库步长", afterColumn: "RESET_DATE")
        }
    }

    changeSet(author: "qiang.zeng", id: "20181225-qiangzeng-1") {
        addColumn(tableName: "SYS_RESOURCE_B") {
            column(name: "LOCKED_BY", type: "VARCHAR(40)", remarks: "建模页面被谁锁定", afterColumn: "ACCESS_CHECK")
        }
    }
}

databaseChangeLog(logicalFilePath:"2016-06-09-init-data-migration.groovy"){

    changeSet(author: "qixiangyu", id: "20171116-data-sys-user-update-name") {
        sqlFile(path: helper.dataPath("script/db/data/" + helper.dbType().toString() + "/init/sys_user_username_update.sql"), encoding: "UTF-8")
    }

}