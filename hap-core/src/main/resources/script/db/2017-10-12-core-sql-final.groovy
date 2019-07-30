package script.db

databaseChangeLog(logicalFilePath: "2016-06-09-init-data-migration.groovy") {

    changeSet(author: "qiangzeng", id: "20170401-data-sys-user-1") {
        sqlFile(path: helper.dataPath("script/db/data/" + helper.dbType().toString() + "/init/sys_user_update.sql"), encoding: "UTF-8")
    }

    changeSet(author: "qiangzeng", id: "20170904-data-sys-resource-item-1") {
        sqlFile(path: helper.dataPath("script/db/data/" + helper.dbType().toString() + "/init/sys_resource_item_update.sql"), encoding: "UTF-8")
    }

}