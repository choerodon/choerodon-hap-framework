package script.db

databaseChangeLog(logicalFilePath: "2016-09-26-init-migration.groovy") {


    changeSet(author: "niujiaqing", id: "20160926-niujiaqing-hr-employee") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HR_EMPLOYEE_S', startValue: "10001")
        }
        createTable(tableName: "HR_EMPLOYEE") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "EMPLOYEE_ID", type: "BIGINT", remarks: "员工ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "HR_EMPLOYEE_PK")
                }
            } else {
                column(name: "EMPLOYEE_ID", type: "BIGINT", remarks: "员工ID") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "HR_EMPLOYEE_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "EMPLOYEE_CODE", remarks: "员工编码", type: "VARCHAR(30)") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "HR_EMPLOYEE_U1")
                }
            } else {
                column(name: "EMPLOYEE_CODE", remarks: "员工编码", type: "VARCHAR(30)") {
                    constraints(nullable: "false")
                }
            }

            column(name: "NAME", remarks: "员工姓名", type: "VARCHAR(50)") { constraints(nullable: "false") }
            column(name: "BORN_DATE", remarks: "出生日期", type: "DATE")
            column(name: "EMAIL", remarks: "电子邮件", type: "VARCHAR(50)")
            column(name: "MOBIL", remarks: "移动电话", type: "VARCHAR(50)")
            column(name: "JOIN_DATE", remarks: "入职日期", type: "DATE")
            column(name: "GENDER", remarks: "性别", type: "VARCHAR(1)")
            if (!helper.isPostgresql()) {
                column(name: "CERTIFICATE_ID", remarks: "ID", type: "VARCHAR(100)") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "HR_EMPLOYEE_U2")
                }
            } else {
                column(name: "CERTIFICATE_ID", remarks: "ID", type: "VARCHAR(100)") { constraints(nullable: "false") }
            }

            column(name: "STATUS", remarks: "状态", type: "VARCHAR(50)") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", remarks: "启用状态", type: "VARCHAR(1)", defaultValue: "Y") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "EMPLOYEE_ID,EMPLOYEE_CODE", tableName: "HR_EMPLOYEE", constraintName: "HR_EMPLOYEE_U1")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "EMPLOYEE_ID,CERTIFICATE_ID", tableName: "HR_EMPLOYEE", constraintName: "HR_EMPLOYEE_U2")
        }
    }

    changeSet(author: "niujiaqing", id: "20160926-niujiaqing-hr-org-unit") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HR_ORG_UNIT_B_S', startValue: "10001")
        }
        createTable(tableName: "HR_ORG_UNIT_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "UNIT_ID", type: "BIGINT", remarks: "组织ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            } else {
                column(name: "UNIT_ID", type: "BIGINT", remarks: "组织ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            }

            column(name: "PARENT_ID", remarks: "父组织", type: "BIGINT")
            column(name: "UNIT_CODE", remarks: "组织编码", type: "VARCHAR(50)")
            column(name: "NAME", remarks: "组织名称", type: "VARCHAR(100)")
            column(name: "DESCRIPTION", remarks: "组织描述", type: "VARCHAR(255)")
            column(name: "MANAGER_POSITION", remarks: "组织管理岗位", type: "BIGINT")
            column(name: "COMPANY_ID", remarks: "公司ID", type: "BIGINT")
            column(name: "ENABLED_FLAG", remarks: "启用状态", type: "VARCHAR(1)", defaultValue: "Y") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createTable(tableName: "HR_ORG_UNIT_TL") {
            column(name: "UNIT_ID", type: "BIGINT", remarks: "组织ID") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "LANG", remarks: "语言", type: "VARCHAR(50)") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "NAME", remarks: "组织名称", type: "VARCHAR(100)")
            column(name: "DESCRIPTION", remarks: "组织描述", type: "VARCHAR(255)")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "niujiaqing", id: "20160926-niujiaqing-hr-org-position") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HR_ORG_POSITION_B_S', startValue: "10001")
        }
        createTable(tableName: "HR_ORG_POSITION_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "POSITION_ID", type: "BIGINT", remarks: "岗位ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            } else {
                column(name: "POSITION_ID", type: "BIGINT", remarks: "岗位ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            }

            column(name: "UNIT_ID", remarks: "组织ID", type: "BIGINT") { constraints(nullable: "false") }
            column(name: "POSITION_CODE", remarks: "岗位编码", type: "VARCHAR(50)")
            column(name: "NAME", remarks: "岗位名称", type: "VARCHAR(100)")
            column(name: "DESCRIPTION", remarks: "岗位描述", type: "VARCHAR(255)")
            column(name: "PARENT_POSITION_ID", remarks: "父岗位ID", type: "BIGINT")
            column(name: "ENABLED_FLAG", remarks: "启用状态", type: "VARCHAR(1)", defaultValue: "Y") { constraints(nullable: "false") }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createTable(tableName: "HR_ORG_POSITION_TL") {
            column(name: "POSITION_ID", type: "BIGINT", remarks: "岗位ID") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "LANG", remarks: "语言", type: "VARCHAR(50)") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "NAME", remarks: "组织名称", type: "VARCHAR(100)")
            column(name: "DESCRIPTION", remarks: "组织描述", type: "VARCHAR(255)")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
    }


    changeSet(author: "niujiaqing", id: "20160926-niujiaqing-hr-employee-assign") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HR_EMPLOYEE_ASSIGN_S', startValue: "10001")
        }
        createTable(tableName: "HR_EMPLOYEE_ASSIGN") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "ASSIGN_ID", type: "BIGINT", remarks: "ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            } else {
                column(name: "ASSIGN_ID", type: "BIGINT", remarks: "ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            }

            column(name: "EMPLOYEE_ID", remarks: "员工ID", type: "BIGINT") { constraints(nullable: "false") }
            column(name: "POSITION_ID", remarks: "岗位ID", type: "BIGINT") { constraints(nullable: "false") }
            column(name: "PRIMARY_POSITION_FLAG", remarks: "主岗位标示", type: "VARCHAR(1)")
            column(name: "ENABLED_FLAG", remarks: "启用状态", type: "VARCHAR(1)", defaultValue: "Y") { constraints(nullable: "false") }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (!helper.isPostgresql()) {
            addUniqueConstraint(tableName: "HR_EMPLOYEE_ASSIGN", columnNames: "EMPLOYEE_ID, POSITION_ID", constraintName: "HR_EMPLOYEE_ASSIGN_U1")
        } else {
            addUniqueConstraint(tableName: "HR_EMPLOYEE_ASSIGN", columnNames: "ASSIGN_ID,EMPLOYEE_ID, POSITION_ID", constraintName: "HR_EMPLOYEE_ASSIGN_U1")
        }

    }


    changeSet(author: "niujiaqing", id: "20161011-niujiaqing-sys-dashboard") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_DASHBOARD_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_DASHBOARD_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "DASHBOARD_ID", type: "BIGINT", remarks: "ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            } else {
                column(name: "DASHBOARD_ID", type: "BIGINT", remarks: "ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            }

            if (!helper.isPostgresql()) {
                column(name: "DASHBOARD_CODE", remarks: "仪表盘编码", type: "VARCHAR(100)") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_DASHBOARD_B_U1")
                }
            } else {
                column(name: "DASHBOARD_CODE", remarks: "仪表盘编码", type: "VARCHAR(100)") {
                    constraints(nullable: "false")
                }
            }

            column(name: "TITLE", remarks: "仪表盘标题", type: "VARCHAR(100)")
            column(name: "DESCRIPTION", remarks: "仪表盘描述", type: "VARCHAR(255)")
            column(name: "RESOURCE_ID", remarks: "资源ID", type: "BIGINT") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", remarks: "启用状态", type: "VARCHAR(1)", defaultValue: "Y") { constraints(nullable: "false") }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "DASHBOARD_ID,DASHBOARD_CODE", tableName: "SYS_DASHBOARD_B", constraintName: "SYS_DASHBOARD_B_U1")
        }

        createTable(tableName: "SYS_DASHBOARD_TL") {
            column(name: "DASHBOARD_ID", type: "BIGINT", remarks: "ID") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "LANG", remarks: "语言", type: "VARCHAR(50)") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "TITLE", remarks: "仪表盘标题", type: "VARCHAR(100)")
            column(name: "DESCRIPTION", remarks: "仪表盘描述", type: "VARCHAR(255)")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
    }

    changeSet(author: "niujiaqing", id: "20161012-niujiaqing-user-dashboard") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_USER_DASHBOARD_S', startValue: "10001")
        }
        createTable(tableName: "SYS_USER_DASHBOARD") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "USER_DASHBOARD_ID", type: "BIGINT", remarks: "ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            } else {
                column(name: "USER_DASHBOARD_ID", type: "BIGINT", remarks: "ID") {
                    constraints(nullable: "false", primaryKey: "true")
                }
            }

            column(name: "USER_ID", remarks: "用户ID", type: "BIGINT") { constraints(nullable: "false") }
            column(name: "DASHBOARD_ID", remarks: "仪表盘ID", type: "BIGINT") { constraints(nullable: "false") }
            column(name: "DASHBOARD_SEQUENCE", type: "decimal(20,0)", defaultValue: "1", remarks: "仪表盘排序号")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }



    changeSet(author: "xuhailin", id: "20161117-sys-user-demo-1") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_USER_DEMO_B_S', startValue: "10001")
        }
        createTable(tableName: "SYS_USER_DEMO_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "USER_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_DEMO_B_PK")
                }
            } else {
                column(name: "USER_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_USER_DEMO_B_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "USER_CODE", type: "VARCHAR(255)", remarks: "编码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_USER_DEMO_B_U1")
                }
            } else {
                column(name: "USER_CODE", type: "VARCHAR(255)", remarks: "编码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "USER_NAME", type: "VARCHAR(255)", remarks: "姓名") {
                constraints(nullable: "false")
            }
            column(name: "USER_AGE", type: "BIGINT", remarks: "年龄")
            column(name: "USER_SEX", type: "VARCHAR(50)", remarks: "性别")
            column(name: "USER_BIRTH", type: "DATE", remarks: "生日")
            column(name: "USER_EMAIL", type: "VARCHAR(255)", remarks: "邮箱")
            column(name: "USER_PHONE", type: "BIGINT", remarks: "电话")
            column(name: "ENABLE_FLAG", type: "VARCHAR(1)", remarks: "是否启用")
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "描述")
            column(name: "ROLE_ID", type: "BIGINT", remarks: "角色ID")
            column(name: "ROLE_NAME", type: "VARCHAR(255)", remarks: "角色名称")
            column(name: "START_ACTIVE_TIME", type: "DATETIME", remarks: "开始时间")
            column(name: "END_ACTIVE_TIME", type: "DATETIME", remarks: "结束时间")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "USER_ID,USER_CODE", tableName: "SYS_USER_DEMO_B", constraintName: "SYS_USER_DEMO_B_U1")
        }

        createTable(tableName: "SYS_USER_DEMO_TL") {
            column(name: "USER_ID", type: "BIGINT", remarks: "pk") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", remarks: "语言", type: "VARCHAR(50)") { constraints(nullable: "false", primaryKey: "true") }
            column(name: "USER_NAME", remarks: "姓名", type: "VARCHAR(255)")
            column(name: "DESCRIPTION", remarks: "描述", type: "VARCHAR(255)")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")


        }
    }


    changeSet(author: "yangzhizheng", id: "20161212-sys-resource-customization-1") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_RESOURCE_CUSTOMIZATION_S', startValue: "10001")
        }

        createTable(tableName: "SYS_RESOURCE_CUSTOMIZATION") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "RESOURCE_CUSTOMIZATION_ID", type: "bigint", autoIncrement: "true", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_CUSTOMIZATION_PK")
                }
            } else {
                column(name: "RESOURCE_CUSTOMIZATION_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_RESOURCE_CUSTOMIZATION_PK")
                }
            }

            column(name: "RESOURCE_ID", type: "bigint", remarks: "外键，资源ID") {
                constraints(nullable: "false")
            }
            column(name: "URL", type: "varchar(255)", remarks: "URL") {
                constraints(nullable: "false")
            }
            column(name: "SEQUENCE", type: "int", remarks: "序列号") {
                constraints(nullable: "false")
            }
            column(name: "ENABLE_FLAG", type: "varchar(1)", defaultValue: "N", remarks: "是否激活") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "varchar(240)", remarks: "描述")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        createIndex(tableName: "SYS_RESOURCE_CUSTOMIZATION", indexName: "SYS_RESOURCE_CUSTOMIZATION_N1") {
            column(name: "RESOURCE_ID", type: "bigint")
        }
    }




    changeSet(author: "xiangyuQi", id: "20161121-sys-if-invoke-in-1") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_IF_INVOKE_INBOUND_S', startValue: "10001")
        }

        createTable(tableName: "SYS_IF_INVOKE_INBOUND") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "INBOUND_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_IF_INVOKE_INBOUND_PK")
                }
            } else {
                column(name: "INBOUND_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_IF_INVOKE_INBOUND_PK")
                }
            }

            column(name: "INTERFACE_NAME", type: "VARCHAR(255)", remarks: "接口名称") {
                constraints(nullable: "false")
            }
            column(name: "INTERFACE_URL", type: "VARCHAR(200)", remarks: "接口地址") {
                constraints(nullable: "false")
            }
            column(name: "REQUEST_TIME", type: "DATETIME", remarks: "请求时间") {
                constraints(nullable: "false")
            }
            column(name: "REQUEST_HEADER_PARAMETER", type: "VARCHAR(2000)", remarks: "请求header参数")
            column(name: "REQUEST_BODY_PARAMETER", type: "CLOB", remarks: "请求body参数")
            column(name: "REQUEST_METHOD", type: "VARCHAR(10)", remarks: "请求方式")
            column(name: "REQUEST_STATUS", type: "VARCHAR(10)", remarks: "请求状态")
            column(name: "RESPONSE_CONTENT", type: "CLOB", remarks: "响应内容")
            column(name: "RESPONSE_TIME", type: "BIGINT", remarks: "响应时间")
            column(name: "STACKTRACE", type: "CLOB", remarks: "错误堆栈")
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
        createIndex(tableName: "SYS_IF_INVOKE_INBOUND", indexName: "SYS_IF_INVOKE_INBOUND_N1") {
            column(name: "INTERFACE_NAME", type: "VARCHAR(255)")
        }
        createIndex(tableName: "SYS_IF_INVOKE_INBOUND", indexName: "SYS_IF_INVOKE_INBOUND_N2") { column(name: "INTERFACE_URL", type: "VARCHAR(200)") }
        createIndex(tableName: "SYS_IF_INVOKE_INBOUND", indexName: "SYS_IF_INVOKE_INBOUND_N3") { column(name: "REQUEST_TIME", type: "DATETIME") }
        createIndex(tableName: "SYS_IF_INVOKE_INBOUND", indexName: "SYS_IF_INVOKE_INBOUND_N4") { column(name: "REQUEST_STATUS", type: "VARCHAR(10)") }

    }

    changeSet(author: "xiangyuQi", id: "20161121-sys-if-invoke-out-1") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_IF_INVOKE_OUTBOUND_S', startValue: "10001")
        }

        createTable(tableName: "SYS_IF_INVOKE_OUTBOUND") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(autoIncrement: "true", startWith: "10001", name: "OUTBOUND_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_IF_INVOKE_OUTBOUND_PK")
                }
            } else {
                column(name: "OUTBOUND_ID", type: "BIGINT", remarks: "pk") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_IF_INVOKE_OUTBOUND_PK")
                }
            }

            column(name: "INTERFACE_NAME", type: "VARCHAR(255)", remarks: "接口名称") {
                constraints(nullable: "false")
            }
            column(name: "INTERFACE_URL", type: "VARCHAR(200)", remarks: "接口地址") {
                constraints(nullable: "false")
            }
            column(name: "REQUEST_TIME", type: "DATETIME", remarks: "请求时间") {
                constraints(nullable: "false")
            }
            column(name: "REQUEST_PARAMETER", type: "CLOB", remarks: "请求参数")
            column(name: "REQUEST_STATUS", type: "VARCHAR(10)", remarks: "请求状态")
            column(name: "RESPONSE_CONTENT", type: "CLOB", remarks: "响应内容")
            column(name: "RESPONSE_TIME", type: "BIGINT", remarks: "响应时间")
            column(name: "RESPONSE_CODE", type: "VARCHAR(30)", remarks: "httpCode")
            column(name: "STACKTRACE", type: "CLOB", remarks: "错误堆栈")
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")


        }
        createIndex(tableName: "SYS_IF_INVOKE_OUTBOUND", indexName: "SYS_IF_INVOKE_OUTBOUND_N1") {
            column(name: "INTERFACE_NAME", type: "VARCHAR(255)")
        }
        createIndex(tableName: "SYS_IF_INVOKE_OUTBOUND", indexName: "SYS_IF_INVOKE_OUTBOUND_N2") {
            column(name: "INTERFACE_URL", type: "VARCHAR(200)")
        }
        createIndex(tableName: "SYS_IF_INVOKE_OUTBOUND", indexName: "SYS_IF_INVOKE_OUTBOUND_N3") { column(name: "REQUEST_TIME", type: "DATETIME") }
        createIndex(tableName: "SYS_IF_INVOKE_OUTBOUND", indexName: "SYS_IF_INVOKE_OUTBOUND_N4") {
            column(name: "REQUEST_STATUS", type: "VARCHAR(10)")
        }
    }

}