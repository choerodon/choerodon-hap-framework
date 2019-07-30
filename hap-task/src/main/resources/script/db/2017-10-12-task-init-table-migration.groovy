package script.db

databaseChangeLog(logicalFilePath: "2017-10-12-init-migration.groovy") {

    changeSet(author: "peng.jiang", id: "20171120-sys-task-detail") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_TASK_DETAIL_S', startValue: "10001")
        }
        createTable(tableName: "SYS_TASK_DETAIL", remarks: "并发任务-任务表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "TASK_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_DETAIL_PK")
                }
            } else {
                column(name: "TASK_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_DETAIL_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "CODE", type: "VARCHAR(200)", remarks: "代码") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_TASK_DETAIL_U1")
                }
            } else {
                column(name: "CODE", type: "VARCHAR(200)", remarks: "代码") {
                    constraints(nullable: "false")
                }
            }

            column(name: "NAME", type: "VARCHAR(200)", remarks: "名称") {
                constraints(nullable: "false")
            }
            column(name: "TYPE", type: "VARCHAR(10)", remarks: "类型") {
                constraints(nullable: "false")
            }
            column(name: "TASK_CLASS", type: "VARCHAR(255)", remarks: "类名")
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "描述")
            column(name: "IDS", type: "VARCHAR(1000)", remarks: "子任务id")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "TASK_ID,CODE", tableName: "SYS_TASK_DETAIL", constraintName: "SYS_TASK_DETAIL_U1")
        }
        createIndex(tableName: "SYS_TASK_DETAIL", indexName: "SYS_TASK_DETAIL_N2") { column(name: "NAME", type: "VARCHAR(200)") }
        createIndex(tableName: "SYS_TASK_DETAIL", indexName: "SYS_TASK_DETAIL_N3") { column(name: "TYPE", type: "VARCHAR(10)") }
    }

    changeSet(author: "peng.jiang", id: "20171120-sys-task-assign") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_TASK_ASSIGN_S', startValue: "10001")
        }
        createTable(tableName: "SYS_TASK_ASSIGN", remarks: "并发任务-任务授权表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "TASK_ASSIGN_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_ASSIGN_PK")
                }
            } else {
                column(name: "TASK_ASSIGN_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_ASSIGN_PK")
                }
            }

            column(name: "TASK_ID", type: "BIGINT", remarks: "任务ID") {
                constraints(nullable: "false")
            }
            column(name: "ASSIGN_ID", type: "BIGINT", remarks: "目标ID") {
                constraints(nullable: "false")
            }
            column(name: "ASSIGN_TYPE", type: "VARCHAR(10)", defaultValue: "ROLE", remarks: "类型") {
                constraints(nullable: "false")
            }
            column(name: "START_DATE", type: "DATE", remarks: "开始时间")
            column(name: "END_DATE", type: "DATE", remarks: "结束时间")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "SYS_TASK_ASSIGN", indexName: "SYS_TASK_ASSIGN_N1") { column(name: "TASK_ID", type: "BIGINT") }
        createIndex(tableName: "SYS_TASK_ASSIGN", indexName: "SYS_TASK_ASSIGN_N2") { column(name: "ASSIGN_ID", type: "BIGINT") }
        createIndex(tableName: "SYS_TASK_ASSIGN", indexName: "SYS_TASK_ASSIGN_N3") { column(name: "ASSIGN_TYPE", type: "VARCHAR(10)") }
    }


    changeSet(author: "peng.jiang", id: "20171120-sys-task-execution") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_TASK_EXECUTION_S', startValue: "10001")
        }
        createTable(tableName: "SYS_TASK_EXECUTION", remarks: "并发任务-任务执行情况表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "EXECUTION_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_EXECUTION_PK")
                }
            } else {
                column(name: "EXECUTION_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_EXECUTION_PK")
                }
            }

            column(name: "EXECUTION_NUMBER", type: "VARCHAR(200)", remarks: "执行编号")
            column(name: "EXECUTION_DESCRIPTION", type: "VARCHAR(255)", remarks: "描述")
            column(name: "TASK_ID", type: "BIGINT", remarks: "任务ID") {
                constraints(nullable: "false")
            }
            column(name: "STATUS", type: "VARCHAR(10)", remarks: "执行状态") {
                constraints(nullable: "false")
            }
            column(name: "USER_ID", type: "BIGINT", remarks: "提交人")
            column(name: "START_TIME", type: "DATETIME", remarks: "开始时间")
            column(name: "END_TIME", type: "DATETIME", remarks: "结束时间")
            column(name: "PARENT_ID", type: "BIGINT", remarks: "父id")
            column(name: "EXECUTION_ORDER", type: "INT", remarks: "执行顺序")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "SYS_TASK_EXECUTION", indexName: "SYS_TASK_EXECUTION_N1") { column(name: "EXECUTION_NUMBER", type: "VARCHAR(200)") }
        createIndex(tableName: "SYS_TASK_EXECUTION", indexName: "SYS_TASK_EXECUTION_N2") { column(name: "STATUS", type: "VARCHAR(10)") }

    }

    changeSet(author: "peng.jiang", id: "20171120-sys-task-execution-detail") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_TASK_EXECUTION_DETAIL_S', startValue: "10001")
        }
        createTable(tableName: "SYS_TASK_EXECUTION_DETAIL", remarks: "并发任务-任务执行情况详情表") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "EXECUTION_DETAIL_ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_EXECUTION_DETAIL_PK")
                }
            } else {
                column(name: "EXECUTION_DETAIL_ID", type: "BIGINT", remarks: "PK") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_TASK_EXECUTION_DETAIL_PK")
                }
            }

            column(name: "EXECUTION_ID", type: "BIGINT", remarks: "任务执行ID") {
                constraints(nullable: "false")
            }
            column(name: "PARAMETER", type: "CLOB", remarks: "参数")
            column(name: "STACKTRACE", type: "CLOB", remarks: "错误堆栈")
            column(name: "EXECUTION_LOG", type: "CLOB", remarks: "执行日志")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "SYS_TASK_EXECUTION_DETAIL", indexName: "SYS_TASK_EXECUTION_DETAIL_N1") {
            column(name: "EXECUTION_ID", type: "BIGINT")
        }
    }
}

databaseChangeLog(logicalFilePath: "patch.groovy") {

    changeSet(author: "jiangpeng", id: "20171205-sys_task_detail") {
        addColumn(tableName: "SYS_TASK_DETAIL") {
            column(name: "START_DATE", type: "DATE", remarks: "开始日期")
        }
        addColumn(tableName: "SYS_TASK_DETAIL") {
            column(name: "END_DATE", type: "DATE", remarks: "结束日期")
        }
    }

    changeSet(author: "jiangpeng", id: "20180104-sys-task-assign") {
        if (!helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "TASK_ID, ASSIGN_ID, ASSIGN_TYPE", tableName: "SYS_TASK_ASSIGN", constraintName: "TASK_ASSIGN_U1")
        } else {
            addUniqueConstraint(columnNames: "TASK_ASSIGN_ID,TASK_ID, ASSIGN_ID, ASSIGN_TYPE", tableName: "SYS_TASK_ASSIGN", constraintName: "TASK_ASSIGN_U1")
        }

    }

    changeSet(author: "jiangpeng", id: "20180110-sys-task-execution") {
        addColumn(tableName: "SYS_TASK_EXECUTION") {
            column(name: "LAST_EXECUTE_DATE", type: "DATETIME", remarks: "上次执行时间", afterColumn: "EXECUTION_ORDER")
        }
        addColumn(tableName: "SYS_TASK_EXECUTION") {
            column(name: "EXECUTE_RESULT_PATH", type: "varchar(250)", remarks: "执行结果路径", afterColumn: "LAST_EXECUTE_DATE")
        }
    }

}

databaseChangeLog(logicalFilePath: "patch.groovy") {

    changeSet(author: "vista yih", id: "201809-13-sys_task_detail_b") {
        renameTable(oldTableName: 'SYS_TASK_DETAIL', newTableName: 'SYS_TASK_DETAIL_B')
    }
    changeSet(author: "vista yih", id: "2018-09-13-sys_task_detail_tl") {
        createTable(tableName: "SYS_TASK_DETAIL_TL", remarks: "任务详情多语言表") {
            column(name: "TASK_ID", type: "bigint", remarks: "任务ID") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LANG", type: "varchar(10)", remarks: "语言") {
                constraints(nullable: "false", primaryKey: "true")
            }

            column(name: "NAME", type: "VARCHAR(200)", remarks: "名称")
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: "描述")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "qiang.zeng", id: "20190111-qiangzeng-1") {
        if (helper.dbType().isSupportSequence()) {
            dropSequence(sequenceName: 'SYS_TASK_DETAIL_S')
            createSequence(sequenceName: 'SYS_TASK_DETAIL_B_S', startValue: "10001")
        }
    }

}

def dbType = helper.dbType().toString()
databaseChangeLog(logicalFilePath: "2017-10-12-task-init-data-migration.groovy") {
    changeSet(author: "vista yih", id: "20180917-task-detail-b") {
        sqlFile(path: "script/db/data/" + dbType + "/init/sys_task_detail_tl.sql", encoding: "UTF-8")
    }
}