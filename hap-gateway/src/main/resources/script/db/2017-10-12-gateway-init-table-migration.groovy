package script.db

databaseChangeLog(logicalFilePath: "2017-10-12-init-migration.groovy") {

    changeSet(author: "peng.jiang", id: "20171025-api-config-server") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'API_CONFIG_SERVER_S', startValue: "10001")
        }
        createTable(tableName: "API_CONFIG_SERVER", remarks: "服务注册功能-服务配置表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "SERVER_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CONFIG_SERVER_PK")
                }
            } else {
                column(name: "SERVER_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CONFIG_SERVER_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "CODE", type: "VARCHAR(30)", remarks: "服务代码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "API_CONFIG_SERVER_U1")
                }
            } else {
                column(name: "CODE", type: "VARCHAR(30)", remarks: "服务代码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "NAME", type: "VARCHAR(200)", remarks: "服务名称") {
                constraints(nullable: "false")
            }
            column(name: "SERVICE_TYPE", type: "VARCHAR(10)", remarks: "服务类型") {
                constraints(nullable: "false")
            }
            column(name: "PUBLISH_TYPE", type: "VARCHAR(10)", remarks: "发布类型");
            column(name: "DOMAIN_URL", type: "VARCHAR(200)", remarks: "服务地址") {
                constraints(nullable: "false")
            }
            if (!helper.isPostgresql()) {
                column(name: "MAPPING_URL", type: "VARCHAR(200)", remarks: "映射地址") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "API_CONFIG_SERVER_U2")
                }
            } else {
                column(name: "MAPPING_URL", type: "VARCHAR(200)", remarks: "映射地址") {
                    constraints(nullable: "false")
                }
            }

            column(name: "ENABLE_FLAG", type: "VARCHAR(1)", defaultValue: "Y", remarks: "是否启用") {
                constraints(nullable: "false")
            }

            column(name: "AUTH_TYPE", type: "VARCHAR(50)", remarks: "校验模式", defaultValue: "NONE")
            column(name: "GRANT_TYPE", type: "VARCHAR(50)", remarks: "授权模式")
            column(name: "ACCESS_TOKEN_URL", type: "VARCHAR(255)", remarks: "获取token的url")
            column(name: "CLIENT_ID", type: "VARCHAR(255)", remarks: "client id")
            column(name: "CLIENT_SECRET", type: "VARCHAR(255)", remarks: "client secret")
            column(name: "AUTH_USERNAME", type: "VARCHAR(80)", remarks: "校验用户名")
            column(name: "AUTH_PASSWORD", type: "VARCHAR(200)", remarks: "校验密码")
            column(name: "SCOPE", type: "VARCHAR(100)", remarks: "权限范围")
            column(name: "NAMESPACE", type: "VARCHAR(30)", remarks: "SOAP命名空间")
            column(name: "ELEMENT_FORM_DEFAULT", type: "VARCHAR(30)", remarks: "参数前缀标识")
            column(name: "WSS_PASSWORD_TYPE", type: "VARCHAR(50)", remarks: "加密类型")
            column(name: "USERNAME", type: "VARCHAR(255)", remarks: "认证用户名")
            column(name: "PASSWORD", type: "VARCHAR(255)", remarks: "认证密码")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "SERVER_ID,CODE", tableName: "API_CONFIG_SERVER", constraintName: "API_CONFIG_SERVER_U1")
            addUniqueConstraint(columnNames: "SERVER_ID,MAPPING_URL", tableName: "API_CONFIG_SERVER", constraintName: "API_CONFIG_SERVER_U2")
        }

        createIndex(tableName: "API_CONFIG_SERVER", indexName: "API_CONFIG_SERVER_N1") { column(name: "NAME", type: "VARCHAR(200)") }
        createIndex(tableName: "API_CONFIG_SERVER", indexName: "API_CONFIG_SERVER_N2") { column(name: "SERVICE_TYPE", type: "VARCHAR(10)") }
        createIndex(tableName: "API_CONFIG_SERVER", indexName: "API_CONFIG_SERVER_N3") { column(name: "ENABLE_FLAG", type: "VARCHAR(1)") }
    }

    changeSet(author: "peng.jiang", id: "20171025-api-config-interface") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'API_CONFIG_INTERFACE_S', startValue: "10001")
        }

        createTable(tableName: "API_CONFIG_INTERFACE", remarks: "服务注册功能-接口配置表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "INTERFACE_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CONFIG_INTERFACE_PK")
                }
            } else {
                column(name: "INTERFACE_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CONFIG_INTERFACE_PK")
                }
            }

            column(name: "SERVER_ID", type: "BIGINT", remarks: "服务Id") {
                constraints(nullable: "false")
            }
            column(name: "CODE", type: "VARCHAR(30)", remarks: "接口代码") {
                constraints(nullable: "false")
            }
            column(name: "NAME", type: "VARCHAR(200)", remarks: "接口名称") {
                constraints(nullable: "false")
            }
            column(name: "SOAP_VERSION", type: "VARCHAR(50)", remarks: "SOAP版本")
            column(name: "INTERFACE_URL", type: "VARCHAR(200)", remarks: "接口地址")
            column(name: "MAPPING_URL", type: "VARCHAR(200)", remarks: "映射地址") {
                constraints(nullable: "false")
            }
            column(name: "ENABLE_FLAG", type: "VARCHAR(1)", defaultValue: "Y", remarks: "是否启用") {
                constraints(nullable: "false")
            }
            column(name: "MAPPING_CLASS", type: "VARCHAR(255)", remarks: "映射类")
            column(name: "REQUEST_METHOD", type: "VARCHAR(10)", remarks: "请求方式")
            column(name: "REQUEST_HEAD", type: "VARCHAR(2000)", remarks: "请求头")
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "接口描述")
            column(name: "SOAP_ACTION", type: "VARCHAR(255)", remarks: "SOAPACTION")
            column(name: "INVOKE_RECORD_DETAILS", type: "VARCHAR(1)", defaultValue: "N", remarks: "是否记录调用详情") {
                constraints(nullable: "false")
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        createIndex(tableName: "API_CONFIG_INTERFACE", indexName: "API_CONFIG_INTERFACE_N1") { column(name: "SERVER_ID", type: "BIGINT") }
    }


    changeSet(author: "peng.jiang", id: "20171025-api-config-application") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'API_CONFIG_APPLICATION_S', startValue: "10001")
        }

        createTable(tableName: "API_CONFIG_APPLICATION", remarks: "服务注册功能-应用配置表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "APPLICATION_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CONFIG_APPLICATION_PK")
                }
            } else {
                column(name: "APPLICATION_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CONFIG_APPLICATION_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "CODE", type: "VARCHAR(30)", remarks: "应用代码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "API_CONFIG_APPLICATION_U1")
                }
            } else {
                column(name: "CODE", type: "VARCHAR(30)", remarks: "应用代码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "NAME", type: "VARCHAR(200)", remarks: "应用名称")
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "应用描述")
            column(name: "CLI_ID", type: "BIGINT", remarks: "client表主键ID") {
                constraints(nullable: "false")
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")


        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "APPLICATION_ID,CODE", tableName: "API_CONFIG_APPLICATION", constraintName: "API_CONFIG_APPLICATION_U1")
        }


        createIndex(tableName: "API_CONFIG_APPLICATION", indexName: "API_CONFIG_APPLICATION_N1") { column(name: "NAME", type: "VARCHAR(200)") }
    }


    changeSet(author: "lijian.yin", id: "20171025-api-client-interface-limit") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'API_CLIENT_INTERFACE_LIMIT_S', startValue: "10001")
        }
        createTable(tableName: "API_CLIENT_INTERFACE_LIMIT", remarks: "服务访问次数限制表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CLIENT_INTERFACE_LIMIT_PK")
                }
            } else {
                column(name: "ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_CLIENT_INTERFACE_LIMIT_PK")
                }
            }

            column(name: "CLIENT_ID", type: "VARCHAR(100)", remarks: "CLIENT_ID") {
                constraints(nullable: "false")
            }
            column(name: "SERVER_CODE", type: "VARCHAR(100)", remarks: "SERVER_CODE") {
                constraints(nullable: "false")
            }
            column(name: "INTERFACE_CODE", type: "VARCHAR(100)", remarks: "INTERFACE_CODE") {
                constraints(nullable: "false")
            }
            column(name: "ACCESS_FLAG", type: "VARCHAR(1)", remarks: "是否授权") {
                constraints(nullable: "false")
            }
            column(name: "ACCESS_FREQUENCY", type: "BIGINT", remarks: "每分钟访问次数")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }

    }



    changeSet(author: "peng.jiang", id: "20171025-api-invoke-record") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'API_INVOKE_RECORD_S', startValue: "10001")
        }
        createTable(tableName: "API_INVOKE_RECORD", remarks: "服务注册功能-服务调用记录表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RECORD_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_INVOKE_RECORD_PK")
                }
            } else {
                column(name: "RECORD_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_INVOKE_RECORD_PK")
                }
            }

            column(name: "INVOKE_ID", type: "VARCHAR(100)", remarks: "调用记录唯一标识");
            column(name: "APPLICATION_CODE", type: "VARCHAR(30)", remarks: "应用代码");
            column(name: "SERVER_CODE", type: "VARCHAR(30)", remarks: "服务代码");
            column(name: "SERVER_NAME", type: "VARCHAR(200)", remarks: "服务名称");
            column(name: "API_URL", type: "VARCHAR(255)", remarks: "API路径");
            column(name: "CLIENT_ID", type: "VARCHAR(200)", remarks: "clientId");
            column(name: "REQUEST_TIME", type: "DATETIME", remarks: "请求时间");
            column(name: "REQUEST_METHOD", type: "VARCHAR(10)", remarks: "请求方式")
            column(name: "IP", type: "VARCHAR(40)", remarks: "ip地址")
            column(name: "API_RESPONSE_TIME", type: "BIGINT", remarks: "API响应时间")
            column(name: "REQUEST_URL", type: "VARCHAR(255)", remarks: "请求路径")
            column(name: "REFERER", type: "VARCHAR(240)")
            column(name: "USER_AGENT", type: "VARCHAR(240)")
            column(name: "RESPONSE_TIME", type: "BIGINT", remarks: "响应时间")
            column(name: "INTERFACE_TYPE", type: "VARCHAR(50)", remarks: "接口类型 SOAP/REST")
            column(name: "RESPONSE_CODE", type: "VARCHAR(10)", remarks: "响应状态代码")
            column(name: "RESPONSE_STATUS", type: "VARCHAR(10)", remarks: "响应状态")


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }

        createIndex(tableName: "API_INVOKE_RECORD", indexName: "API_INVOKE_RECORD_N1") { column(name: "APPLICATION_CODE", type: "VARCHAR(30)") }
        createIndex(tableName: "API_INVOKE_RECORD", indexName: "API_INVOKE_RECORD_N2") { column(name: "CLIENT_ID", type: "VARCHAR(200)") }
        createIndex(tableName: "API_INVOKE_RECORD", indexName: "API_INVOKE_RECORD_N3") { column(name: "INVOKE_ID", type: "VARCHAR(100)") }
        createIndex(tableName: "API_INVOKE_RECORD", indexName: "API_INVOKE_RECORD_N4") { column(name: "SERVER_CODE", type: "VARCHAR(30)") }
        createIndex(tableName: "API_INVOKE_RECORD", indexName: "API_INVOKE_RECORD_N5") { column(name: "RESPONSE_STATUS", type: "VARCHAR(10)") }
        createIndex(tableName: "API_INVOKE_RECORD", indexName: "API_INVOKE_RECORD_N6") { column(name: "REQUEST_TIME", type: "DATETIME") }
    }

    changeSet(author: "peng.jiang", id: "20171025-api-invoke-record-details") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'API_INVOKE_RECORD_DETAILS_S', startValue: "10001")
        }

        createTable(tableName: "API_INVOKE_RECORD_DETAILS", remarks: "服务注册功能-服务调用记录详情表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RECORD_DETAILS_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_INVOKE_RECORD_DETAILS_PK")
                }
            } else {
                column(name: "RECORD_DETAILS_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "API_INVOKE_RECORD_DETAILS_PK")
                }
            }

            column(name: "RECORD_ID", type: "BIGINT", remarks: "调用记录Id") {
                constraints(nullable: "false")
            }
            column(name: "API_REQUEST_BODY_PARAMETER", type: "CLOB", remarks: "API请求body参数")
            column(name: "API_RESPONSE_CONTENT", type: "CLOB", remarks: "API响应内容")
            column(name: "REQUEST_HEADER_PARAMETER", type: "VARCHAR(2000)", remarks: "请求header参数")
            column(name: "REQUEST_BODY_PARAMETER", type: "CLOB", remarks: "请求body参数")
            column(name: "RESPONSE_CONTENT", type: "CLOB", remarks: "响应内容")
            column(name: "STACKTRACE", type: "CLOB", remarks: "错误堆栈")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }

        createIndex(tableName: "API_INVOKE_RECORD_DETAILS", indexName: "API_INVOKE_RECORD_DETAILS_N1") { column(name: "RECORD_ID", type: "BIGINT") }
    }
}

databaseChangeLog(logicalFilePath: "patch.groovy") {

    if (!helper.isHana()) {
        changeSet(author: "jiangpeng", id: "api_config_server-alter-namespace-length-1") {
            modifyDataType(tableName: "API_CONFIG_SERVER", columnName: "NAMESPACE", newDataType: "VARCHAR(500)")
        }
    } else {
        changeSet(author: "jiangpeng", id: "api_config_server-alter-namespace-length-1-hana") {
            sqlFile(path: "script/db/data/hana/patch/api_config_server-alter-namespace-length.sql", encoding: "UTF-8")
        }
    }

}