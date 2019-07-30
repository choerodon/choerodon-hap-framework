package script.db

databaseChangeLog(logicalFilePath: "2016-09-26-init-migration.groovy") {

    changeSet(author: "xiangyu.qi", id: "20170418-sys-oauth-client") {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'SYS_OAUTH_CLIENT_DETAILS_S', startValue: "10001")
        }
        createTable(tableName: "SYS_OAUTH_CLIENT_DETAILS") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: "ID", type: "BIGINT", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_OAUTH_CLIENT_DETAILS_PK")
                }
            } else {
                column(name: "ID", type: "BIGINT", remarks: "表ID，主键，供其他表做外键") {
                    constraints(nullable: "false", primaryKey: "true", primaryKeyName: "SYS_OAUTH_CLIENT_DETAILS_PK")
                }
            }
            if (!helper.isPostgresql()) {
                column(name: "CLIENT_ID", type: "VARCHAR(100)", remarks: "客户端id") {
                    constraints(nullable: "false", unique: "true", uniqueConstraintName: "SYS_OAUTH_CLIENT_U1")
                }
            } else {
                column(name: "CLIENT_ID", type: "VARCHAR(100)", remarks: "客户端id") {
                    constraints(nullable: "false")
                }
            }

            column(name: "CLIENT_SECRET", type: "VARCHAR(256)", remarks: "客户端密码")
            column(name: "AUTHORIZED_GRANT_TYPES", type: "VARCHAR(256)", remarks: "授权模式") {
                constraints(nullable: "false")
            }
            column(name: "RESOURCE_IDS", type: "VARCHAR(256)", remarks: "资源ID", defaultValue: "api-resource")
            column(name: "AUTHORITIES", type: "VARCHAR(256)", remarks: "角色信息")
            column(name: "AUTO_APPROVE", type: "VARCHAR(256)", remarks: "自动授权")
            column(name: "SCOPE", type: "VARCHAR(256)", remarks: "scope")
            column(name: "ACCESS_TOKEN_VALIDITY", type: "BIGINT", remarks: "accessToken 失效时间")
            column(name: "REFRESH_TOKEN_VALIDITY", type: "BIGINT", remarks: "refreshToken 失效时间")
            column(name: "WEB_SERVER_REDIRECT_URI", type: "VARCHAR(2000)", remarks: "授权码模式下的重定向URI")
            column(name: "ADDITIONAL_INFORMATION", type: "VARCHAR(4000)", remarks: "自定义信息")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "REQUEST_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "PROGRAM_ID", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
        if (helper.isPostgresql()) {
            addUniqueConstraint(columnNames: "ID,CLIENT_ID", tableName: "SYS_OAUTH_CLIENT_DETAILS", constraintName: "SYS_OAUTH_CLIENT_U1")
        }
    }
}
