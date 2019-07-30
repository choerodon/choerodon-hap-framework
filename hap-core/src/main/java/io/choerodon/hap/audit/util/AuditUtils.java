package io.choerodon.hap.audit.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.choerodon.hap.core.util.CommonUtils;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.web.util.TimeZoneUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class AuditUtils {

    public static final Set<String> NONE_AUDIT_FIELD = new HashSet<>();
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    static {
        NONE_AUDIT_FIELD.add("auditId");
        NONE_AUDIT_FIELD.add("auditSessionId");
        NONE_AUDIT_FIELD.add("auditTimestamp");

        NONE_AUDIT_FIELD.add("programId");
        NONE_AUDIT_FIELD.add("requestId");
        NONE_AUDIT_FIELD.add("objectVersionNumber");
        NONE_AUDIT_FIELD.add("createdBy");
        NONE_AUDIT_FIELD.add("creationDate");
        NONE_AUDIT_FIELD.add("lastUpdatedBy");
        NONE_AUDIT_FIELD.add("lastUpdateDate");

        NONE_AUDIT_FIELD.add("rowId");
    }

    private static final Map<String, String> PATTERN_MAP = new HashMap<>();

    public static String convertDateToString(Class clazz, String fieldName, Date date) {
        String key = clazz.getName() + "#" + fieldName;
        String pattern = PATTERN_MAP.get(key);
        if (pattern == null) {
            Field field = ReflectionUtils.findField(clazz, fieldName);
            pattern = DATE_FORMAT;
            if (field == null) {
                pattern = BaseConstants.DATE_TIME_FORMAT;
            } else {
                JsonFormat jf = field.getAnnotation(JsonFormat.class);
                if (jf != null && StringUtils.isNotEmpty(jf.pattern())) {
                    pattern = jf.pattern();
                }
            }
            PATTERN_MAP.put(key, pattern);
        }

        return DateFormatUtils.format(date, pattern, TimeZoneUtils.getTimeZone());
    }

    public static String convertDateToString(Date v, Field f) {
        return convertDateToString(f.getDeclaringClass(), f.getName(), v);
    }

    public static String camel(String str) {
        String[] ss = StringUtils.split(str, "_");
        for (int i = 1; i < ss.length; i++) {
            ss[i] = StringUtils.capitalize(ss[i]);
        }
        return StringUtils.join(ss);
    }

    public static String camel_(String str) {
        String s = camel(str);
        if (str.endsWith("_")) {
            return s + "_";
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> transform(List<Map<String, Object>> originalList, Class clazz) {
        List<Map<String, Object>> resultList = new ArrayList<>(originalList.size());
        // 在循环中,将当前行与前一行比较,pre代表前一行
        final Map<String, Object>[] pre = new Map[]{null};

        // 当前行的key可能会比pre的key要少(不存在值为null的key)
        // pre中存在,但在当前行不存在的key,都将作为不同点
        // 使用preKeys暂存pre的keySet,在比较中做减法,最后剩余的key也算做不同点
        final Set<String> preKeys = new HashSet<>();
        for (Map<String, Object> e : originalList) {
            Map<String, Object> n = new HashMap<>();
            List<String> changes = new ArrayList<>();
            e.forEach((k, v) -> {
                String key = AuditUtils.camel_(k);
                preKeys.remove(key); // 做减法
                if (v instanceof Date) {
                    v = AuditUtils.convertDateToString(clazz, key, (Date) v);
                }
                if (pre[0] != null && !AuditUtils.NONE_AUDIT_FIELD.contains(key)) {
                    Object vp = pre[0].get(key);
                    if (!CommonUtils.eq(v, vp)) {
                        changes.add(key);
                    }
                }
                n.put(key, v);
            });

            // 剩余的key,也是不同点
            changes.addAll(preKeys);
            if (pre[0] != null) {
                pre[0].put("__changes", changes);
            }
            resultList.add(n);
            pre[0] = n;
            preKeys.clear();
            preKeys.addAll(n.keySet());
        }
        return resultList;
    }

    public static List<Map<String, Object>> transform1(List<Map<String, Object>> resultPrev,
                                                       List<Map<String, Object>> resultThis, Class clazz, String code) {

        Map<String, Object> prevMap;
        final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> resuList = new ArrayList<>();
        Map<String, Object> thisMap;

        for (int i = 0; i < resultThis.size(); i++) {
            Map<String, Object> resuMap = new HashMap<String, Object>();
            int a = -1;
            List<String> changes = new ArrayList<>();
            thisMap = resultThis.get(i);
            for (int j = 0; j < resultPrev.size(); j++) {
                if (thisMap.get(code).equals(resultPrev.get(j).get(code))) {
                    a = j;
                }
            }
            if (a != -1) {
                prevMap = resultPrev.get(a);
                list.add(prevMap);
                thisMap.forEach((k, v) -> {
                    String key = AuditUtils.camel_(k);
                    if (v instanceof Date) {
                        v = AuditUtils.convertDateToString(clazz, key, (Date) v);
                    }
                    Object vp = list.get(0).get(k);
                    if (vp instanceof Date) {
                        vp = AuditUtils.convertDateToString(clazz, key, (Date) vp);
                    }
                    if (!AuditUtils.NONE_AUDIT_FIELD.contains(key)) {
                        if (!CommonUtils.eq(v, vp)) {
                            changes.add(key);
                        }
                    }
                    resuMap.put(key, v);
                });
                resuMap.put("__changes", changes);
                if (!changes.isEmpty()) {
                    resuList.add(resuMap);
                }
                list.remove(0);
            } else {
                thisMap.forEach((k, v) -> {
                    String key = AuditUtils.camel_(k);
                    if (v instanceof Date) {
                        v = AuditUtils.convertDateToString(clazz, key, (Date) v);
                    }
                    resuMap.put(key, v);
                });
                resuList.add(resuMap);
            }

        }
        return resuList;
    }


}
