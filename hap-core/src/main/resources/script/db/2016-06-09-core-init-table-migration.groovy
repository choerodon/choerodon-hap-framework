package script.db

databaseChangeLog(logicalFilePath: "2016-06-09-init-table-migration.groovy") {

    changeSet(author: "hailor", id: "20160609-hailor-4") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_FUNCTION_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_FUNCTION_B") {

            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "FUNCTION_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_FUNCTION_B_PK")
                }
            } else {
                column(name: "FUNCTION_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_FUNCTION_B_PK")
                }
            }

            column(name: "MODULE_CODE", type: "varchar(30)", remarks: "模块编码")
            column(name: "FUNCTION_ICON", type: "varchar(100)", remarks: "功能图标")
            if (!helper.isPostgresql()) {
                column(name: "FUNCTION_CODE", type: "varchar(30)", remarks: "功能编码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_FUNCTION_B_U1")
                }
            } else {
                column(name: "FUNCTION_CODE", type: "varchar(30)", remarks: "功能编码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "FUNCTION_NAME", type: "varchar(150)", remarks: "功能名称")
            column(name: "FUNCTION_DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "RESOURCE_ID", type: "bigint", remarks: "功能入口")
            column(name: "TYPE", type: "varchar(30)", remarks: "功能类型")
            column(name: "PARENT_FUNCTION_ID", type: "bigint", remarks: "父功能")
            column(name: "ENABLED_FLAG", type: "varchar(1)", remarks: "是否启用", defaultValue: "Y")
            column(name: "FUNCTION_SEQUENCE", type: "decimal(20,0)", remarks: "排序号", defaultValue: "1")
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
            addUniqueConstraint(columnNames: "FUNCTION_ID,FUNCTION_CODE", tableName: "SYS_FUNCTION_B", constraintName: "SYS_FUNCTION_B_U1")
        }

        createIndex(tableName: "SYS_FUNCTION_B", indexName: "SYS_FUNCTION_B_N1") { column(name: "PARENT_FUNCTION_ID", type: "bigint") }

        createTable(tableName: "SYS_FUNCTION_TL") {
            column(name: "FUNCTION_ID", type: "bigint", remarks: "功能ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "FUNCTION_NAME", type: "varchar(150)", remarks: "功能名")
            column(name: "FUNCTION_DESCRIPTION", type: "varchar(240)", remarks: "功能描述")
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

    changeSet(author: "hailor", id: "20160609-hailor-5") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_FUNCTION_RESOURCE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_FUNCTION_RESOURCE") {

            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "FUNC_SRC_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_FUNCTION_RESOURCE_PK")
                }
            } else {
                column(name: "FUNC_SRC_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_FUNCTION_RESOURCE_PK")
                }
            }

            column(name: "FUNCTION_ID", type: "bigint", remarks: "外键，功能 ID")
            column(name: "RESOURCE_ID", type: "bigint")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "FUNCTION_ID,RESOURCE_ID", tableName: "SYS_FUNCTION_RESOURCE", constraintName: "SYS_FUNCTION_RESOURCE_U1")
        } else {
            addUniqueConstraint(columnNames: "FUNC_SRC_ID,FUNCTION_ID,RESOURCE_ID", tableName: "SYS_FUNCTION_RESOURCE", constraintName: "SYS_FUNCTION_RESOURCE_U1")
        }


    }

    changeSet(author: "hailor", id: "20160609-hailor-6") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_JOB_RUNNING_INFO_S', startValue: "10001")
        }
        createTable(tableName: "SYS_JOB_RUNNING_INFO") {

            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "JOB_RUNNING_INFO_ID", type: "bigint", autoIncrement: "true", startWith: "10001") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_JOB_RUNNING_INFO_PK")
                }
            } else {
                column(name: "JOB_RUNNING_INFO_ID", type: "bigint") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_JOB_RUNNING_INFO_PK")
                }
            }

            column(name: "JOB_NAME", type: "varchar(225)")
            column(name: "JOB_GROUP", type: "varchar(225)")
            column(name: "JOB_RESULT", type: "varchar(225)")
            column(name: "JOB_STATUS", type: "varchar(225)")
            column(name: "JOB_STATUS_MESSAGE", type: "varchar(225)")
            column(name: "TRIGGER_NAME", type: "varchar(225)")
            column(name: "TRIGGER_GROUP", type: "varchar(225)")
            column(name: "PREVIOUS_FIRE_TIME", type: "datetime")
            column(name: "FIRE_TIME", type: "datetime")
            column(name: "NEXT_FIRE_TIME", type: "datetime")
            column(name: "REFIRE_COUNT", type: "bigint", defaultValue: "0")
            column(name: "FIRE_INSTANCE_ID", type: "varchar(225)")
            column(name: "SCHEDULER_INSTANCE_ID", type: "varchar(225)")
            column(name: "SCHEDULED_FIRE_TIME", type: "datetime")
            column(name: "EXECUTION_SUMMARY", type: "varchar(225)")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "hailor", id: "20160609-hailor-12") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PROFILE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PROFILE") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "PROFILE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PROFILE_PK")
                }
            } else {
                column(name: "PROFILE_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PROFILE_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "PROFILE_NAME", type: "varchar(40)", remarks: "配置文件名称") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_PROFILE_U1")
                }
            } else {
                column(name: "PROFILE_NAME", type: "varchar(40)", remarks: "配置文件名称") {
                    constraints(nullable: "false")
                }
            }

            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "PROFILE_ID,PROFILE_NAME", tableName: "SYS_PROFILE", constraintName: "SYS_PROFILE_U1")
        }
    }

    changeSet(author: "hailor", id: "20160609-hailor-13") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PROFILE_VALUE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PROFILE_VALUE") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "PROFILE_VALUE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PROFILE_VALUE_PK")
                }
            } else {
                column(name: "PROFILE_VALUE_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PROFILE_VALUE_PK")
                }
            }

            column(name: "PROFILE_ID", type: "varchar(32)", remarks: "配置文件ID")
            column(name: "LEVEL_ID", type: "varchar(32)", remarks: "层次ID")
            column(name: "LEVEL_VALUE", type: "varchar(40)", remarks: "层次值")
            column(name: "PROFILE_VALUE", type: "varchar(80)", remarks: "配置文件值")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(tableName: "SYS_PROFILE_VALUE", columnNames: "PROFILE_ID,LEVEL_ID,LEVEL_VALUE", constraintName: "SYS_PROFILE_VALUE_U1")
        } else {
            addUniqueConstraint(tableName: "SYS_PROFILE_VALUE", columnNames: "PROFILE_VALUE_ID,PROFILE_ID,LEVEL_ID,LEVEL_VALUE", constraintName: "SYS_PROFILE_VALUE_U1")
        }

    }

    changeSet(author: "hailor", id: "20160609-hailor-15") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_RESOURCE_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_RESOURCE_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RESOURCE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_B_PK")
                }
            } else {
                column(name: "RESOURCE_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_B_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "URL", type: "varchar(255)", remarks: "URL") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_RESOURCE_B_U1")
                }
            } else {
                column(name: "URL", type: "varchar(255)", remarks: "URL") {
                    constraints(nullable: "false")
                }
            }

            column(name: "TYPE", type: "varchar(15)", remarks: "资源类型")
            column(name: "NAME", type: "varchar(40)", remarks: "资源名称")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "LOGIN_REQUIRE", type: "varchar(1)", defaultValue: "Y", remarks: "是否需要登录")
            column(name: "ACCESS_CHECK", type: "varchar(1)", defaultValue: "Y", remarks: "是否权限校验")
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
            addUniqueConstraint(columnNames: "RESOURCE_ID,URL", tableName: "SYS_RESOURCE_B", constraintName: "SYS_RESOURCE_B_U1")
        }

        createTable(tableName: "SYS_RESOURCE_TL") {
            column(name: "RESOURCE_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "NAME", type: "varchar(40)", remarks: "资源名称")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
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

    changeSet(author: "hailor", id: "20160609-hailor-16") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_RESOURCE_ITEM_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_RESOURCE_ITEM_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RESOURCE_ITEM_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_ITEM_B_PK")
                }
            } else {
                column(name: "RESOURCE_ITEM_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_ITEM_B_PK")
                }
            }

            column(name: "OWNER_RESOURCE_ID", type: "bigint", remarks: "功能资源ID")
            column(name: "TARGET_RESOURCE_ID", type: "bigint", remarks: "目标资源ID")
            column(name: "ITEM_ID", type: "varchar(50)", remarks: "HTML中控件ID")
            column(name: "ITEM_NAME", type: "varchar(50)", remarks: "控件名称")
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")
            column(name: "ITEM_TYPE", type: "varchar(10)", remarks: "控件类型")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "ITEM_ID,OWNER_RESOURCE_ID", tableName: "SYS_RESOURCE_ITEM_B", constraintName: "SYS_RESOURCE_ITEM_B_U1")
        } else {
            addUniqueConstraint(columnNames: "RESOURCE_ITEM_ID,ITEM_ID,OWNER_RESOURCE_ID", tableName: "SYS_RESOURCE_ITEM_B", constraintName: "SYS_RESOURCE_ITEM_B_U1")
        }

        createIndex(tableName: "SYS_RESOURCE_ITEM_B", indexName: "SYS_RESOURCE_ITEM_B_N1") { column(name: "OWNER_RESOURCE_ID", type: "bigint") }


        createTable(tableName: "SYS_RESOURCE_ITEM_TL") {
            column(name: "RESOURCE_ITEM_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "ITEM_NAME", type: "varchar(150)", remarks: "控件名称")
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

    changeSet(author: "hailor", id: "20160609-hailor-17") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_ROLE_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_ROLE_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ROLE_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_B_PK")
                }
            } else {
                column(name: "ROLE_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_B_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "ROLE_CODE", type: "varchar(40)", remarks: "角色编码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_ROLE_B_U1")
                }
            } else {
                column(name: "ROLE_CODE", type: "varchar(40)", remarks: "角色编码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "ROLE_NAME", type: "varchar(150)", remarks: "角色名称") {
                constraints(nullable: "false")
            }
            column(name: "ROLE_DESCRIPTION", type: "varchar(240)", remarks: "角色描述")
            column(name: "START_ACTIVE_DATE", type: "DATE", remarks: "开始生效日期")
            column(name: "END_ACTIVE_DATE", type: "DATE", remarks: "截至生效日期")
            column(name: "ENABLE_FLAG", type: "VARCHAR(1)", remarks: "启用标记", defaultValue: "Y")
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
            addUniqueConstraint(columnNames: "ROLE_ID,ROLE_CODE", tableName: "SYS_ROLE_B", constraintName: "SYS_ROLE_B_U1")
        }

        createTable(tableName: "SYS_ROLE_TL") {
            column(name: "ROLE_ID", type: "bigint", remarks: "角色ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "ROLE_NAME", type: "varchar(150)", remarks: "角色名称")
            column(name: "ROLE_DESCRIPTION", type: "varchar(240)", remarks: "角色描述")
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

    changeSet(author: "hailor", id: "20160609-hailor-18") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_ROLE_FUNCTION_S', startValue: "10001")
        }
        createTable(tableName: "SYS_ROLE_FUNCTION") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "SRF_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_FUNCTION_PK")
                }
            } else {
                column(name: "SRF_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_FUNCTION_PK")
                }
            }

            column(name: "ROLE_ID", type: "bigint", remarks: "功能ID")
            column(name: "FUNCTION_ID", type: "bigint", remarks: "功能ID")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "ROLE_ID,FUNCTION_ID", tableName: "SYS_ROLE_FUNCTION", constraintName: "SYS_ROLE_FUNCTION_U1")
        } else {
            addUniqueConstraint(columnNames: "SRF_ID,ROLE_ID,FUNCTION_ID", tableName: "SYS_ROLE_FUNCTION", constraintName: "SYS_ROLE_FUNCTION_U1")
        }


    }

    changeSet(author: "hailor", id: "20160609-hailor-19") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_ROLE_RESOURCE_ITEM_S', startValue: "10001")
        }
        createTable(tableName: "SYS_ROLE_RESOURCE_ITEM") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RSI_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_RESOURCE_ITEM_PK")
                }
            } else {
                column(name: "RSI_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ROLE_RESOURCE_ITEM_PK")
                }
            }

            column(name: "ROLE_ID", type: "bigint", remarks: "角色ID")
            column(name: "RESOURCE_ITEM_ID", type: "bigint", remarks: "功能控件ID")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "ROLE_ID,RESOURCE_ITEM_ID", tableName: "SYS_ROLE_RESOURCE_ITEM", constraintName: "SYS_ROLE_RESOURCE_ITEM_U1")
        } else {
            addUniqueConstraint(columnNames: "RSI_ID,ROLE_ID,RESOURCE_ITEM_ID", tableName: "SYS_ROLE_RESOURCE_ITEM", constraintName: "SYS_ROLE_RESOURCE_ITEM_U1")
        }

        createIndex(tableName: "SYS_ROLE_RESOURCE_ITEM", indexName: "SYS_ROLE_RESOURCE_ITEM_N1") { column(name: "ROLE_ID", type: "bigint") }

    }

    changeSet(author: "hailor", id: "20160609-hailor-20") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_USER_S', startValue: "10001")
        }
        createTable(tableName: "SYS_USER") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "USER_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_PK")
                }
            } else {
                column(name: "USER_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_PK")
                }
            }

            column(name: "USER_TYPE", type: "varchar(30)", remarks: "用户类型")
            if (!helper.isPostgresql()) {
                column(name: "USER_NAME", type: "varchar(40)", remarks: "用户名") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_USER_U1")
                }
            } else {
                column(name: "USER_NAME", type: "varchar(40)", remarks: "用户名") {
                    constraints(nullable: "false")
                }
            }

            column(name: "PASSWORD_ENCRYPTED", type: "varchar(80)", remarks: "加密过的密码")
            column(name: "EMAIL", type: "varchar(150)", remarks: "邮箱地址")
            column(name: "PHONE", type: "varchar(40)", remarks: "电话号码")
            column(name: "START_ACTIVE_DATE", type: "datetime", remarks: "有效期从")
            column(name: "END_ACTIVE_DATE", type: "datetime", remarks: "有效期至")
            column(name: "STATUS", type: "varchar(30)", remarks: "状态")
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
            addUniqueConstraint(columnNames: "USER_ID,USER_NAME", tableName: "SYS_USER", constraintName: "SYS_USER_U1")
        }
    }

    changeSet(author: "hailor", id: "20160609-hailor-21") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_USER_ROLE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_USER_ROLE") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "SUR_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_ROLE_PK")
                }
            } else {
                column(name: "SUR_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_ROLE_PK")
                }
            }

            column(name: "USER_ID", type: "bigint", remarks: "用户ID")
            column(name: "ROLE_ID", type: "bigint", remarks: "角色ID")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "ROLE_ID,USER_ID", tableName: "SYS_USER_ROLE", constraintName: "SYS_USER_ROLE_U1")
        } else {
            addUniqueConstraint(columnNames: "SUR_ID,ROLE_ID,USER_ID", tableName: "SYS_USER_ROLE", constraintName: "SYS_USER_ROLE_U1")
        }


    }

    changeSet(author: "jessen", id: "20160613-sys-preferences-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_PREFERENCES_S', startValue: "10001")
        }
        createTable(tableName: "SYS_PREFERENCES") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "PREFERENCES_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PREFERENCES_PK")
                }
            } else {
                column(name: "PREFERENCES_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_PREFERENCES_PK")
                }
            }

            column(name: "PREFERENCES", type: "varchar(30)", remarks: "首选项名")
            column(name: "PREFERENCES_VALUE", type: "varchar(80)", remarks: "首选项值")
            column(name: "USER_ID", type: "bigint", remarks: "账号ID")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(tableName: "SYS_PREFERENCES", columnNames: "PREFERENCES,USER_ID", constraintName: "SYS_PREFERENCES_U1")
        } else {
            addUniqueConstraint(tableName: "SYS_PREFERENCES", columnNames: "PREFERENCES_ID,PREFERENCES,USER_ID", constraintName: "SYS_PREFERENCES_U1")
        }

    }

    changeSet(author: "jessen", id: "20160613-sys-account-retrieve-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_ACCOUNT_RETRIEVE_S', startValue: "10001")
        }
        createTable(tableName: "SYS_ACCOUNT_RETRIEVE") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "USER_ID", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ACCOUNT_RETRIEVE_PK")
                }
            } else {
                column(name: "USER_ID", type: "bigint", remarks: "表ID，主键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_ACCOUNT_RETRIEVE_PK")
                }
            }

            column(name: "RETRIEVE_TYPE", type: "varchar(30)", remarks: "类型:NAME/PWD")
            column(name: "RETRIEVE_DATE", type: "datetime", remarks: "时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "bigint", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "bigint", defaultValue: "-1")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "xuhailin", id: "20160922-sys-config-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_CONFIG_S', startValue: "10001")
        }
        createTable(tableName: "SYS_CONFIG") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "CONFIG_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CONFIG_PK")
                }
            } else {
                column(name: "CONFIG_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_CONFIG_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "CONFIG_CODE", type: "VARCHAR(240)", remarks: "配置编码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_CONFIG_U1")
                }
            } else {
                column(name: "CONFIG_CODE", type: "VARCHAR(240)", remarks: "配置编码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "CONFIG_VALUE", type: "VARCHAR(240)", remarks: "配置值")
            column(name: "CATEGORY", type: "VARCHAR(240)", remarks: "配置分类")
            column(name: "ENABLED_FLAG", type: "VARCHAR(1)", remarks: "启用标记", defaultValue: "Y")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "CONFIG_ID,CONFIG_CODE", tableName: "SYS_CONFIG", constraintName: "SYS_CONFIG_U1")
        }
    }

    changeSet(author: "jialongzuo", id: "2016-10-9-FND_COMPANY") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'FND_COMPANY_B_S', startValue: "10001")
        }
        createTable(tableName: "FND_COMPANY_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "company_id", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_COMPANY_B_PK")
                }
            } else {
                column(name: "company_id", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "FND_COMPANY_B_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "company_code", type: "VARCHAR(30)", remarks: "公司编码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "FND_COMPANY_B_U1")
                }
            } else {
                column(name: "company_code", type: "VARCHAR(30)", remarks: "公司编码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "company_type", type: "VARCHAR(30)", remarks: "公司类型")
            column(name: "address", type: "VARCHAR(250)", remarks: "地址")
            column(name: "company_level_id", type: "BIGINT")
            column(name: "parent_company_id", type: "BIGINT")
            column(name: "chief_position_id", type: "BIGINT")
            column(name: "start_date_active", type: "DATETIME")
            column(name: "end_date_active", type: "DATETIME")
            column(name: "company_short_name", type: "VARCHAR(250)", remarks: "公司简称")
            column(name: "company_full_name", type: "VARCHAR(250)", remarks: "公司全称")
            column(name: "zipcode", type: "VARCHAR(100)")
            column(name: "fax", type: "VARCHAR(100)")
            column(name: "phone", type: "VARCHAR(100)")
            column(name: "contact_person", type: "VARCHAR(100)")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "company_id,company_code", tableName: "FND_COMPANY_B", constraintName: "FND_COMPANY_B_U1")
        }

        createTable(tableName: "FND_COMPANY_TL") {
            column(name: "company_id", type: "bigint", remarks: "公司ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "company_short_name", type: "varchar(250)")
            column(name: "company_full_name", type: "varchar(250)")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

    }

    changeSet(author: "jialongzuo", id: "20161011-sys-userLogin-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_USER_LOGIN_S', startValue: "10001")
        }
        createTable(tableName: "SYS_USER_LOGIN") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "LOGIN_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_LOGIN_PK")
                }
            } else {
                column(name: "LOGIN_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_LOGIN_PK")
                }
            }

            column(name: "USER_ID", type: "BIGINT", remarks: "用户id") {
                constraints(nullable: "false")
            }
            column(name: "LOGIN_TIME", type: "DATETIME", remarks: "登录时间")
            column(name: "IP", type: "VARCHAR(40)", remarks: "ip地址")
            column(name: "REFERER", type: "VARCHAR(240)")
            column(name: "USER_AGENT", type: "VARCHAR(240)")
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
