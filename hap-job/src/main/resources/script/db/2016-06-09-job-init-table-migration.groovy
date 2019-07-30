package script.db

databaseChangeLog(logicalFilePath: "2018-11-15-init-table-migration.groovy") {
    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q1") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_JOB_DETAILS")
            }
        }
        if (helper.isPostgresql()) {
            createTable(tableName: "QRTZ_JOB_DETAILS") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "JOB_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "JOB_GROUP", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "DESCRIPTION", type: "VARCHAR(250)")
                column(name: "JOB_CLASS_NAME", type: "VARCHAR(250)") {
                    constraints(nullable: "false")
                }
                column(name: "IS_DURABLE", type: "BOOLEAN") {
                    constraints(nullable: "false")
                }
                column(name: "IS_NONCONCURRENT", type: "BOOLEAN") {
                    constraints(nullable: "false")
                }
                column(name: "IS_UPDATE_DATA", type: "BOOLEAN") {
                    constraints(nullable: "false")
                }
                column(name: "REQUESTS_RECOVERY", type: "BOOLEAN") {
                    constraints(nullable: "false")
                }
                column(name: "JOB_DATA", type: "BLOB")

            }
            createIndex(tableName: "QRTZ_JOB_DETAILS", indexName: "IDX_QRTZ_J_REQ_RECOVERY") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)")
                column(name: "REQUESTS_RECOVERY", type: "BOOLEAN")
            }
        } else {
            createTable(tableName: "QRTZ_JOB_DETAILS") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "JOB_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "JOB_GROUP", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "DESCRIPTION", type: "VARCHAR(250)")
                column(name: "JOB_CLASS_NAME", type: "VARCHAR(250)") {
                    constraints(nullable: "false")
                }
                column(name: "IS_DURABLE", type: "VARCHAR(1)") {
                    constraints(nullable: "false")
                }
                column(name: "IS_NONCONCURRENT", type: "VARCHAR(1)") {
                    constraints(nullable: "false")
                }
                column(name: "IS_UPDATE_DATA", type: "VARCHAR(1)") {
                    constraints(nullable: "false")
                }
                column(name: "REQUESTS_RECOVERY", type: "VARCHAR(1)") {
                    constraints(nullable: "false")
                }
                column(name: "JOB_DATA", type: "BLOB")

            }
            createIndex(tableName: "QRTZ_JOB_DETAILS", indexName: "IDX_QRTZ_J_REQ_RECOVERY") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)")
                column(name: "REQUESTS_RECOVERY", type: "VARCHAR(1)")
            }
        }
        createIndex(tableName: "QRTZ_JOB_DETAILS", indexName: "IDX_QRTZ_J_GRP") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "JOB_GROUP", type: "VARCHAR(200)")
        }
    }


    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q2") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_TRIGGERS")
            }
        }
        createTable(tableName: "QRTZ_TRIGGERS") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "JOB_NAME", type: "VARCHAR(200)") {
                constraints(nullable: "true")
            }
            column(name: "JOB_GROUP", type: "VARCHAR(200)") {
                constraints(nullable: "false")
            }
            column(name: "DESCRIPTION", type: "VARCHAR(250)")
            column(name: "NEXT_FIRE_TIME", type: "BIGINT")
            column(name: "PREV_FIRE_TIME", type: "BIGINT")
            column(name: "PRIORITY", type: "INT")
            column(name: "TRIGGER_STATE", type: "VARCHAR(16)") {
                constraints(nullable: "false")
            }
            column(name: "TRIGGER_TYPE", type: "VARCHAR(8)") {
                constraints(nullable: "false")
            }
            column(name: "START_TIME", type: "BIGINT") {
                constraints(nullable: "false")
            }
            column(name: "END_TIME", type: "BIGINT")
            column(name: "CALENDAR_NAME", type: "VARCHAR(200)")
            column(name: "MISFIRE_INSTR", type: "TINYINT(2)")
            column(name: "JOB_DATA", type: "BLOB")
        }
        addForeignKeyConstraint(baseTableName: "QRTZ_TRIGGERS", baseColumnNames: "SCHED_NAME,JOB_NAME,JOB_GROUP", constraintName: "QRTZ_TRIGGERS_IBFK_1", referencedTableName: "QRTZ_JOB_DETAILS", referencedColumnNames: "SCHED_NAME,JOB_NAME,JOB_GROUP")
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_J") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "JOB_NAME", type: "VARCHAR(200)")
            column(name: "JOB_GROUP", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_JG") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "JOB_GROUP", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_C") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "CALENDAR_NAME", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_G") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_STATE") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "TRIGGER_STATE", type: "VARCHAR(16)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_N_STATE") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "TRIGGER_NAME", type: "VARCHAR(200)")
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)")
            column(name: "TRIGGER_STATE", type: "VARCHAR(16)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_N_G_STATE") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)")
            column(name: "TRIGGER_STATE", type: "VARCHAR(16)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_NEXT_FIRE_TIME") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "NEXT_FIRE_TIME", type: "BIGINT")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_NFT_ST") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "TRIGGER_STATE", type: "VARCHAR(16)")
            column(name: "NEXT_FIRE_TIME", type: "BIGINT")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_NFT_MISFIRE") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "MISFIRE_INSTR", type: "TINYINT")
            column(name: "NEXT_FIRE_TIME", type: "BIGINT")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_NFT_ST_MISFIRE") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "MISFIRE_INSTR", type: "TINYINT")
            column(name: "NEXT_FIRE_TIME", type: "BIGINT")
            column(name: "TRIGGER_STATE", type: "VARCHAR(16)")
        }
        createIndex(tableName: "QRTZ_TRIGGERS", indexName: "IDX_QRTZ_T_NFT_ST_MISFIRE_GRP") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "MISFIRE_INSTR", type: "TINYINT")
            column(name: "NEXT_FIRE_TIME", type: "BIGINT")
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)")
            column(name: "TRIGGER_STATE", type: "VARCHAR(16)")
        }
    }

    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q3") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_SIMPLE_TRIGGERS")
            }
        }
        createTable(tableName: "QRTZ_SIMPLE_TRIGGERS") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "REPEAT_COUNT", type: "BIGINT") {
                constraints(nullable: "false")
            }
            column(name: "REPEAT_INTERVAL", type: "BIGINT") {
                constraints(nullable: "false")
            }
            column(name: "TIMES_TRIGGERED", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
        addForeignKeyConstraint(baseTableName: "QRTZ_SIMPLE_TRIGGERS", baseColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP", constraintName: "QRTZ_SIMPLE_TRIGGERS_IBFK_1", referencedTableName: "QRTZ_TRIGGERS", referencedColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP")
    }

    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q4") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_CRON_TRIGGERS")
            }
        }
        createTable(tableName: "QRTZ_CRON_TRIGGERS") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "CRON_EXPRESSION", type: "VARCHAR(120)") {
                constraints(nullable: "false")
            }
            column(name: "TIME_ZONE_ID", type: "VARCHAR(80)")
        }
        addForeignKeyConstraint(baseTableName: "QRTZ_CRON_TRIGGERS", baseColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP", constraintName: "QRTZ_CRON_TRIGGERS_IBFK_1", referencedTableName: "QRTZ_TRIGGERS", referencedColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP")
    }


    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q5") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_SIMPROP_TRIGGERS")
            }
        }
        if (helper.isPostgresql()) {
            createTable(tableName: "QRTZ_SIMPROP_TRIGGERS") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "STR_PROP_1", type: "VARCHAR(512)")
                column(name: "STR_PROP_2", type: "VARCHAR(512)")
                column(name: "STR_PROP_3", type: "VARCHAR(512)")
                column(name: "INT_PROP_1", type: "INT")
                column(name: "INT_PROP_2", type: "INT")
                column(name: "LONG_PROP_1", type: "BIGINT")
                column(name: "LONG_PROP_2", type: "BIGINT")
                column(name: "DEC_PROP_1", type: "NUMBER")
                column(name: "DEC_PROP_2", type: "NUMBER")
                column(name: "BOOL_PROP_1", type: "BOOLEAN")
                column(name: "BOOL_PROP_2", type: "BOOLEAN")
            }
        } else {
            createTable(tableName: "QRTZ_SIMPROP_TRIGGERS") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "STR_PROP_1", type: "VARCHAR(512)")
                column(name: "STR_PROP_2", type: "VARCHAR(512)")
                column(name: "STR_PROP_3", type: "VARCHAR(512)")
                column(name: "INT_PROP_1", type: "INT")
                column(name: "INT_PROP_2", type: "INT")
                column(name: "LONG_PROP_1", type: "BIGINT")
                column(name: "LONG_PROP_2", type: "BIGINT")
                column(name: "DEC_PROP_1", type: "NUMBER")
                column(name: "DEC_PROP_2", type: "NUMBER")
                column(name: "BOOL_PROP_1", type: "VARCHAR(1)")
                column(name: "BOOL_PROP_2", type: "VARCHAR(1)")
            }
        }

        addForeignKeyConstraint(baseTableName: "QRTZ_SIMPROP_TRIGGERS", baseColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP", constraintName: "QRTZ_SIMPROP_TRIGGERS_IBFK_1", referencedTableName: "QRTZ_TRIGGERS", referencedColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP")
    }

    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q6") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_BLOB_TRIGGERS")
            }
        }
        createTable(tableName: "QRTZ_BLOB_TRIGGERS") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "BLOB_DATA", type: "BLOB")
        }
        addForeignKeyConstraint(baseTableName: "QRTZ_BLOB_TRIGGERS", baseColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP", constraintName: "QRTZ_BLOB_TRIGGERS_IBFK_1", referencedTableName: "QRTZ_TRIGGERS", referencedColumnNames: "SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP")
    }

    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q7") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_CALENDARS")
            }
        }
        createTable(tableName: "QRTZ_CALENDARS") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "CALENDAR_NAME", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "CALENDAR", type: "BLOB") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q8") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_PAUSED_TRIGGER_GRPS")
            }
        }
        createTable(tableName: "QRTZ_PAUSED_TRIGGER_GRPS") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
        }
    }

    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q9") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_FIRED_TRIGGERS")
            }
        }

        if (helper.isPostgresql()) {
            createTable(tableName: "QRTZ_FIRED_TRIGGERS") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "ENTRY_ID", type: "VARCHAR(95)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false")
                }
                column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                    constraints(nullable: "false")
                }
                column(name: "INSTANCE_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false")
                }
                column(name: "FIRED_TIME", type: "BIGINT") {
                    constraints(nullable: "false")
                }
                column(name: "SCHED_TIME", type: "BIGINT") {
                    constraints(nullable: "false")
                }
                column(name: "PRIORITY", type: "INT") {
                    constraints(nullable: "false")
                }
                column(name: "STATE", type: "VARCHAR(16)") {
                    constraints(nullable: "false")
                }
                column(name: "JOB_NAME", type: "VARCHAR(200)")
                column(name: "JOB_GROUP", type: "VARCHAR(200)")
                column(name: "IS_NONCONCURRENT", type: "BOOLEAN")
                column(name: "REQUESTS_RECOVERY", type: "BOOLEAN")
            }
            createIndex(tableName: "QRTZ_FIRED_TRIGGERS", indexName: "IDX_QRTZ_FT_TRIG_INST_NAME") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)")
                column(name: "INSTANCE_NAME", type: "VARCHAR(200)")
                column(name: "REQUESTS_RECOVERY", type: "BOOLEAN")
            }
        } else {
            createTable(tableName: "QRTZ_FIRED_TRIGGERS") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "ENTRY_ID", type: "VARCHAR(95)") {
                    constraints(nullable: "false", primaryKey: "true")
                }
                column(name: "TRIGGER_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false")
                }
                column(name: "TRIGGER_GROUP", type: "VARCHAR(200)") {
                    constraints(nullable: "false")
                }
                column(name: "INSTANCE_NAME", type: "VARCHAR(200)") {
                    constraints(nullable: "false")
                }
                column(name: "FIRED_TIME", type: "BIGINT") {
                    constraints(nullable: "false")
                }
                column(name: "SCHED_TIME", type: "BIGINT") {
                    constraints(nullable: "false")
                }
                column(name: "PRIORITY", type: "INT") {
                    constraints(nullable: "false")
                }
                column(name: "STATE", type: "VARCHAR(16)") {
                    constraints(nullable: "false")
                }
                column(name: "JOB_NAME", type: "VARCHAR(200)")
                column(name: "JOB_GROUP", type: "VARCHAR(200)")
                column(name: "IS_NONCONCURRENT", type: "VARCHAR(1)")
                column(name: "REQUESTS_RECOVERY", type: "VARCHAR(1)")
            }
            createIndex(tableName: "QRTZ_FIRED_TRIGGERS", indexName: "IDX_QRTZ_FT_TRIG_INST_NAME") {
                column(name: "SCHED_NAME", type: "VARCHAR(120)")
                column(name: "INSTANCE_NAME", type: "VARCHAR(200)")
                column(name: "REQUESTS_RECOVERY", type: "VARCHAR(1)")
            }
        }
        createIndex(tableName: "QRTZ_FIRED_TRIGGERS", indexName: "IDX_QRTZ_FT_INST_JOB_REQ_RCVRY") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "INSTANCE_NAME", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_FIRED_TRIGGERS", indexName: "IDX_QRTZ_FT_J_G") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "JOB_NAME", type: "VARCHAR(200)")
            column(name: "JOB_GROUP", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_FIRED_TRIGGERS", indexName: "IDX_QRTZ_FT_JG") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "JOB_GROUP", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_FIRED_TRIGGERS", indexName: "IDX_QRTZ_FT_T_G") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "TRIGGER_NAME", type: "VARCHAR(200)")
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)")
        }
        createIndex(tableName: "QRTZ_FIRED_TRIGGERS", indexName: "IDX_QRTZ_FT_TG") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)")
            column(name: "TRIGGER_GROUP", type: "VARCHAR(200)")
        }
    }


    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q10") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_SCHEDULER_STATE")
            }
        }
        createTable(tableName: "QRTZ_SCHEDULER_STATE") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "INSTANCE_NAME", type: "VARCHAR(200)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LAST_CHECKIN_TIME", type: "BIGINT") {
                constraints(nullable: "false")
            }
            column(name: "CHECKIN_INTERVAL", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "jiameng.cao", id: "20181115-jiameng.cao-q11") {
        preConditions(onFail: "MARK_RAN") {
            not {
                tableExists(tableName: "QRTZ_LOCKS")
            }
        }
        createTable(tableName: "QRTZ_LOCKS") {
            column(name: "SCHED_NAME", type: "VARCHAR(120)") {
                constraints(nullable: "false", primaryKey: "true")
            }
            column(name: "LOCK_NAME", type: "VARCHAR(40)") {
                constraints(nullable: "false", primaryKey: "true")
            }
        }
    }
}
