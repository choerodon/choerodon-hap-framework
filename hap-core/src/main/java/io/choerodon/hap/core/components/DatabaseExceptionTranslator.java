/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.core.components;

import io.choerodon.hap.core.exception.DatabaseException;
import io.choerodon.hap.system.dto.DTOClassInfo;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
public class DatabaseExceptionTranslator {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private MessageSource messageSource;

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(DatabaseExceptionTranslator.class);

    private Map<Integer, AbstractDbExceptionHandler> HANDLER_MAPPING_U = new HashMap<>();
    private Map<Integer, AbstractDbExceptionHandler> HANDLER_MAPPING_N = new HashMap<>();
    private OracleDbExceptionHandler oracleDbExceptionHandler = new OracleDbExceptionHandler();
    private MySqlDbExceptionHandler mySqlDbExceptionHandler = new MySqlDbExceptionHandler();
    private SqlServerDbExceptionHandler sqlServerDbExceptionHandler = new SqlServerDbExceptionHandler();

    {
        HANDLER_MAPPING_U.put(1, oracleDbExceptionHandler);
        HANDLER_MAPPING_U.put(1062, mySqlDbExceptionHandler);
        HANDLER_MAPPING_U.put(2627, sqlServerDbExceptionHandler);
        HANDLER_MAPPING_U.put(2601, sqlServerDbExceptionHandler);
        /////
        HANDLER_MAPPING_N.put(1364, mySqlDbExceptionHandler);
        HANDLER_MAPPING_N.put(1048, mySqlDbExceptionHandler);
        HANDLER_MAPPING_N.put(1400, oracleDbExceptionHandler);
        HANDLER_MAPPING_N.put(1407, oracleDbExceptionHandler);
        HANDLER_MAPPING_N.put(515, sqlServerDbExceptionHandler);
    }

    public Exception translateException(Exception exception, Object domain) {
        Throwable rootCause = ExceptionUtils.getRootCause(exception);
        if (!(rootCause instanceof SQLException)) {
            return exception;
        }

        SQLException sqlException = (SQLException) rootCause;
        int errorCode = sqlException.getErrorCode();
        logger.error("SQLException Error Code: " + errorCode);
        logger.error(sqlException.getMessage(), sqlException);
        try {
            AbstractDbExceptionHandler handler = HANDLER_MAPPING_U.get(errorCode);
            if (handler != null) {
                return handler.handleUniqueException(sqlException, domain);
            }

            handler = HANDLER_MAPPING_N.get(errorCode);
            if (handler != null) {
                return handler.handleNullException(sqlException, domain);
            }

            logger.warn("can not translate database exception, error code:{}, message:{}", errorCode,
                    sqlException.getMessage());

        } catch (Exception e) {
            logger.error("error while translate database exception", e);
            return exception;
        }
        return exception;
    }

    private static Locale getLocale() {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        if (StringUtils.isEmpty(iRequest.getLocale())) {
            iRequest.setLocale("zh_CN");
        }
        String[] ss = iRequest.getLocale().split("_");
        return new Locale(ss[0], ss[1]);
    }

    abstract class AbstractDbExceptionHandler {
        public abstract List<String> getRelatedColumns(String indexName);

        protected abstract String getUniqueIndexName(String message);

        protected abstract String getNullFieldName(String message);

        public Exception handleUniqueException(SQLException sqlException, Object domain) throws Exception {
            String indexName = getUniqueIndexName(sqlException.getMessage());
            List<String> columnNames = getRelatedColumns(indexName);
            Class<?> clazz = domain.getClass();

            Locale locale = getLocale();

            StringBuilder sb = new StringBuilder();
            sb.append(messageSource.getMessage("hap.error.uniqueexception", null, locale));

            if (!(domain instanceof BaseDTO)) {
                sb.append(" ; ").append(sqlException.getMessage());
                return new DatabaseException(indexName + ":" + sqlException.getErrorCode(), sb.toString());
            }

            for (String cn : columnNames) {
                String field = DTOClassInfo.underLineToCamel(cn);
                String value0 = org.apache.commons.beanutils.BeanUtils.getProperty(domain, field);
                sb.append(" ; ").append(messageSource
                        .getMessage(StringUtils.lowerCase(clazz.getSimpleName() + "." + field), null, locale));
                sb.append(" : ").append(value0);
            }
            return new DatabaseException(indexName + ":" + sqlException.getErrorCode(), sb.toString());
        }

        public Exception handleNullException(SQLException sqlException, Object domain) throws Exception {
            String columnName = getNullFieldName(sqlException.getMessage());
            Class<?> clazz = domain.getClass();

            Locale locale = getLocale();

            StringBuilder sb = new StringBuilder();
            sb.append(messageSource.getMessage("hap.error.nullexception", null, locale));

            if (!(domain instanceof BaseDTO)) {
                sb.append(" ; ").append(sqlException.getMessage());
                return new DatabaseException(columnName + ":" + sqlException.getErrorCode(), sb.toString());
            }

            String field = DTOClassInfo.underLineToCamel(columnName);
            sb.append(" : ").append(
                    messageSource.getMessage(StringUtils.lowerCase(clazz.getSimpleName() + "." + field), null, locale));
            return new DatabaseException(columnName + ":" + sqlException.getErrorCode(), sb.toString());
        }
    }

    class MySqlDbExceptionHandler extends AbstractDbExceptionHandler {

        @Override
        public List<String> getRelatedColumns(String indexName) {
            List<String> columnNames = new ArrayList<>();
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement ps = connection.prepareStatement(
                        "select * from INFORMATION_SCHEMA.STATISTICS s where s.TABLE_SCHEMA=? and s.INDEX_NAME=?")) {
                    ps.setString(1, connection.getCatalog());
                    ps.setString(2, indexName);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            columnNames.add(rs.getString("COLUMN_NAME"));
                        }
                    }
                }
            } catch (SQLException e1) {
                logger.error(e1.getMessage(), e1);
            }
            return columnNames;
        }

        @Override
        public String getUniqueIndexName(String message) {
            int i0 = message.indexOf('\'');
            int i1 = message.lastIndexOf("' for key '");

            String value = null, indexName = null;

            try {
                value = message.substring(i0 + 1, i1);
                indexName = message.substring(i1 + 11, message.length() - 1);
            } catch (Exception e0) {
                logger.error("error while extract index name from error message:" + message, e0);
            }
            return indexName;
        }

        @Override
        protected String getNullFieldName(String message) {
            int i0 = message.indexOf("'");
            int i1 = message.indexOf("'", i0 + 1);
            return message.substring(i0 + 1, i1);
        }
    }

    class OracleDbExceptionHandler extends AbstractDbExceptionHandler {

        @Override
        public List<String> getRelatedColumns(String indexName) {
            List<String> columnNames = new ArrayList<>();
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement ps = connection
                        .prepareStatement("select * from user_ind_columns where index_name=?")) {
                    ps.setString(1, indexName);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            columnNames.add(rs.getString("COLUMN_NAME"));
                        }
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return columnNames;
        }

        @Override
        public String getUniqueIndexName(String message) {
            int i0 = message.lastIndexOf('(');
            int i1 = message.lastIndexOf(')');

            String indexName = null;

            try {
                indexName = message.substring(i0 + 1, i1);
                int i2 = indexName.lastIndexOf('.');
                if (i2 >= 0)
                    return indexName.substring(i2 + 1);
            } catch (Exception e0) {
                logger.error("error while extract index name from error message:" + message, e0);
            }
            return indexName;
        }

        @Override
        protected String getNullFieldName(String message) {
            int i0 = message.lastIndexOf('"');
            int i1 = message.lastIndexOf('"', i0 - 1);
            return message.substring(i1 + 1, i0);
        }
    }

    class SqlServerDbExceptionHandler extends AbstractDbExceptionHandler {

        @Override
        public List<String> getRelatedColumns(String indexName) {
            List<String> columnNames = new ArrayList<>();
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement ps = connection
                        .prepareStatement("SELECT  OBJECT_SCHEMA_NAME(ind.object_id) AS SCHEMA_NAME\n"
                                + "      , OBJECT_NAME(ind.object_id) AS TABLE_NAME\n"
                                + "      , col.name AS COLUMN_NAME\n" + "FROM    sys.indexes ind\n"
                                + "        INNER JOIN sys.index_columns ic\n"
                                + "            ON ind.object_id = ic.object_id\n"
                                + "               AND ind.index_id = ic.index_id\n"
                                + "        INNER JOIN sys.columns col\n"
                                + "            ON ic.object_id = col.object_id\n"
                                + "               AND ic.column_id = col.column_id\n"
                                + "        INNER JOIN sys.tables t\n" + "            ON ind.object_id = t.object_id\n"
                                + "WHERE   t.is_ms_shipped = 0 and ind.name =?")) {
                    ps.setString(1, indexName);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            columnNames.add(rs.getString("COLUMN_NAME"));
                        }
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return columnNames;
        }

        @Override
        public String getUniqueIndexName(String message) {
            int i0 = message.indexOf('"');
            if (i0 == -1) {
                i0 = message.indexOf('“');
            }
            int i1 = message.indexOf('"', i0 + 1);
            if (i1 == -1) {
                i1 = message.indexOf('”', i0 + 1);
            }

            String indexName = null;

            try {
                indexName = message.substring(i0 + 1, i1);
                int i2 = indexName.lastIndexOf('.');
                if (i2 >= 0)
                    return indexName.substring(i2 + 1);
            } catch (Exception e0) {
                logger.error("error while extract index name from error message:" + message, e0);
            }
            return indexName;
        }

        @Override
        protected String getNullFieldName(String message) {
            int i0 = message.indexOf("'");
            int i1 = message.indexOf("'", i0 + 1);
            return message.substring(i0 + 1, i1);
        }
    }

}
