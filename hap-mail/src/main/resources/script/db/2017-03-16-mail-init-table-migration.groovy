package script.db

databaseChangeLog(logicalFilePath: "2017-03-16-init-table-migration.groovy") {

    changeSet(author: "qiangzeng", id: "20170721-sys-message-email-property-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_MESSAGE_EMAIL_PROPERTY_S', startValue: "10001")
        }
        createTable(tableName: "SYS_MESSAGE_EMAIL_PROPERTY", remarks: "邮件消息属性") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "PROPERTY_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_MESSAGE_EMAIL_PROPERTY_PK")
                }
            } else {
                column(name: "PROPERTY_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_MESSAGE_EMAIL_PROPERTY_PK")
                }
            }

            column(name: "PROPERTY_NAME", type: "VARCHAR(50)", remarks: "属性名称")
            column(name: "PROPERTY_CODE", type: "VARCHAR(50)", remarks: "属性值")
            column(name: "CONFIG_ID", type: "BIGINT", remarks: "邮件账户ID")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "SYS_MESSAGE_EMAIL_PROPERTY", indexName: "SYS_MESSAGE_EMAIL_PROPERTY_N1") {
            column(name: "PROPERTY_NAME", type: "varchar(50)")
        }
    }

}

dbType = helper.dbType().toString()
databaseChangeLog(logicalFilePath: "2016-06-09-init-data-migration.groovy") {

    changeSet(author: "jessen", id: "20160613-data-sys-message-email-config-1") {
        sqlFile(path: "script/db/data/" + dbType + "/init/sys_message_email_config.sql", encoding: "UTF-8")
    }

    changeSet(author: "jessen", id: "20160613-data-sys-message-email-account-1") {
        sqlFile(path: "script/db/data/" + dbType + "/init/sys_message_email_account.sql", encoding: "UTF-8")
    }

    changeSet(author: "jessen", id: "20160613-data-sys-message-email-white-lt-1") {
        sqlFile(path: "script/db/data/" + dbType + "/init/sys_message_email_white_lt.sql", encoding: "UTF-8")
    }

    changeSet(author: "jessen", id: "20160613-data-sys-message-template-1") {
        sqlFile(path: "script/db/data/" + dbType + "/init/sys_message_template.sql", encoding: "UTF-8")
    }

    changeSet(author: "qiangzeng", id: "20170717-data-sys-message-email-config-1") {
        sqlFile(path: "script/db/data/" + dbType + "/init/sys_message_email_config_update.sql", encoding: "UTF-8")
    }

}

databaseChangeLog(logicalFilePath: "patch.groovy") {

    changeSet(author: "jialong.zuo", id: "20170705-sys_message_email_config-add-required") {
        if (!helper.isHana()) {
            addNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_CONFIG", columnName: "HOST", columnDataType: "varchar(50)")
            addNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_CONFIG", columnName: "PORT", columnDataType: "varchar(10)")
            addNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_CONFIG", columnName: "USE_WHITE_LIST", columnDataType: "varchar(1)")
            addNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_CONFIG", columnName: "USER_NAME", columnDataType: "varchar(240)")
            addNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_CONFIG", columnName: "PASSWORD", columnDataType: "varchar(240)")
        }
    }

    changeSet(author: "jialong.zuo", id: "20170705-sys_message_email_account-add-required") {
        if (!helper.isHana()) {
            addNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_ACCOUNT", columnName: "USER_NAME", columnDataType: "varchar(240)")
        }
    }

    changeSet(author: "jialong.zuo", id: "20170705-sys_message_template-add-required") {
        if (!helper.isHana()) {
            addNotNullConstraint(tableName: "SYS_MESSAGE_TEMPLATE", columnName: "SUBJECT", columnDataType: "longtext")
            addNotNullConstraint(tableName: "SYS_MESSAGE_TEMPLATE", columnName: "CONTENT", columnDataType: "longtext")
        }
    }

    changeSet(author: "qiangzeng", id: "20170717-sys_message_template") {
        addColumn(tableName: "SYS_MESSAGE_TEMPLATE") {
            column(name: "SEND_TYPE", type: "VARCHAR(50)", remarks: "发送类型", defaultValue: "DIRECT", afterColumn: "DESCRIPTION")
        }
    }

    changeSet(author: "qiangzeng", id: "20170717-sys_message_email_config") {
        dropNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_CONFIG", columnName: "USER_NAME", columnDataType: "varchar(240)")
        dropNotNullConstraint(tableName: "SYS_MESSAGE_EMAIL_CONFIG", columnName: "PASSWORD", columnDataType: "varchar(240)")
    }

    changeSet(author: "qiangzeng", id: "20170718-sys_message_email_config") {
        addColumn(tableName: "SYS_MESSAGE_EMAIL_CONFIG") {
            column(name: "SERVER_ENABLE", type: "VARCHAR(1)", remarks: "是否启用服务器配置", defaultValue: "Y", afterColumn: "ENABLE")
        }
    }

    if (helper.isHana()) {
        changeSet(author: "qiangzeng", id: "20180302-patch-for-hana") {
            sqlFile(path: "script/db/data/hana/patch/mail-update.sql", encoding: "UTF-8")
        }
    }
}