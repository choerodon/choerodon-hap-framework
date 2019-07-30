package script.db

databaseChangeLog(logicalFilePath: "2016-06-09-init-data-migration.groovy") {

    changeSet(author: "qiangzeng", id: "20180820-data-sys-code-value-1") {
        sqlFile(path: helper.dataPath("script/db/data/" + helper.dbType().toString() + "/init/sys_code_value_update.sql"), encoding: "UTF-8")
    }

}