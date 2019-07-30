package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_permission.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-04-02-iam-permission') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'IAM_PERMISSION_S', startValue: "1")
        }
        createTable(tableName: "IAM_PERMISSION") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                    constraints(primaryKey: true, primaryKeyName: 'PK_IAM_PERMISSION')
                }
            } else {
                column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '表ID，主键，供其他表做外键，unsigned bigint') {
                    constraints(primaryKey: true, primaryKeyName: 'PK_IAM_PERMISSION')
                }
            }
            column(name: 'CODE', type: 'VARCHAR(128)', remarks: '权限的标识') {
                constraints(nullable: false)
            }
            column(name: 'PATH', type: 'VARCHAR(128)', remarks: '权限对应的api路径') {
                constraints(nullable: false)
            }
            column(name: 'PERMISSION_TYPE', type: 'VARCHAR(128)', remarks: '类型包括url/api/page等', defaultValue: 'api')
            column(name: 'CONTROLLER', type: 'VARCHAR(128)', remarks: 'controller名')
            column(name: 'METHOD', type: 'VARCHAR(64)', remarks: '请求的http方法')
            column(name: 'ACTION', type: 'VARCHAR(64)', remarks: '权限对应的方法名')
            column(name: 'DESCRIPTION', type: 'VARCHAR(1024)', remarks: '权限描述')
            column(name: 'RESOURCE_LEVEL', type: 'VARCHAR(64)', remarks: '权限的层级')
            column(name: 'IS_PUBLIC_ACCESS', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否公开的权限')
            column(name: 'IS_LOGIN_ACCESS', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否需要登录才能访问的权限')
            column(name: 'IS_WITHIN', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否为内部接口')
            column(name: 'SERVICE_CODE', type: 'VARCHAR(128)', remarks: '权限所在的服务编码')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'IAM_PERMISSION', columnNames: 'CODE', constraintName: 'UK_IAM_PERMISSION_U1')
        addUniqueConstraint(tableName: 'IAM_PERMISSION', columnNames: 'ACTION,CONTROLLER,SERVICE_CODE', constraintName: 'UK_IAM_PERMISSION_U2')
        addUniqueConstraint(tableName: 'IAM_PERMISSION', columnNames: 'PATH,RESOURCE_LEVEL,SERVICE_CODE,METHOD,CODE', constraintName: 'UK_IAM_PERMISSION_U3')
    }
}