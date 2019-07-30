package io.choerodon.hap.excel.impl;

import io.choerodon.base.annotation.ExcelJoinColumn;
import io.choerodon.hap.excel.util.TableUtils;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.util.OGNL;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/11/21.
 */
public class ExcelSheetStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ExcelSheetStrategy.class);

    protected Connection connection;

    protected String tableName;

    protected List<String> columnName;

    protected Class dtoClass;

    protected List<Field> excelJoinColumns;

    //字段类型mapping表
    protected Map<String, Class> typeMapping = new HashMap<>();

    //替换表Mapping 减少查询数据库次数
    protected Map<String, Object> translateCellMap = new HashMap<>();

    protected Set<String> languages;

    public enum TranslateType {alterColumnTojoinColumn, joinColumnToAlterCloumn}

    public enum MapperType {Select, Insert}

    @SuppressWarnings("unchecked")
    public ExcelSheetStrategy(Connection connection, String tableName) {
        this.tableName = tableName;
        this.connection = connection;
        dtoClass = TableUtils.getTableClass(tableName);
        excelJoinColumns = TableUtils.getExcelJoinColumns(tableName);
        Set<EntityColumn> entityColumns = TableUtils.getAllColumns(tableName);
        entityColumns.forEach(v -> typeMapping.put(StringUtil.underlineToCamelhump(v.getColumn().toLowerCase()), v.getJavaType()));
        if (dtoClass.isAnnotationPresent(MultiLanguage.class)) {
            languages = OGNL.getSupportedLanguages();
        }
    }

    /**
     * 是否处理了多语言字段.
     *
     * @param column 字段
     * @param cell   cell
     * @param tls    多语言Map
     * @return 是否处理了多语言字段
     */
    @SuppressWarnings("unchecked")
    protected boolean translateLanguageCell(String column, String cell, Map tls) {
        for (String language : languages) {
            if (column.endsWith(":" + language)) {
                String col = column.substring(0, column.length() - language.length() - 1);
                Map<String, String> map = (Map<String, String>) Optional.ofNullable(tls.get(col)).orElseGet(() -> {
                    Map<String, String> map2 = new HashMap<>();
                    tls.put(col, map2);
                    return map2;
                });
                map.put(language, cell);
                return true;
            }
        }
        return false;
    }

    /**
     * 将dto中包含@ExcelJoinColumn注解的行替换为其所在头表中对应的数据.
     *
     * @param column        字段
     * @param cell          cell
     * @param translateType 转换类型
     * @param dto           DTO
     * @return Object
     * @throws IllegalAccessException    非法访问异常
     * @throws NoSuchMethodException     找不到方法异常
     * @throws InvocationTargetException 调用目标异常
     * @throws SQLException              SQL异常
     */
    protected Object translateCellValue(String column, String cell, TranslateType translateType, Object dto) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLException {
        Object rs = cell;
        if (null == cell || "".equals(cell)) {
            return null;
        }
        for (Field field : excelJoinColumns) {
            ExcelJoinColumn ann = field.getAnnotationsByType(ExcelJoinColumn.class)[0];

            if (field.getName().equalsIgnoreCase(column)) {
                if (null != dto) {
                    cell = BeanUtils.getProperty(dto, column);
                }

                if (translateCellMap.containsKey(column + "-" + cell)) {
                    return translateCellMap.get(column + "-" + cell);
                }
                String joinColumn = ann.JoinColumn();
                String alternateColumn = ann.AlternateColumn();
                String joinTable = TableUtils.getTable(ann.JoinTable()).getName();
                String sql;
                if (translateType == TranslateType.alterColumnTojoinColumn) {
                    sql = String.format("select %s from %s where %s = '%s'", StringUtil.underlineToCamelhump(joinColumn.toLowerCase()), joinTable, StringUtil.underlineToCamelhump(alternateColumn.toLowerCase()), cell);

                } else {
                    sql = String.format("select %s from %s where %s = '%s'", StringUtil.underlineToCamelhump(alternateColumn.toLowerCase()), joinTable, StringUtil.underlineToCamelhump(joinColumn.toLowerCase()), cell);
                }
                PreparedStatement stmt1 = connection.prepareStatement(sql);
                try (ResultSet set = stmt1.executeQuery()) {
                    if (set.next()) {
                        rs = set.getObject(1);
                        translateCellMap.put(column + "-" + cell, rs);
                        changeTypeMapping(column, ann.AlternateColumn(), ann.JoinTable());
                    }
                } catch (SQLException | NoSuchFieldException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } finally {
                    try {
                        stmt1.close();
                    } catch (SQLException e) {
                        logger.error("connection close failed!", e);
                    }
                }
                break;
            }
        }
        return rs;
    }

    /**
     * 将excel join字段的类型映射修改为替换字段的类型.
     *
     * @param column 字段
     * @param cls    Class
     * @throws NoSuchFieldException 无相应字段异常
     */
    public void changeTypeMapping(String column, String alternateColumn, Class cls) throws NoSuchFieldException {
        typeMapping.replace(column, cls.getDeclaredField(alternateColumn).getType());
    }


    /**
     * 删除缓存数据（应在每个表结束后执行）
     */
    protected void cleanData() {
        tableName = null;
        columnName = null;
        dtoClass = null;
        excelJoinColumns = null;
        typeMapping = new HashMap<>();
        translateCellMap = new HashMap<>();
    }
}
