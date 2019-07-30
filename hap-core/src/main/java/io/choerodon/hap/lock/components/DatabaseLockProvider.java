package io.choerodon.hap.lock.components;

import io.choerodon.hap.lock.exception.LockException;
import io.choerodon.hap.lock.util.LockKeyUtil;
import io.choerodon.hap.system.dto.DTOClassInfo;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.util.StringUtil;

import javax.persistence.Column;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Qixiangyu on 2017/1/16.
 */
@Component
public class DatabaseLockProvider {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseLockProvider.class);

    private static final String DATABASE_MYSQL = "mysql";

    private static final String DATABASE_ORACLE = "oracle";

    private static final String DATABASE_MSSQL = "mssql";

    private String dbType = DATABASE_MYSQL;

    private DataSource dataSource;

    @Autowired
    public DatabaseLockProvider(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
            DatabaseMetaData meta = conn.getMetaData();
            dbType = meta.getDatabaseProductName().toLowerCase();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用数据库锁，根据传入对象，自动加锁。
     *
     * @param dto
     *            确保bean上有@Table 注解，能自动解析出对应的表。 默认根据有@ID注解的字段进行加锁，确保id有值
     */
    public void lock(Object dto) {
        String tabaleName = DTOClassInfo.getTableName(dto.getClass());
        if (StringUtil.isEmpty(tabaleName)) {
            logger.error("table name is null, dto must has @Table");
            throw new RuntimeException(new LockException(LockException.ERROR_LOCK_FAILURE, null));
        }
        StringBuilder whereCondition = new StringBuilder();
        EntityField[] fields = DTOClassInfo.getIdFields(dto.getClass());
        if (fields.length == 0) {
            logger.error("can not get id field ,dto must has @ID");
            throw new RuntimeException(new LockException(LockException.ERROR_LOCK_FAILURE, null));
        }
        Object[] parms = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            try {
                // 拼接 id1 = ? and id2 = ? ...
                String idName = "";
                if(fields[i].isAnnotationPresent(Column.class)){
                    Column column = fields[i].getAnnotation(Column.class);
                    idName = column.name();
                }
                if(StringUtils.isEmpty(idName)){
                    idName = LockKeyUtil.camelToUnderline(fields[i].getName());
                }
                whereCondition.append(idName).append(" = ? ");
                if (i < fields.length - 1) {
                    whereCondition.append(" and ");
                }
                // 获得id的值
                Object object = PropertyUtils.getProperty(dto, fields[i].getName());
                if (object == null) {
                    logger.error("id's value is null");
                    throw new RuntimeException(new LockException(LockException.ERROR_LOCK_FAILURE, null));
                }
                parms[i] = object;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        lock(tabaleName, whereCondition.toString(), parms);
    }

    /**
     * 使用数据库锁，根据传入对象和自定义的where条件，自动加锁。
     *
     * @param dto
     *            确保bean上有@Table 注解，能自动解析出对应的表。
     * @param whereCondition
     *            传入类似 "name = ? and age = ?" 的条件语句
     * @param whereParameter
     *            不定长参数，根据whereCondition，依次对应传入属性对应的值。
     */
    public void lock(Object dto, String whereCondition, Object... whereParameter) {
        String tabaleName = DTOClassInfo.getTableName(dto.getClass());
        if (StringUtil.isEmpty(tabaleName)) {
            logger.error("table name is null, dto must has @Table");
            throw new RuntimeException(new LockException(LockException.ERROR_LOCK_FAILURE, null));
        }
        lock(tabaleName, whereCondition, whereParameter);
    }

    /**
     * 使用数据库锁，自定义表名和自定义的where条件，自动加锁。
     *
     * @param tableName
     *            自定义要加锁的表名。
     * @param whereCondition
     *            传入类似 "name = ? and age = ?" 的条件语句
     * @param whereParameter
     *            不定长参数，根据whereCondition，依次对应传入属性对应的值。
     */
    public void lock(String tableName, String whereCondition, Object... whereParameter) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement(getSqlStr(tableName, whereCondition));) {
            for (int i = 0; i < whereParameter.length; i++) {
                pstmt.setObject(i + 1, whereParameter[i]);
            }
            pstmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    String getSqlStr(String tableName, String whereCondition) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(tableName).append(" ");
        switch (dbType) {
        case DATABASE_MYSQL:
        case DATABASE_ORACLE:
            if (StringUtil.isNotEmpty(whereCondition)) {
                sb.append(" where ").append(whereCondition);
            }
            sb.append(" for update");
            break;
        case DATABASE_MSSQL:
            sb.append(" with (ROWLOCK) ");
            if (StringUtil.isNotEmpty(whereCondition)) {
                sb.append(whereCondition);
            }
        }
        return sb.toString();
    }

}
