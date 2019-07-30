package script.db

databaseChangeLog(logicalFilePath: "2017-03-16-init-table-migration.groovy") {

    changeSet(author: "jialong.zuo", id: "20170427-shortcut-1") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_USER_SHORTCUT_S', startValue: "10001")
        }
        createTable(tableName: "SYS_USER_SHORTCUT", remarks: "主页快捷方式") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "SHORTCUT_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_SHORTCUT_PK")
                }
            } else {
                column(name: "SHORTCUT_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_SHORTCUT_PK")
                }
            }

            column(name: "USER_ID", type: "BIGINT", remarks: "用户id") {
                constraints(nullable: "false")
            }
            column(name: "FUNCTION_CODE", type: "VARCHAR(50)", remarks: "功能CODE") {
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
    }

    changeSet(author: 'qixiangyu', id: '2017-05-04-tokenLogs-1') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_TOKEN_LOGS_S', startValue: "10001")
        }

        createTable(tableName: "SYS_TOKEN_LOGS") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: 'ID', type: 'BIGINT', autoIncrement: "true", remarks: '日志ID') {
                    constraints(primaryKey: "true")
                }
            } else {
                column(name: 'ID', type: 'BIGINT', remarks: '日志ID') {
                    constraints(primaryKey: "true")
                }
            }

            column(name: 'USER_ID', type: 'BIGINT', remarks: '用户ID')
            column(name: 'CLIENT_ID', type: 'VARCHAR(100)', remarks: '客户端ID') {
                constraints(nullable: "false")
            }
            column(name: 'TOKEN', type: 'VARCHAR(1024)', remarks: 'token')
            column(name: 'TOKEN_ACCESS_TIME', type: 'DATETIME', remarks: 'token获取日期')
            column(name: 'TOKEN_ACCESS_TYPE', type: 'VARCHAR(32)', remarks: 'token获取方式')
            column(name: 'TOKEN_EXPIRES_TIME', type: 'DATETIME', remarks: 'token失效日期')
            column(name: 'REVOKE_FLAG', type: 'VARCHAR(5)', remarks: '是否有效', defaultValue: "Y")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: "SYS_TOKEN_LOGS", indexName: "SYS_TOKEN_LOGS_N1") { column(name: "USER_ID", type: "BIGINT") }
        createIndex(tableName: "SYS_TOKEN_LOGS", indexName: "SYS_TOKEN_LOGS_N2") { column(name: "CLIENT_ID", type: "BIGINT") }
    }

    changeSet(author: "jialong.zuo", id: "20170504-flexfield-1") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FND_FLEX_MODEL_S', startValue: "10001")
        }
        createTable(tableName: "FND_FLEX_MODEL", remarks: "弹性域表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "MODEL_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_MODEL_PK")
                }
            } else {
                column(name: "MODEL_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_MODEL_PK")
                }
            }

            column(name: "MODEL_CODE", type: "VARCHAR(50)", remarks: "模型代码") {
                constraints(nullable: "false")
            }
            column(name: "MODEL_NAME", type: "VARCHAR(80)", remarks: "模型名称") {
                constraints(nullable: "false")
            }
            column(name: "MODEL_TABLE", type: "VARCHAR(50)", remarks: "模型表") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "MODEL_CODE", tableName: "FND_FLEX_MODEL", constraintName: "FND_FLEX_MODEL_U1")
        } else {
            addUniqueConstraint(columnNames: "MODEL_ID,MODEL_CODE", tableName: "FND_FLEX_MODEL", constraintName: "FND_FLEX_MODEL_U1")
        }

    }
    changeSet(author: "jialong.zuo", id: "20170504-flexfield-2") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FND_FLEX_MODEL_COLUMN_S', startValue: "10001")
        }
        createTable(tableName: "FND_FLEX_MODEL_COLUMN", remarks: "弹性域表属性") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "MODEL_COLUMN_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_MODEL_COLUMN_PK")
                }
            } else {
                column(name: "MODEL_COLUMN_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_MODEL_COLUMN_PK")
                }
            }

            column(name: "MODEL_ID", type: "BIGINT", remarks: "模型ID") {
                constraints(nullable: "false")
            }
            column(name: "COLUMN_NAME", type: "VARCHAR(50)", remarks: "模型弹性域") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "VARCHAR(80)", remarks: "弹性域描述")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "MODEL_ID,COLUMN_NAME", tableName: "FND_FLEX_MODEL_COLUMN", constraintName: "FND_FLEX_MODEL_COLUMN_U1")
        } else {
            addUniqueConstraint(columnNames: "MODEL_COLUMN_ID,MODEL_ID,COLUMN_NAME", tableName: "FND_FLEX_MODEL_COLUMN", constraintName: "FND_FLEX_MODEL_COLUMN_U1")
        }

    }


    changeSet(author: "jialong.zuo", id: "20170504-flexfield-3") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FND_FLEX_RULE_SET_S', startValue: "10001")
        }
        createTable(tableName: "FND_FLEX_RULE_SET", remarks: "model规则集") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RULE_SET_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_SET_PK")
                }
            } else {
                column(name: "RULE_SET_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_SET_PK")
                }
            }

            column(name: "RULE_SET_CODE", type: "VARCHAR(50)", remarks: "规则集CODE") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "VARCHAR(50)", remarks: "规则集描述")

            column(name: "MODEL_ID", type: "BIGINT", remarks: "弹性域模板id") {
                constraints(nullable: "false")
            }
            column(name: "ENABLE_FLAG", type: "VARCHAR(2)", defaultValue: "Y", remarks: "是否启用") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "RULE_SET_CODE", tableName: "FND_FLEX_RULE_SET", constraintName: "FND_FLEX_RULE_SET_U1")
        } else {
            addUniqueConstraint(columnNames: "RULE_SET_ID,RULE_SET_CODE", tableName: "FND_FLEX_RULE_SET", constraintName: "FND_FLEX_RULE_SET_U1")
        }

    }

    changeSet(author: "jialong.zuo", id: "20170504-flexfield-4") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FND_FLEX_RULE_S', startValue: "10001")
        }
        createTable(tableName: "FND_FLEX_RULE", remarks: "弹性域表属性") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RULE_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_PK")
                }
            } else {
                column(name: "RULE_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_PK")
                }
            }

            column(name: "RULE_SET_ID", type: "BIGINT", remarks: "规则集id") {
                constraints(nullable: "false")
            }
            column(name: "RULE_CODE", type: "VARCHAR(50)", remarks: "规则code") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "VARCHAR(80)", remarks: "规则描述") {
                constraints(nullable: "false")
            }
            column(name: "ENABLE_FLAG", type: "VARCHAR(2)", defaultValue: "Y", remarks: "是否启用") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "RULE_CODE", tableName: "FND_FLEX_RULE", constraintName: "FND_FLEX_RULE_U1")
        } else {
            addUniqueConstraint(columnNames: "RULE_ID,RULE_CODE", tableName: "FND_FLEX_RULE", constraintName: "FND_FLEX_RULE_U1")
        }

    }

    changeSet(author: "jialong.zuo", id: "20170504-flexfield-5") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FND_FLEX_RULE_DETAIL_S', startValue: "10001")
        }
        createTable(tableName: "FND_FLEX_RULE_DETAIL", remarks: "弹性域表属性") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "DETAIL_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_DETAIL_PK")
                }
            } else {
                column(name: "DETAIL_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_DETAIL_PK")
                }
            }

            column(name: "RULE_ID", type: "BIGINT", remarks: "规则id") {
                constraints(nullable: "false")
            }
            column(name: "FIELD_NAME", type: "VARCHAR(50)", remarks: "规则name") {
                constraints(nullable: "false")
            }
            column(name: "FIELD_TYPE", type: "VARCHAR(500)", remarks: "规则Type") {
                constraints(nullable: "false")
            }
            column(name: "FIELD_VALUE", type: "VARCHAR(50)", remarks: "规则value") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "RULE_ID,FIELD_NAME", tableName: "FND_FLEX_RULE_DETAIL", constraintName: "FND_FLEX_RULE_DETAIL_U1")
        } else {
            addUniqueConstraint(columnNames: "DETAIL_ID,RULE_ID,FIELD_NAME", tableName: "FND_FLEX_RULE_DETAIL", constraintName: "FND_FLEX_RULE_DETAIL_U1")
        }

    }

    changeSet(author: "jialong.zuo", id: "20170504-flexfield-6") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FND_FLEX_RULE_FIELD_S', startValue: "10001")
        }
        createTable(tableName: "FND_FLEX_RULE_FIELD", remarks: "弹性域表属性") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "FIELD_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_FIELD_PK")
                }
            } else {
                column(name: "FIELD_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_FLEX_RULE_FIELD_PK")
                }
            }

            column(name: "RULE_ID", type: "BIGINT", remarks: "规则id") {
                constraints(nullable: "false")
            }
            column(name: "MODEL_COLUMN_ID", type: "BIGINT", remarks: "弹性域id") {
                constraints(nullable: "false")
            }
            column(name: "FIELD_SEQUENCE", type: "BIGINT", remarks: "field序号") {
                constraints(nullable: "false")
            }
            column(name: "FIELD_COLUMN_NUMBER", type: "BIGINT", remarks: "field行号") {
                constraints(nullable: "false")
            }
            column(name: "FIELD_COLUMN_WIDTH", type: "BIGINT", remarks: "field宽度") {
                constraints(nullable: "false")
            }
            column(name: "FIELD_TYPE", type: "VARCHAR(500)", remarks: "渲染框体类型") {
                constraints(nullable: "false")
            }
            column(name: "READABLE_FLAG", type: "VARCHAR(2)", remarks: "是否只读", defaultValue: "N") {
                constraints(nullable: "false")
            }
            column(name: "REQUIRED_FLAG", type: "VARCHAR(2)", remarks: "是否必输", defaultValue: "Y") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "RULE_ID,MODEL_COLUMN_ID", tableName: "FND_FLEX_RULE_FIELD", constraintName: "FND_FLEX_RULE_FIELD_U1")
        } else {
            addUniqueConstraint(columnNames: "FIELD_ID,RULE_ID,MODEL_COLUMN_ID", tableName: "FND_FLEX_RULE_FIELD", constraintName: "FND_FLEX_RULE_FIELD_U1")
        }

    }

    changeSet(author: "qixiangyu", id: "20170817-sys-code-rules-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_CODE_RULES_HEADER_S', startValue: "10001")
        }
        createTable(tableName: "SYS_CODE_RULES_HEADER", remarks: "编码规则头表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "HEADER_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_RULES_HEADER_PK")
                }
            } else {
                column(name: "HEADER_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_RULES_HEADER_PK")
                }
            }

            column(name: "RULE_CODE", type: "VARCHAR(50)", remarks: "编码规则CODE") {
                constraints(nullable: "false")
            }
            column(name: "RULE_NAME", type: "VARCHAR(50)", remarks: "名称")
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "描述")
            column(name: "ENABLE_FLAG", type: "VARCHAR(5)", remarks: "启用标志")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "RULE_CODE", tableName: "SYS_CODE_RULES_HEADER", constraintName: "SYS_CODE_RULES_HEADER_U1")
        } else {
            addUniqueConstraint(columnNames: "HEADER_ID,RULE_CODE", tableName: "SYS_CODE_RULES_HEADER", constraintName: "SYS_CODE_RULES_HEADER_U1")
        }

    }

    changeSet(author: "qixiangyu", id: "20170817-sys-code-rules-line-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_CODE_RULES_LINE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_CODE_RULES_LINE", remarks: "编码规则行表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "LINE_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_RULES_LINE_PK")
                }
            } else {
                column(name: "LINE_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CODE_RULES_LINE_PK")
                }
            }

            column(name: "HEADER_ID", type: "BIGINT", remarks: "编码头ID") {
                constraints(nullable: "false")
            }
            column(name: "FILED_TYPE", type: "VARCHAR(10)", remarks: "编码段类型") {
                constraints(nullable: "false")
            }
            column(name: "FILED_VALUE", type: "VARCHAR(100)", remarks: "编码段值")
            column(name: "FIELD_SEQUENCE", type: "BIGINT", remarks: "序号")
            column(name: "DATE_MASK", type: "VARCHAR(50)", remarks: "日期掩码")
            column(name: "SEQ_LENGTH", type: "INT", remarks: "序列长度")
            column(name: "START_VALUE", type: "BIGINT", remarks: "序列开始值")
            column(name: "CURRENT_VALUE", type: "BIGINT", remarks: "序列号段当前值")
            column(name: "RESET_FREQUENCY", type: "VARCHAR(10)", remarks: "重置频率")
            column(name: "RESET_DATE", type: "DATETIME", remarks: "上次重置日期")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
    }

    changeSet(author: "qiangzeng", id: "20170904-sys-resource-item-assign-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_RESOURCE_ITEM_ASSIGN_S', startValue: "10001")
        }
        createTable(tableName: "SYS_RESOURCE_ITEM_ASSIGN", remarks: "资源组件分配") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ASSIGN_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_ITEM_ASSIGN_PK")
                }
            } else {
                column(name: "ASSIGN_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_ITEM_ASSIGN_PK")
                }
            }

            column(name: "ASSIGN_TYPE", type: "VARCHAR(50)", remarks: "分配类型")
            column(name: "TYPE_ID", type: "BIGINT", remarks: "类型ID")
            column(name: "ELEMENT_ID", type: "BIGINT", remarks: "组件元素ID")
            column(name: "FUNCTION_ID", type: "BIGINT", remarks: "功能ID")
            column(name: "ENABLE", type: "VARCHAR(50)", remarks: "是否启用")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "qiangzeng", id: "20170904-sys-resource-item-element-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_RESOURCE_ITEM_ELEMENT_S', startValue: "10001")
        }
        createTable(tableName: "SYS_RESOURCE_ITEM_ELEMENT", remarks: "资源组件元素") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ELEMENT_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_ITEM_ELEMENT_PK")
                }
            } else {
                column(name: "ELEMENT_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_ITEM_ELEMENT_PK")
                }
            }

            column(name: "TYPE", type: "VARCHAR(20)", remarks: "类型") {
                constraints(nullable: "false")
            }
            column(name: "PROPERTY", type: "VARCHAR(20)", remarks: "属性") {
                constraints(nullable: "false")
            }
            column(name: "PROPERTY_VALUE", type: "VARCHAR(20)", remarks: "属性值") {
                constraints(nullable: "false")
            }
            column(name: "NAME", type: "VARCHAR(20)", remarks: "名称")
            column(name: "RESOURCE_ITEM_ID", type: "BIGINT", remarks: "资源组件ID")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "jialong.zuo", id: "20170829-permission-rule-init-table") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PERMISSION_RULE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PERMISSION_RULE", remarks: "规则屏蔽管理表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RULE_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_RULE_PK")
                }
            } else {
                column(name: "RULE_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_RULE_PK")
                }
            }

            column(name: "RULE_CODE", type: "VARCHAR(250)", remarks: "屏蔽规则code") {
                constraints(nullable: "false")
            }
            column(name: "RULE_NAME", type: "VARCHAR(250)", remarks: "屏蔽规则NAME") {
                constraints(nullable: "false")
            }
            column(name: "PERMISSION_FIELD", type: "VARCHAR(100)", remarks: "安全性字段") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "RULE_CODE", tableName: "SYS_PERMISSION_RULE", constraintName: "SYS_PERMISSION_RULE_U1")
        } else {
            addUniqueConstraint(columnNames: "RULE_ID,RULE_CODE", tableName: "SYS_PERMISSION_RULE", constraintName: "SYS_PERMISSION_RULE_U1")
        }

        createIndex(tableName: "SYS_PERMISSION_RULE", indexName: "SYS_PERMISSION_RULE_N1") { column(name: "RULE_NAME", type: "VARCHAR(250)") }

    }


    changeSet(author: "jialong.zuo", id: "20170829-permission-rule-detail-init-table") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PERMISSION_RULE_DETAIL_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PERMISSION_RULE_DETAIL", remarks: "规则屏蔽管理表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "DETAIL_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_RULE_DETAIL_PK")
                }
            } else {
                column(name: "DETAIL_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_RULE_DETAIL_PK")
                }
            }

            column(name: "RULE_ID", type: "BIGINT", remarks: "MANGE CODE") {
                constraints(nullable: "false")
            }
            column(name: "PERMISSION_FIELD_VALUE", type: "BIGINT", remarks: "安全性字段值")

            column(name: "PERMISSION_FIELD_SQL_VALUE", type: "VARCHAR(2000)", remarks: "安全性sql字段值")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "PERMISSION_FIELD_VALUE,RULE_ID", tableName: "SYS_PERMISSION_RULE_DETAIL", constraintName: "SYS_PERMISSION_RULE_DETAIL_U1")
        } else {
            addUniqueConstraint(columnNames: "DETAIL_ID,PERMISSION_FIELD_VALUE,RULE_ID", tableName: "SYS_PERMISSION_RULE_DETAIL", constraintName: "SYS_PERMISSION_RULE_DETAIL_U1")
        }

    }

    changeSet(author: "jialong.zuo", id: "20170829-permission-rule-assign-init-table") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PERMISSION_RULE_ASSIGN_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PERMISSION_RULE_ASSIGN", remarks: "规则屏蔽管理表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ASSIGN_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_RULE_ASSIGN_PK")
                }
            } else {
                column(name: "ASSIGN_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_RULE_ASSIGN_PK")
                }
            }

            column(name: "DETAIL_ID", type: "BIGINT", remarks: "MANGE CODE") {
                constraints(nullable: "false")
            }
            column(name: "ASSIGN_FIELD", type: "VARCHAR(250)", remarks: "层级") {
                constraints(nullable: "false")
            }
            column(name: "ASSIGN_FIELD_VALUE", type: "VARCHAR(250)", remarks: "层级值") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "DETAIL_ID,ASSIGN_FIELD,ASSIGN_FIELD_VALUE", tableName: "SYS_PERMISSION_RULE_ASSIGN", constraintName: "SYS_PERMISSION_RULE_ASSIGN_U1")
        } else {
            addUniqueConstraint(columnNames: "ASSIGN_ID,DETAIL_ID,ASSIGN_FIELD,ASSIGN_FIELD_VALUE", tableName: "SYS_PERMISSION_RULE_ASSIGN", constraintName: "SYS_PERMISSION_RULE_ASSIGN_U1")
        }

    }


    changeSet(author: "jialong.zuo", id: "20170829-permission-table-init-table") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PERMISSION_TABLE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PERMISSION_TABLE", remarks: "规则屏蔽管理表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "TABLE_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_TABLE_PK")
                }
            } else {
                column(name: "TABLE_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_TABLE_PK")
                }
            }

            column(name: "TABLE_NAME", type: "VARCHAR(250)", remarks: "MANGE 表名") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "VARCHAR(250)", remarks: "描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "TABLE_NAME", tableName: "SYS_PERMISSION_TABLE", constraintName: "SYS_PERMISSION_TABLE_U1")
        } else {
            addUniqueConstraint(columnNames: "TABLE_ID,TABLE_NAME", tableName: "SYS_PERMISSION_TABLE", constraintName: "SYS_PERMISSION_TABLE_U1")
        }


    }


    changeSet(author: "jialong.zuo", id: "20170829-permission-table-rule-init-table") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PERMISSION_TABLE_RULE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PERMISSION_TABLE_RULE", remarks: "规则屏蔽管理表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "TABLE_RULE_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_TABLE_RULE_PK")
                }
            } else {
                column(name: "TABLE_RULE_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PERMISSION_TABLE_RULE_PK")
                }
            }

            column(name: "TABLE_ID", type: "BIGINT", remarks: "table Id") {
                constraints(nullable: "false")
            }
            column(name: "TABLE_FIELD", type: "VARCHAR(250)", remarks: "关联表字段") {
                constraints(nullable: "false")
            }
            column(name: "RULE_ID", type: "BIGINT", remarks: "规则ID") {
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
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "TABLE_ID,TABLE_FIELD,RULE_ID", tableName: "SYS_PERMISSION_TABLE_RULE", constraintName: "SYS_PERMISSION_TABLE_RULE_U1")
        } else {
            addUniqueConstraint(columnNames: "TABLE_RULE_ID,TABLE_ID,TABLE_FIELD,RULE_ID", tableName: "SYS_PERMISSION_TABLE_RULE", constraintName: "SYS_PERMISSION_TABLE_RULE_U1")
        }

    }


}