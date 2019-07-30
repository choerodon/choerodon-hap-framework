package script.db

databaseChangeLog(logicalFilePath: "2016-06-09-init-table-migration.groovy") {
    changeSet(author: "jessen", id: "20160610-sys-attach-category-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_ATTACH_CATEGORY_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_ATTACH_CATEGORY_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "CATEGORY_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ATTACH_CATEGORY_B_PK")
                }
            } else {
                column(name: "CATEGORY_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ATTACH_CATEGORY_B_PK")
                }
            }

            column(name: "CATEGORY_NAME", type: "varchar(40)")
            column(name: "LEAF_FLAG", type: "varchar(1)")
            column(name: "DESCRIPTION", type: "varchar(240)")
            column(name: "STATUS", type: "varchar(1)")
            column(name: "PARENT_CATEGORY_ID", type: "bigint")
            column(name: "PATH", type: "varchar(200)", remarks: "层级路径")
            column(name: "SOURCE_TYPE", type: "varchar(100)")
            column(name: "ALLOWED_FILE_TYPE", type: "varchar(300)")
            column(name: "ALLOWED_FILE_SIZE", type: "decimal(20,0)")
            column(name: "IS_UNIQUE", type: "varchar(1)")
            column(name: "CATEGORY_PATH", type: "varchar(255)")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createTable(tableName: "SYS_ATTACH_CATEGORY_TL") {
            column(name: "CATEGORY_ID", type: "bigint", remarks: "角色ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "CATEGORY_NAME", type: "varchar(40)", remarks: "类别名称")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "角色描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "jessen", id: "20160610-sys-attachment-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_ATTACHMENT_S', startValue: "10001")
        }
        createTable(tableName: "SYS_ATTACHMENT") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ATTACHMENT_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ATTACHMENT_PK")
                }
            } else {
                column(name: "ATTACHMENT_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ATTACHMENT_PK")
                }
            }

            column(name: "CATEGORY_ID", type: "bigint", remarks: "分类ID")
            column(name: "NAME", type: "varchar(40)", remarks: "附件名称")
            column(name: "SOURCE_TYPE", type: "varchar(30)", remarks: "对应业务类型")
            column(name: "SOURCE_KEY", type: "varchar(40)", remarks: "业务主键")
            column(name: "STATUS", type: "varchar(1)", remarks: "状态")
            column(name: "START_ACTIVE_DATE", type: "datetime", remarks: "开始生效日期")
            column(name: "END_ACTIVE_DATE", type: "datetime", remarks: "失效时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "SYS_ATTACHMENT", indexName: "SYS_ATTACHMENT_N1") { column(name: "CATEGORY_ID", type: "bigint") }
        createIndex(tableName: "SYS_ATTACHMENT", indexName: "SYS_ATTACHMENT_N2") {
            column(name: "SOURCE_TYPE", type: "varchar(30)")
            column(name: "SOURCE_KEY", type: "varchar(40)")
        }
    }

    changeSet(author: "jessen", id: "20160610-sys-file-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_FILE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_FILE") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "FILE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_FILE_PK")
                }
            } else {
                column(name: "FILE_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_FILE_PK")
                }
            }

            column(name: "ATTACHMENT_ID", type: "bigint", remarks: "附件ID")
            column(name: "FILE_NAME", type: "varchar(255)", remarks: "文件名")
            column(name: "FILE_PATH", type: "varchar(255)", remarks: "文件虚拟路径")
            column(name: "FILE_SIZE", type: "decimal(20,0)", remarks: "文件大小")
            column(name: "FILE_TYPE", type: "varchar(240)", remarks: "文件类型")
            column(name: "UPLOAD_DATE", type: "datetime", remarks: "上传时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "SYS_FILE", indexName: "SYS_FILE_N1") { column(name: "ATTACHMENT_ID", type: "bigint") }

    }

}

databaseChangeLog(logicalFilePath: "2016-06-09-init-data-migration.groovy") {
    changeSet(author: "jessen", id: "20160613-data-sys-attach-category-1") {
        sqlFile(path: "script/db/data/" + helper.dbType().toString() + "/init/sys_attach_category.sql", encoding: "UTF-8")
    }
}

databaseChangeLog(logicalFilePath: "patch.groovy") {
    changeSet(author: "jialong.zuo", id: "20170705-sys_attach_category_b-add-required") {
        if (!helper.isHana()) {
            addNotNullConstraint(tableName: "SYS_ATTACH_CATEGORY_B", columnName: "CATEGORY_NAME", columnDataType: "varchar(40)")
        }
    }
    changeSet(author: "qiangzeng", id: "20180208-sys-attachment-1") {
        if (!helper.isPostgresql()) {
            addUniqueConstraint(tableName: "SYS_ATTACHMENT", columnNames: "SOURCE_TYPE,SOURCE_KEY", constraintName: "SYS_ATTACHMENT_U1")
        } else {
            addUniqueConstraint(tableName: "SYS_ATTACHMENT", columnNames: "ATTACHMENT_ID,SOURCE_TYPE,SOURCE_KEY", constraintName: "SYS_ATTACHMENT_U1")
        }
    }
    if (!helper.isHana()) {
        changeSet(author: "niujiaqing", id: "20171012-category-type-length") {
            modifyDataType(tableName: "SYS_ATTACH_CATEGORY_B", columnName: "ALLOWED_FILE_TYPE", newDataType: "VARCHAR(300)")
        }
    }
    if (helper.isHana()) {
        changeSet(author: "niujiaqing", id: "20171012-category-type-length-hana") {
            sqlFile(path: helper.dataPath("script/db/data/hana/patch/20171012-1265.sql"), encoding: "UTF-8")
        }
    }

    if (helper.isPostgresql() || helper.isHana()) {
        changeSet(author: "qixiangyu", id: "20180208-sys-attachment-1-fix") {
            dropIndex(tableName: "SYS_ATTACHMENT", indexName: "SYS_ATTACHMENT_N2")
        }
    }
}