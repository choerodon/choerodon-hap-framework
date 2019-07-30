package script.db

databaseChangeLog(logicalFilePath: "2016-09-26-init-migration.groovy") {

    /* 接口透传*/
    changeSet(author: "xiangyuQi", id: "20161031-sys-interfaceHeader-1") {

        createTable(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "HEADER_ID", type: "VARCHAR(255)", remarks: "pk") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_INTERFACE_HEADER_PK")
            }
            column(name: "INTERFACE_CODE", type: "VARCHAR(30)", remarks: "系统代码") {
                constraints(nullable: "false")
            }
            column(name: "INTERFACE_TYPE", type: "VARCHAR(10)", remarks: "接口类型") {
                constraints(nullable: "false")
            }
            column(name: "DOMAIN_URL", type: "VARCHAR(200)", remarks: "系统地址") {
                constraints(nullable: "false")
            }
            column(name: "BODY_HEADER", type: "VARCHAR(2000)", remarks: "SOAP报文前缀")
            column(name: "BODY_TAIL", type: "VARCHAR(2000)", remarks: "SOAP报文后缀")
            column(name: "NAMESPACE", type: "VARCHAR(30)", remarks: "SOAP命名空间")
            column(name: "REQUEST_METHOD", type: "VARCHAR(10)", remarks: "请求方法") {
                constraints(nullable: "false")
            }
            column(name: "REQUEST_FORMAT", type: "VARCHAR(30)", remarks: "请求形式") {
                constraints(nullable: "false")
            }
            column(name: "REQUEST_CONTENTTYPE", type: "VARCHAR(80)", remarks: "请求报文格式")
            column(name: "REQUEST_ACCEPT", type: "VARCHAR(80)", remarks: "请求接收类型")
            column(name: "AUTH_FLAG", type: "VARCHAR(1)", remarks: "是否需要验证") {
                constraints(nullable: "false")
            }
            column(name: "AUTH_USERNAME", type: "VARCHAR(80)", remarks: "校验用户名")
            column(name: "AUTH_PASSWORD", type: "VARCHAR(200)", remarks: "校验密码")
            column(name: "ENABLE_FLAG", type: "VARCHAR(1)", remarks: "是否有效") {
                constraints(nullable: "false")
            }
            column(name: "NAME", type: "VARCHAR(200)", remarks: "系统名称") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "系统描述") {
                constraints(nullable: "false")
            }
            column(name: "SYSTEM_TYPE", type: "VARCHAR(10)", remarks: "系统类型")
            column(name: "MAPPER_CLASS", type: "VARCHAR(255)", remarks: "包装类")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")


        }

        createTable(tableName: "SYS_IF_CONFIG_HEADER_TL") {
            column(name: "HEADER_ID", type: "VARCHAR(255)", remarks: "pk") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", remarks: "语言", type: "VARCHAR(50)") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "NAME", remarks: "系统名称", type: "VARCHAR(200)")
            column(name: "DESCRIPTION", remarks: "系统描述", type: "VARCHAR(255)")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")


        }
    }

    changeSet(author: "xiangyuQi", id: "20161031-sys-interfaceLine-1") {

        createTable(tableName: "SYS_IF_CONFIG_LINE_B") {
            column(name: "LINE_ID", type: "VARCHAR(255)", remarks: "pk") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_INTERFACE_LINE_PK")
            }
            column(name: "HEADER_ID", type: "VARCHAR(255)", remarks: "行Id") {
                constraints(nullable: "false")
            }
            column(name: "LINE_CODE", type: "VARCHAR(30)", remarks: "接口代码") {
                constraints(nullable: "false")
            }
            column(name: "IFT_URL", type: "VARCHAR(200)", remarks: "接口地址") {
                constraints(nullable: "false")
            }
            column(name: "ENABLE_FLAG", type: "VARCHAR(1)", remarks: "是否有效") {
                constraints(nullable: "false")
            }
            column(name: "LINE_NAME", type: "VARCHAR(50)", remarks: "接口名称")
            column(name: "LINE_DESCRIPTION", type: "VARCHAR(255)", remarks: "接口描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")


        }

        createTable(tableName: "SYS_IF_CONFIG_LINE_TL") {
            column(name: "HEADER_ID", type: "VARCHAR(255)", remarks: "pk") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", remarks: "语言", type: "VARCHAR(50)") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "LINE_NAME", remarks: "接口名称", type: "VARCHAR(50)")
            column(name: "LINE_DESCRIPTION", remarks: "接口描述", type: "VARCHAR(255)")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")


        }
    }

}

databaseChangeLog(logicalFilePath: "patch.groovy") {

    changeSet(author: "xiangyu.qi", id: "20161109-xiangyuqi-1") {
        renameColumn(tableName: "SYS_IF_CONFIG_LINE_TL", columnDataType: "VARCHAR(255)", oldColumnName: "HEADER_ID", newColumnName: "LINE_ID")
        if (!helper.isPostgresql()) {
            addUniqueConstraint(tableName: "SYS_IF_CONFIG_HEADER_B", columnNames: "INTERFACE_CODE", constraintName: "SYS_IF_CONFIG_HEADER_U1")
        } else {
            addUniqueConstraint(tableName: "SYS_IF_CONFIG_HEADER_B", columnNames: "HEADER_ID,INTERFACE_CODE", constraintName: "SYS_IF_CONFIG_HEADER_U1")
        }

    }

    changeSet(author: "qixiangyu", id: "20170327-qixiangyu-1") {
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "AUTH_TYPE", type: "VARCHAR(50)", remarks: "校验模式", defaultValue: "BASIC_AUTH")
        }
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "AUTH_URL", type: "VARCHAR(255)", remarks: "取授权码的url")
        }
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "ACCESS_TOKEN_URL", type: "VARCHAR(255)", remarks: "获取token的url")
        }
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "CLIENT_ID", type: "VARCHAR(255)", remarks: "应用id")
        }
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "CLIENT_SECRET", type: "VARCHAR(255)", remarks: "应用secret")
        }
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "GRANT_TYPE", type: "VARCHAR(50)", remarks: "授权模式")
        }
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "SCOPE", type: "VARCHAR(100)", remarks: "权限范围")
        }
    }

    changeSet(author: "jialong.zuo", id: "20170616-sys-if-config-header-b-addColumn-action") {
        addColumn(tableName: "SYS_IF_CONFIG_HEADER_B") {
            column(name: "SOAP_ACTION", type: "VARCHAR(100)", remarks: "SOAPACTION")
        }
    }

    if (!helper.isHana()) {
        changeSet(author: "qixiangyu", id: "interface-alter-url-length-2") {
            modifyDataType(tableName: "SYS_IF_CONFIG_HEADER_B", columnName: "AUTH_URL", newDataType: "VARCHAR(2000)")
            modifyDataType(tableName: "SYS_IF_CONFIG_HEADER_B", columnName: "ACCESS_TOKEN_URL", newDataType: "VARCHAR(2000)")
            modifyDataType(tableName: "SYS_IF_CONFIG_HEADER_B", columnName: "DOMAIN_URL", newDataType: "VARCHAR(2000)")
            modifyDataType(tableName: "SYS_IF_CONFIG_LINE_B", columnName: "IFT_URL", newDataType: "VARCHAR(2000)")
        }
    }
    if (helper.isHana()) {
        changeSet(author: "qixiangyu", id: "interface-alter-url-length-for-hana") {
            sqlFile(path: "script/db/data/hana/patch/hana-alter-url.sql", encoding: "UTF-8")
        }
    }
}