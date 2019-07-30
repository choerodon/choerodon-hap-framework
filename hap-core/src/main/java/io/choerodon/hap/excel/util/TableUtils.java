package io.choerodon.hap.excel.util;

import io.choerodon.base.annotation.Children;
import io.choerodon.base.annotation.ExcelJoinColumn;
import io.choerodon.base.helper.ApplicationContextHelper;
import io.choerodon.hap.excel.impl.ExcelSheetStrategy;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.util.OGNL;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.ResolvableType;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表工具类.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2017/11/15.
 */
public class TableUtils {
    private static final Logger logger = LoggerFactory.getLogger(TableUtils.class);
    private static List<String> STD_WHO_COLUMN = new ArrayList<>();

    static {
        STD_WHO_COLUMN.add(BaseDTO.FIELD_CREATED_BY);
        STD_WHO_COLUMN.add(BaseDTO.FIELD_OBJECT_VERSION_NUMBER);
        STD_WHO_COLUMN.add(BaseDTO.FIELD_PROGRAM_ID);
        STD_WHO_COLUMN.add(BaseDTO.FIELD_REQUEST_ID);
        STD_WHO_COLUMN.add(BaseDTO.FIELD_CREATION_DATE);
        STD_WHO_COLUMN.add(BaseDTO.FIELD_LAST_UPDATE_DATE);
        STD_WHO_COLUMN.add(BaseDTO.FIELD_LAST_UPDATED_BY);
    }

    /**
     * 根据表名获取表的实体类.
     *
     * @param tableName 表名
     * @return 表的实体类
     */
    @SuppressWarnings("unchecked")
    public static EntityTable getEntityTable(String tableName) {
        EntityTable entityTable = null;
        try {
            Field entityTableMapField = EntityHelper.class.getDeclaredField("entityTableMap");
            entityTableMapField.setAccessible(true);
            Map<Class<?>, EntityTable> entityTableMap = (Map<Class<?>, EntityTable>) entityTableMapField.get(null);
            for (EntityTable entity : entityTableMap.values()) {
                if (entity.getName().equalsIgnoreCase(tableName)) {
                    entityTable = entity;
                    break;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        if (null == entityTable) {
            throw new RuntimeException("无法通过表名" + tableName + "获取对应的表实体类!");
        }
        return entityTable;
    }

    /**
     * 通过表名获取表的映射类.
     *
     * @param tableName 表名
     * @return 表的映射类
     */
    public static Class getTableClass(String tableName) {
        return getEntityTable(tableName).getEntityClass();
    }

    /**
     * 通过表的映射类获取表的实体类.
     *
     * @param dto 表的映射类
     * @return 表的实体类
     */
    public static EntityTable getTable(Class dto) {
        return EntityHelper.getEntityTable(dto);
    }

    /**
     * 根据表名获取ExcelJoinColumn标注的列.
     *
     * @param tableName 表名
     * @return ExcelJoinColumn标注的列
     */
    public static List<Field> getExcelJoinColumns(String tableName) {
        Class tableClass = getTableClass(tableName);
        return getExcelJoinColumn(tableClass);
    }

    /**
     * 根据表的映射类获取ExcelJoinColumn标注的列.
     *
     * @param tableDto 表的映射类
     * @return ExcelJoinColumn标注的列
     */
    public static List<Field> getExcelJoinColumn(Class tableDto) {
        return Arrays.stream(tableDto.getDeclaredFields()).filter(v -> {
            return v.isAnnotationPresent(ExcelJoinColumn.class);
        }).collect(Collectors.toList());
    }

    /**
     * 根据表的映射类获取MultiLanguageField标注的列.
     *
     * @param tableDto 表的映射类
     * @return MultiLanguageField标注的列
     */
    public static List<Field> getMultiLanguageColumn(Class tableDto) {
        if (!tableDto.isAnnotationPresent(MultiLanguage.class)) {
            return new ArrayList<>();
        }
        return Arrays.stream(tableDto.getDeclaredFields()).filter(v -> v.isAnnotationPresent(MultiLanguageField.class)).collect(Collectors.toList());
    }

    /**
     * 根据表的映射类获取Children标注的列.
     *
     * @param dto 表的映射类
     * @return Children标注的列
     */
    public static List<Field> getChildrenColumn(Class dto) {
        return Arrays.stream(dto.getDeclaredFields()).filter(v -> {
            if (v.isAnnotationPresent(Children.class)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    /**
     * 通过表的映射类获取其InsertMapper.
     *
     * @param dtoClass   表的映射类
     * @param mapperType mapper类型
     * @return InsertMapper
     * @throws ClassNotFoundException 类未找到异常
     */
    public static BaseMapper getBaseMapperByType(Class dtoClass, ExcelSheetStrategy.MapperType mapperType) throws ClassNotFoundException {
        String[] names = ApplicationContextHelper.getApplicationContext().getBeanNamesForType(ResolvableType.forClassWithGenerics(BaseMapper.class, dtoClass));
        if (names.length != 0) {
            return (BaseMapper) ApplicationContextHelper.getApplicationContext().getBean(names[0]);
        }
        return null;
    }

    /**
     * 根据表名获取所有列.
     *
     * @param tableName 表名
     * @return 表的所有列
     */
    public static Set getAllColumns(String tableName) {
        return EntityHelper.getColumns(getTableClass(tableName));
    }

    /**
     * 根据column获取title.
     *
     * @param column 字段列表
     * @param dto    表的映射类
     * @return title
     */
    public static List<String> getTitle(List<String> column, Class dto) {
        String[] lang = OGNL.language().split("_");
        MessageSource messageSource = ApplicationContextHelper.getApplicationContext().getBean(MessageSource.class);
        return column.stream().map(v -> {
            String title = dto.getSimpleName().toLowerCase() + "." + v.toLowerCase();
            title = messageSource.getMessage(title, null, new Locale(lang[0], lang[1]));
            return title;
        }).collect(Collectors.toList());

    }

    /**
     * 获取表实体的字段
     *
     * @param tableEntity    表实体 表实体
     * @param containColumns 包含的字段
     * @return 表实体的字段
     */
    public static List<String> getColumn(EntityTable tableEntity, List<String> containColumns) {
        return getColumn(tableEntity, containColumns, true);
    }

    /**
     * 获取表实体的字段.
     *
     * @param tableEntity    表实体
     * @param containColumns 包含的字段
     * @param containStdWho  是否包含标准WHO字段
     * @return 表实体的字段
     */
    public static List<String> getColumn(EntityTable tableEntity, List<String> containColumns, boolean containStdWho) {
        Class dto = tableEntity.getEntityClass();
        String dtoName = dto.getSimpleName();
        List<Field> languageColumns = TableUtils.getMultiLanguageColumn(dto);
        List<String> column = new ArrayList<>();
        Set<EntityColumn> allColumns = tableEntity.getEntityClassColumns();
        if (null != containColumns) {
            allColumns.stream().map(m -> StringUtil.underlineToCamelhump(m.getColumn())).filter(u -> containColumns.contains(dtoName + "." + u)).forEach(column::add);
        } else {
            allColumns.stream().map(m -> StringUtil.underlineToCamelhump(m.getColumn())).forEach(column::add);
        }
        if (!languageColumns.isEmpty() && null == containColumns) {
            Set<String> languages = OGNL.getSupportedLanguages();
            languageColumns.forEach(v -> {
                if (column.contains(v.getName())) {
                    languages.forEach(u -> column.add(v.getName() + ":" + u));
                }
            });
        }
        if (!containStdWho) {
            column.removeAll(STD_WHO_COLUMN);
        }

        return column;
    }


    /**
     * 通过tableName查询所有关联表.
     *
     * @param parentTableName 基表表明
     * @param tables          递归查询表名并将其放入tables集合中
     */
    public static void getAllChildrenTable(Class parentTableName, List<Class> tables) {
        tables.add(parentTableName);
        getChildren(parentTableName).forEach(v -> getAllChildrenTable(v, tables));
    }

    /**
     * 根据表的映射类获取所有Children类.
     *
     * @param entityTable 表的映射类
     * @return 所有Children类
     */
    @SuppressWarnings("unchecked")
    public static List<Class> getChildren(Class entityTable) {
        List<Class> tables = new ArrayList<>();
        List<Field> childrenField = TableUtils.getChildrenColumn(entityTable);
        if (!childrenField.isEmpty()) {
            for (Field aChildrenField : childrenField) {
                Type type = aChildrenField.getGenericType();
                Class entityClass;
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    entityClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
                } else {
                    entityClass = (Class) type;
                }
                tables.add(entityClass);
            }
        }
        return tables;
    }

}
