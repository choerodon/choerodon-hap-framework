package io.choerodon.hap.generator.service.impl;

import com.github.pagehelper.util.StringUtil;
import io.choerodon.hap.generator.dto.DBColumn;
import io.choerodon.hap.generator.dto.DBTable;
import io.choerodon.hap.generator.dto.GeneratorInfo;
import io.choerodon.hap.generator.dto.TableName;
import io.choerodon.hap.generator.service.IHapGeneratorService;
import freemarker.template.TemplateException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by jialong.zuo@hand-china.com on 2016/10/24.
 */
@Service
public class HapGeneratorServiceImpl implements IHapGeneratorService {
    public static final Set<String> MULTI_LANGUAGE_SKIP_COLUMNS = new TreeSet<>();

    static {
        MULTI_LANGUAGE_SKIP_COLUMNS.add("OBJECT_VERSION_NUMBER");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("REQUEST_ID");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("PROGRAM_ID");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("CREATED_BY");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("CREATION_DATE");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("LAST_UPDATED_BY");
        MULTI_LANGUAGE_SKIP_COLUMNS.add("LAST_UPDATE_DATE");
        for (int i = 1; i <= 15; i++) {
            MULTI_LANGUAGE_SKIP_COLUMNS.add("ATTRIBUTE" + i);
        }
    }

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<String> showTables() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<String> tables;

            Connection conn = DBUtil.getConnectionBySqlSession(sqlSession);
            tables = DBUtil.showAllTables(conn);
            conn.close();
            return tables;
        } catch (SQLException e) {
            logger.error("数据库查询出错");
        }
        return new ArrayList<String>();
    }

    @Override
    public List<TableName> showTablesObj() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<TableName> tables;

            Connection conn = DBUtil.getConnectionBySqlSession(sqlSession);
            tables = DBUtil.showAllTablesObj(conn);
            conn.close();
            return tables;
        } catch (SQLException e) {
            logger.error("数据库查询出错");
        }
        return new ArrayList<TableName>();
    }

    @Override
    public int generatorFile(GeneratorInfo info) {
        int rs = 0;
        String tableName = info.getTargetName();
        DBTable dbTable = getTableInfo(tableName);
        try {
            rs = createFile(dbTable, info);
        } catch (IOException | TemplateException e) {
            rs = -1;
            logger.error(e.getMessage());
        }
        return rs;
    }

    // 获取table信息
    public DBTable getTableInfo(String tableName) {
        Connection conn = null;
        DBTable dbTable = new DBTable();
        List<DBColumn> columns = dbTable.getColumns();
        List<String> multiColumns = null;
        List<String> NotNullColumns = null;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            // 设置tablename
            dbTable.setName(tableName);
            conn = DBUtil.getConnectionBySqlSession(sqlSession);
            DatabaseMetaData dbmd = conn.getMetaData();
            // 是否为多语言表
            boolean multiLanguage = DBUtil.isMultiLanguageTable(tableName);
            if (multiLanguage) {
                dbTable.setMultiLanguage(multiLanguage);
                multiColumns = DBUtil.isMultiLanguageColumn(tableName, dbmd);
                // 判断多语言字段
            }
            // 获取主键字段
            String columnPk = DBUtil.getPrimaryKey(tableName, dbmd);
            // 获取不为空的字段
            NotNullColumns = DBUtil.getNotNullColumn(tableName, dbmd);
            // 获取表列信息
            ResultSet rs1 = DBUtil.getTableColumnInfo(tableName, dbmd);

            while (rs1.next()) {
                String columnName = rs1.getString("COLUMN_NAME");
                if ("OBJECT_VERSION_NUMBER".equalsIgnoreCase(columnName) || "REQUEST_ID".equalsIgnoreCase(columnName)
                        || "PROGRAM_ID".equalsIgnoreCase(columnName) || "CREATED_BY".equalsIgnoreCase(columnName)
                        || "CREATION_DATE".equalsIgnoreCase(columnName) || "LAST_UPDATED_BY".equalsIgnoreCase(columnName)
                        || "LAST_UPDATE_DATE".equalsIgnoreCase(columnName) || "LAST_UPDATE_LOGIN".equalsIgnoreCase(columnName)
                        || columnName.toUpperCase().startsWith("ATTRIBUTE")) {
                    continue;
                }
                columns.add(setColumnInfo(rs1, columnPk, NotNullColumns, multiLanguage, multiColumns));
            }
            // 是否是多语言表
            rs1.close();
            conn.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return dbTable;
    }

    private DBColumn setColumnInfo(ResultSet rs1, String columnPk, List<String> NotNullColumns, boolean multiLanguage, List<String> multiColumns) throws SQLException {
        DBColumn column = new DBColumn();
        String columnName = rs1.getString("COLUMN_NAME");
        column.setName(columnName);
        String typeName = rs1.getString("TYPE_NAME");
        column.setJdbcType(typeName);
        if (StringUtil.isNotEmpty(rs1.getString("REMARKS"))) {
            column.setRemarks(rs1.getString("REMARKS"));
        }
        // 判断是否为主键
        if (columnName.equalsIgnoreCase(columnPk)) {
            column.setId(true);
        }
        // 判断是否为null字段
        for (String n : NotNullColumns) {
            if (columnName.equalsIgnoreCase(n) && !columnName.equalsIgnoreCase(columnPk)) {
                if ("BIGINT".equalsIgnoreCase(typeName)) {
                    column.setNotNull(true);
                } else if ("VARCHAR".equalsIgnoreCase(typeName)) {
                    column.setNotEmpty(true);
                }
            }
        }
        // 判断多语言表中的多语言字段
        if (multiLanguage) {
            for (String m : multiColumns) {
                if (m.equals(columnName)) {
                    column.setMultiLanguage(true);
                    break;
                }
            }
        }
        column.setColumnLength(rs1.getString("COLUMN_SIZE"));
        return column;
    }


    public int createFile(DBTable table, GeneratorInfo info) throws IOException, TemplateException {

        int rs = FileUtil.isFileExist(info);
        if (rs == 0) {
            if (!"NotOperation".equalsIgnoreCase(info.getDtoStatus())) {
                FileUtil.createDto(table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getControllerStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Controller, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getMapperStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Mapper, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getImplStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Impl, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getServiceStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Service, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getMapperXmlStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.MapperXml, table, info);
            }
            if (!"NotOperation".equalsIgnoreCase(info.getHtmlStatus())) {
                FileUtil.createFtlInfoByType(FileUtil.pType.Html, table, info);
            }
        }
        return rs;
    }

}
