package io.choerodon.hap.flexfield.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.flexfield.dto.ColumnName;
import io.choerodon.hap.flexfield.dto.FlexModelColumn;
import io.choerodon.hap.flexfield.service.IFlexModelColumnService;
import io.choerodon.hap.generator.service.impl.DBUtil;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Dataset("FlexModelColumn")
public class FlexModelColumnServiceImpl extends BaseServiceImpl<FlexModelColumn> implements IFlexModelColumnService, IDatasetService<FlexModelColumn> {

    @Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public List<String> getTableColumn(String tableName) {
        List<String> columnList = new ArrayList<>();
        SqlSession sqlSession = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            connection = DBUtil.getConnectionBySqlSession(sqlSession);
            resultSet = DBUtil.getTableColumnInfo(tableName, connection.getMetaData());
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                if ("OBJECT_VERSION_NUMBER".equalsIgnoreCase(columnName) || "REQUEST_ID".equalsIgnoreCase(columnName)
                        || "PROGRAM_ID".equalsIgnoreCase(columnName) || "CREATED_BY".equalsIgnoreCase(columnName)
                        || "CREATION_DATE".equalsIgnoreCase(columnName) || "LAST_UPDATED_BY".equalsIgnoreCase(columnName)
                        || "LAST_UPDATE_DATE".equalsIgnoreCase(columnName) || "LAST_UPDATE_LOGIN".equalsIgnoreCase(columnName)
                        ) {
                    continue;
                }
                columnList.add(columnName);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                DBUtil.closeResultSet(resultSet);
                DBUtil.closeConnection(connection);
                DBUtil.closeSqlSession(sqlSession);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return columnList;
    }

    @Override
    public List<ColumnName> getTableColumnObj(String tableName) {
        List<ColumnName> columnList = new ArrayList<>();
        SqlSession sqlSession = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
            connection = DBUtil.getConnectionBySqlSession(sqlSession);
            resultSet = DBUtil.getTableColumnInfo(tableName, connection.getMetaData());
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                if ("OBJECT_VERSION_NUMBER".equalsIgnoreCase(columnName) || "REQUEST_ID".equalsIgnoreCase(columnName)
                        || "PROGRAM_ID".equalsIgnoreCase(columnName) || "CREATED_BY".equalsIgnoreCase(columnName)
                        || "CREATION_DATE".equalsIgnoreCase(columnName) || "LAST_UPDATED_BY".equalsIgnoreCase(columnName)
                        || "LAST_UPDATE_DATE".equalsIgnoreCase(columnName) || "LAST_UPDATE_LOGIN".equalsIgnoreCase(columnName)
                        ) {
                    continue;
                }
                ColumnName columnNameObj = new ColumnName();
                columnNameObj.setColumnName(columnName);
                columnList.add(columnNameObj);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                DBUtil.closeResultSet(resultSet);
                DBUtil.closeConnection(connection);
                DBUtil.closeSqlSession(sqlSession);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return columnList;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            FlexModelColumn flexModelColumn = new FlexModelColumn();
            BeanUtils.populate(flexModelColumn, body);
            return selectOptions(flexModelColumn, null, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.FlexModelColumn", e);
        }
    }

    @Override
    public List<FlexModelColumn> mutations(List<FlexModelColumn> objs) {
        for (FlexModelColumn flexModelColumn : objs) {
            switch (flexModelColumn.get__status()) {
                case DTOStatus.ADD:
                    insertSelective(flexModelColumn);
                    break;
                case DTOStatus.UPDATE:
                    updateByPrimaryKeySelective(flexModelColumn);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(flexModelColumn);
                    break;
            }
        }
        return objs;
    }
}