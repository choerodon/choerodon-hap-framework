package io.choerodon.hap.system.dto;

import io.choerodon.base.annotation.Children;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.common.query.JoinCache;
import io.choerodon.mybatis.common.query.JoinCode;
import io.choerodon.mybatis.common.query.JoinLov;
import org.apache.commons.lang.StringUtils;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.mapperhelper.FieldHelper;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取,并缓存dto类中的字段信息.
 *
 * @author shengyang.zhou@hand-china.com
 */
public final class DTOClassInfo {

    private static final Comparator<EntityField> FIELD_COMPARATOR = Comparator.comparing(EntityField::getName);

    @SuppressWarnings("unchecked")
    /**
     * the order of this array can not be change. <br>
     * if more Annotations should be concerned, please add it to last
     */
    private static final Class<? extends Annotation>[] CONCERNED_ANNOTATION = new Class[]{Id.class, Children.class,
            MultiLanguageField.class, JoinCache.class, JoinCode.class, JoinLov.class};

    @SuppressWarnings("unchecked")
    private static final Map<Class<?>, EntityField[]>[] CLASS_ANNO_MAPPING = new HashMap[CONCERNED_ANNOTATION.length];

    private static final Map<Class<?>, Map<String, EntityField>> CLASS_FIELDS_MAPPING = new HashMap<>();

    static {
        for (int i = 0; i < CLASS_ANNO_MAPPING.length; i++) {
            CLASS_ANNO_MAPPING[i] = new HashMap<>();
        }
    }

    private static final Map<String, String> CAMEL_UL_MAP = new HashMap<>();
    private static final Map<String, String> UL_CAMEL_MAP = new HashMap<>();

    private DTOClassInfo() {
    }

    /**
     * 取得所有带有@Id注解的Field.
     * <p>
     * EntityField 已经被设置setAccessible(true)
     *
     * @param clazz 类型
     * @return 有多个字段时, 按照字母表顺序排序.没有匹配类型时, 返回空数组, 不返回null
     */
    public static EntityField[] getIdFields(Class<?> clazz) {
        return getFields0(clazz, 0);
    }

    /**
     * 取得所有带有@Children注解的Field.
     * <p>
     * EntityField 已经被设置setAccessible(true)
     *
     * @param clazz 类型
     * @return 有多个字段时, 按照字母表顺序排序.没有匹配类型时, 返回空数组, 不返回null
     */
    public static EntityField[] getChildrenFields(Class<?> clazz) {
        return getFields0(clazz, 1);
    }

    /**
     * 取得所有带有@MultiLanguageField注解的Field.
     * <p>
     * EntityField 已经被设置setAccessible(true)
     *
     * @param clazz 类型
     * @return 有多个字段时, 按照字母表顺序排序.没有匹配类型时, 返回空数组, 不返回null
     */
    public static EntityField[] getMultiLanguageFields(Class<?> clazz) {
        return getFields0(clazz, 2);
    }

    private static EntityField[] getFields0(Class<?> clazz, int idx) {
        EntityField[] fields = CLASS_ANNO_MAPPING[idx].get(clazz);
        if (fields == null) {
            analysis(clazz);
            fields = CLASS_ANNO_MAPPING[idx].get(clazz);
        }
        return fields;
    }

    /**
     * 按照Annotation类型获取字段集.
     *
     * @param clazz    类型
     * @param annoType Annotation Type
     * @return EntityField array, return empty array if annoType is not support.
     */
    public static EntityField[] getFieldsOfAnnotation(Class<?> clazz, Class<? extends Annotation> annoType) {
        int idx = Arrays.asList(CONCERNED_ANNOTATION).indexOf(annoType);
        if (idx != -1) {
            return getFields0(clazz, idx);
        }
        return new EntityField[0];
    }

    @SuppressWarnings("unchecked")
    private static void analysis(Class<?> clazz) {
        List<EntityField> fields = FieldHelper.getAll(clazz);

        fields.sort(FIELD_COMPARATOR);

        // List<EntityField> fields = new ArrayList<>();
        // ReflectionUtils.doWithFields(clazz, fields::add);
        // Collections.sort(fields, FIELD_COMPARATOR);
        List<EntityField>[] lists = new List[CONCERNED_ANNOTATION.length];
        for (int i = 0; i < CONCERNED_ANNOTATION.length; i++) {
            lists[i] = new ArrayList<>();
        }

        Map<String, EntityField> fieldMap = new HashMap<>();

        for (EntityField f : fields) {
            for (int i = 0; i < CONCERNED_ANNOTATION.length; i++) {
                if (f.getAnnotation(CONCERNED_ANNOTATION[i]) != null) {
                    // f.setAccessible(true);
                    lists[i].add(f);
                }
            }
            fieldMap.put(f.getName(), f);
        }
        CLASS_FIELDS_MAPPING.put(clazz, fieldMap);
        for (int i = 0; i < CLASS_ANNO_MAPPING.length; i++) {
            EntityField[] fs = lists[i].toArray(new EntityField[lists[i].size()]);
            CLASS_ANNO_MAPPING[i].put(clazz, fs);
        }
    }

    public static EntityField getEntityField(Class<?> clazz, String field) {
        Map<String, EntityField> map = CLASS_FIELDS_MAPPING.get(clazz);
        if (map == null) {
            analysis(clazz);
            map = CLASS_FIELDS_MAPPING.get(clazz);
        }
        return map.get(field);
    }

    public static Map<String, EntityField> getEntityFields(Class<?> clazz) {
        Map<String, EntityField> map = CLASS_FIELDS_MAPPING.get(clazz);
        if (map == null) {
            analysis(clazz);
        }
        return map;
    }

    /**
     * convert camel hump to under line case.
     *
     * @param camel can not be null
     * @return under line case
     */
    public static String camelToUnderLine(String camel) {
        String ret = CAMEL_UL_MAP.get(camel);
        if (ret == null) {
            ArrayList<String> tmp = new ArrayList<>();
            int lastIdx = 0;
            for (int i = 0; i < camel.length(); i++) {
                if (Character.isUpperCase(camel.charAt(i))) {
                    tmp.add(camel.substring(lastIdx, i).toLowerCase());
                    lastIdx = i;
                }
            }
            tmp.add(camel.substring(lastIdx));
            ret = StringUtils.join(tmp, "_");
            CAMEL_UL_MAP.put(camel, ret);
        }
        return ret;
    }

    public static String underLineToCamel(String str) {
        String camel = UL_CAMEL_MAP.get(str);
        if (camel == null) {
            String[] array = str.toLowerCase().split("_");
            for (int i = 1; i < array.length; i++) {
                array[i] = StringUtils.capitalize(array[i]);
            }
            camel = StringUtils.join(array);
            UL_CAMEL_MAP.put(str, camel);
        }
        return camel;
    }

    /**
     * get the column name for sql.
     * <p>
     * if there is a @Column on EntityField and @Column.name() is not empty,
     * then use the name.<br>
     * else , convert fieldName to under line case
     *
     * @param field EntityField
     * @return column name of under line case
     */
    public static String getColumnName(EntityField field) {
        Column col = field.getAnnotation(Column.class);
        if (col == null || StringUtils.isEmpty(col.name())) {
            return camelToUnderLine(field.getName());
        }
        return col.name();
    }

    /**
     * 根据Object获取Table name,如果传入的参数，没有Table(父类也没有)注解
     * 则返回null
     *
     * @param clazz can not be null
     * @return table name or null
     */
    public static String getTableName(Class<?> clazz) {
        String tableName = null;
        Table anonotation = getTableAnnotation(clazz);
        if (anonotation != null) {
            tableName = anonotation.name();
        }
        return tableName;
    }

    private static Table getTableAnnotation(Class<?> clazz) {
        Table annotation = null;
        annotation = clazz.getAnnotation(Table.class);
        if (annotation == null && clazz.getSuperclass() != null) {
            annotation = getTableAnnotation(clazz.getSuperclass());
        }
        return annotation;
    }

}
