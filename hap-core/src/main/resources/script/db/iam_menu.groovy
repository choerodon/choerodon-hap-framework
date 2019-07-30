package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu.groovy') {
    changeSet(author: 'guokai.wu.work@gmail.com', id: '2018-03-29-iam-menu') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'IAM_MENU_B_S', startValue: "1")
        }
        createTable(tableName: "IAM_MENU_B") {
            if (helper.dbType().isSupportAutoIncrement()) {
                column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                    constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_B')
                }
            } else {
                column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '表ID，主键，供其他表做外键，unsigned bigint') {
                    constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_B')
                }
            }
            column(name: 'CODE', type: 'VARCHAR(128)', remarks: '菜单的标识') {
                constraints(nullable: false)
            }
            column(name: 'NAME', type: 'VARCHAR(128)', remarks: '菜单名') {
                constraints(nullable: false)
            }
            column(name: 'RESOURCE_LEVEL', type: 'VARCHAR(64)', remarks: '菜单层级', defaultValue: 'site')
            column(name: 'PARENT_CODE', type: 'VARCHAR(128)', remarks: '父级菜单编码')
            column(name: 'TYPE', type: 'VARCHAR(64)', remarks: '菜单类型， 包括三种(top,menu,menu_item)') {
                constraints(nullable: false)
            }
            column(name: 'SORT', type: 'BIGINT UNSIGNED', remarks: '菜单顺序')
            column(name: 'IS_DEFAULT', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否是默认菜单,0不是默认菜单，1是默认菜单')
            column(name: 'ICON', type: 'VARCHAR(128)', remarks: '图标的code值') {
                constraints(nullable: false)
            }
            column(name: 'PAGE_PERMISSION_CODE', type: 'VARCHAR(128)', remarks: 'permission code作为外键')
            column(name: 'SEARCH_CONDITION', type: 'TEXT', remarks: '条件表达式',)
            column(name: 'SERVICE_CODE', type: 'VARCHAR(128)', remarks: '服务code', defaultValue: 'iam-service')
            column(name: 'CATEGORY', type: 'VARCHAR(64)', remarks: '项目层菜单分类，可以为AGILE，PROGRAM，ANALYTICAL')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'IAM_MENU_B', columnNames: 'CODE', constraintName: 'UK_IAM_MENU_B_U1')

        createTable(tableName: "IAM_MENU_TL") {
            column(name: 'LANG', type: 'VARCHAR(16)', remarks: '语言名称') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_TL_P1')
            }
            column(name: 'ID', type: 'BIGINT UNSIGNED', remarks: '资源ID') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_MENU_TL_P2')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '菜单名')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}