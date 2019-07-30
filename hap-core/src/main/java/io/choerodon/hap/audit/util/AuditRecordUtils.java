package io.choerodon.hap.audit.util;

import io.choerodon.hap.system.dto.DTOClassInfo;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.web.core.impl.RequestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 审计记录工具类.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/9/11.
 */
public class AuditRecordUtils {

    private final static Map<String, String> AUDIT_TYPE = new HashMap<>();

    static {
        AUDIT_TYPE.put("INSERT", "添加");
        AUDIT_TYPE.put("UPDATE", "修改");
        AUDIT_TYPE.put("DELETE", "删除");
    }

    /**
     * 获取DTO最新快照.
     *
     * @param auditList 审计记录
     * @return 审计记录
     */
    public static List<Map<String, Object>> operateAuditRecord(List<Map<String, Object>> auditList) {

        // 提取最新的记录，并将KEY转为驼峰式
        for (int i = 0, j = auditList.size(); i < j; i++) {
            Map<String, Object> map = auditList.get(i);
            Map<String, Object> tmp = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                // key转驼峰
                String key = DTOClassInfo.underLineToCamel(entry.getKey());
                if (null != entry.getValue()) {
                    tmp.put(key, entry.getValue().toString());
                }
            }
            auditList.remove(i);
            auditList.add(i, tmp);
        }
        return auditList;
    }

    /**
     * 单语言审计数据处理.
     *
     * @param list 审计数据
     * @return 审计数据处理后的数据
     */
    public static List<Map<String, Object>> operateAuditRecordSingleDetail(List<Map<String, Object>> list) {
        Collections.reverse(list);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = null;
        for (int i = 0, j = list.size(); i < j; i++) {
            Map<String, Object> map = list.get(i);
            int diffNum = 0;
            if (i == 0) {
                resultMap = new HashMap<>();
                for (Map.Entry<String, Object> audit : map.entrySet()) {
                    String key = audit.getKey();
                    String value = null;
                    if (null != audit.getValue()) {
                        value = audit.getValue().toString();
                    } else {
                        continue;
                    }
                    if ("AUDIT_TRANSACTION_TYPE".equals(key)) {
                        value = AUDIT_TYPE.get(value);
                    }
                    resultMap.put(DTOClassInfo.underLineToCamel(key), value);
                }
                resultList.add(resultMap);
            } else { // 其他数据 update or delete
                resultMap = new HashMap<>();
                for (Map.Entry<String, Object> audit : map.entrySet()) {
                    String key = audit.getKey();
                    String value = singleLanguageConpare(list.get(i - 1), map, key);
                    diffNum = value.endsWith("&") ? diffNum + 1 : diffNum;
                    resultMap.put(DTOClassInfo.underLineToCamel(key), value);
                }
                String type = map.get("AUDIT_TRANSACTION_TYPE").toString();
                if (diffNum > 0 || "DELETE".equals(type)) {
                    resultList.add(resultMap);
                }
            }
        }
        for (int i = 0, j = resultList.size(); i < j; i++) {
            list.remove(i);
            list.add(i, resultList.get(i));
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * 多语言审计数据处理.
     *
     * @param auditList 审计数据
     * @param languages 系统支持的语言集合
     * @return 处理后的审计数据
     */
    public static List<Map<String, Map<String, String>>> operateAuditRecordMultiDetail(List<Map<String, Object>> auditList,
                                                                                       List<Language> languages) {

        // 当前语言
        final String CURRENT_LANGUAGE = RequestHelper.getCurrentRequest().getLocale();
        // 删除审计记录语言为null  设为当前语言
        for (Map<String, Object> map : auditList) {
            if (null == map.get("LANG")) {
                map.put("LANG", CURRENT_LANGUAGE);
            }
        }
        // 按语言分组
        Map<String, List<Map<String, Object>>> groupByLanguageMap =
                auditList.stream().collect(Collectors.groupingBy(
                        (map) -> map.get("LANG").toString()
                ));
        List<Map<String, Object>> currentLanguageAuditList = groupByLanguageMap.get(CURRENT_LANGUAGE);
        // 获取多语言数据
        Map<String, List<Map<String, Object>>> multiLanguageAuditMap = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Object>>> map : groupByLanguageMap.entrySet()) {
            if (!map.getKey().equals(CURRENT_LANGUAGE)) {
                multiLanguageAuditMap.put(map.getKey(), map.getValue());
            }
        }

        // 比较当前语言的记录不同
        List<Map<String, Object>> currentLanguageList = languageDataDeal(currentLanguageAuditList);

        Map<String, List<Map<String, Object>>> multiLanguage = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Object>>> otherMap : multiLanguageAuditMap.entrySet()) {
            List<Map<String, Object>> multiItem = new ArrayList<>();
            for (int i = 0, j = currentLanguageAuditList.size(); i < j; i++) {
                Map<String, Object> map = currentLanguageAuditList.get(i);
                // 该版本的其他语言审计记录
                String sessionId = map.get("AUDIT_SESSION_ID").toString();
                // 获取对应语言的记录
                Map<String, Object> itemList = otherMap.getValue().stream().filter((m) -> {
                    String id = m.get("AUDIT_SESSION_ID").toString();
                    return sessionId.equals(id);
                }).collect(Collectors.toList()).get(0);
                multiItem.add(itemList);
            }
            multiLanguage.put(otherMap.getKey(), multiItem);
        }

        // 多语言语言记录 比较不同
        Map<String, List<Map<String, Object>>> multiLanguageMap = new HashMap<>();
        List<Map<String, Object>> multiLanguageList = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, Object>>> multiMap : multiLanguage.entrySet()) {
            List<Map<String, Object>> list = multiMap.getValue();
            Collections.reverse(list);
            multiLanguageList = languageDataDeal(list);
            multiLanguageMap.put(multiMap.getKey(), multiLanguageList);
        }

        // 当前语言与多语言合并
        List<Map<String, Map<String, String>>> resultList = new ArrayList<>();
        for (int i = 0, j = currentLanguageList.size(); i < j; i++) {
            Map<String, Object> current = currentLanguageList.get(i);
            Map<String, Map<String, String>> maps = new HashMap<>();
            for (Map.Entry<String, Object> map : current.entrySet()) {
                String key = map.getKey();
                Map<String, String> item = new HashMap<>();
                for (Map.Entry<String, List<Map<String, Object>>> multiMap : multiLanguageMap.entrySet()) {
                    List<Map<String, Object>> list = multiMap.getValue();
                    String language = multiMap.getKey();
                    Map<String, Object> otherMap = list.get(i);
                    // 当前语言没有修改而其他语言修改
                    String str = null;
                    if (null != otherMap.get(key)) {
                        str = otherMap.get(key).toString();
                    } else {
                        continue;
                    }
                    if (!current.get(key).toString().endsWith("&") && otherMap.get(key).toString().endsWith("&")) {
                        current.put(key, current.get(key).toString() + "&");
                        str.substring(0, str.length() - 1);
                    }
                    item.put(getLanguageDescription(languages, language), str);
                }
                item.put(getLanguageDescription(languages, CURRENT_LANGUAGE) + "&", current.get(key).toString());
                maps.put(key, item);
            }
            resultList.add(maps);
        }
        Collections.reverse(resultList);
        return resultList;
    }

    /**
     * 获取语言描述
     *
     * @param languages 语言列表
     * @param key       语言key
     * @return 语言描述
     */
    private static String getLanguageDescription(List<Language> languages, String key) {
        for (Language language : languages) {
            if (language.getLangCode().equals(key)) {
                return language.getDescription();
            }
        }
        return "";
    }

    /**
     * 比较当前语言的记录不同，处理并获得当前语言对应多语言.
     *
     * @param compareList 当前语言审计记录集合
     * @return 比较后的审计记录集合
     */
    private static List<Map<String, Object>> languageDataDeal(List<Map<String, Object>> compareList) {
        Collections.reverse(compareList);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0, j = compareList.size(); i < j; i++) {
            Map<String, Object> map = compareList.get(i);
            if (i == 0) {
                Map<String, Object> tmp = new HashMap<>();
                for (Map.Entry<String, Object> item : map.entrySet()) {
                    String key = item.getKey();
                    String value = null;
                    if (null != item.getValue()) {
                        value = item.getValue().toString();
                    } else {
                        continue;
                    }
                    if ("AUDIT_TRANSACTION_TYPE".equals(key)) {
                        value = AUDIT_TYPE.get(item.getValue());
                    }
                    tmp.put(DTOClassInfo.underLineToCamel(key), value);
                }
                resultList.add(tmp);
            } else {
                // 当前语言记录 比较不同
                resultList.add(multiLanguageCompare(map, compareList, i));
            }
        }
        return resultList;
    }

    /**
     * 多语言 比较不同.
     *
     * @param map                      一条审计记录
     * @param currentLanguageAuditList 审计记录集合
     * @param i                        当前记录位置索引
     * @return
     */
    private static Map<String, Object> multiLanguageCompare(Map<String, Object> map,
                                                            List<Map<String, Object>> currentLanguageAuditList,
                                                            int i) {
        Map<String, Object> resultMap = null;
        int diffNum = 0;
        Map<String, Object> result = new HashMap<>();
        Object nowValue = null;
        Object beforeValue = null;
        resultMap = new HashMap<>();
        String str = "";

        for (Map.Entry<String, Object> item : map.entrySet()) {
            String key = item.getKey();
            nowValue = map.get(key);
            beforeValue = currentLanguageAuditList.get(i - 1).get(key);
            if ("LAST_UPDATED_BY".equals(key)
                    || "AUDIT_ID".equals(key)
                    || "OBJECT_VERSION_NUMBER".equals(key)
                    || "AUDIT_TIMESTAMP".equals(key)
                    || "AUDIT_TIMESTAMP".equals(key)) {
                str = nowValue.toString();
            } else if ("AUDIT_TRANSACTION_TYPE".equals(key)) {
                str = AUDIT_TYPE.get(nowValue);
            } else if (null != nowValue) {
                if (null != beforeValue) {
                    diffNum++;
                    str = nowValue.equals(beforeValue) ? nowValue.toString() : nowValue.toString() + "&";
                } else {
                    diffNum++;
                    str = nowValue.toString() + "&";
                }
            }
            resultMap.put(DTOClassInfo.underLineToCamel(key), str);
        }

        String type = map.get("AUDIT_TRANSACTION_TYPE").toString();
        if (diffNum > 0 || "DELETE".equals(type)) {
            result.putAll(resultMap);
        }
        return result;
    }

    /**
     * 单语言比较数据.
     *
     * @param before 前一条数据
     * @param now    后一条数据
     * @param key    字段名称 根据key得到value进行比较
     * @return 返回字符串
     */
    private static String singleLanguageConpare(Map<String, Object> before, Map<String, Object> now, String key) {
        Object beforeValue = before.get(key);
        Object nowValue = now.get(key);
        if ("AUDIT_ID".equals(key) || "AUDIT_TIMESTAMP".equals(key)) {
            return nowValue.toString();
        }
        if ("AUDIT_TRANSACTION_TYPE".equals(key)) {
            return AUDIT_TYPE.get(nowValue);
        }
        if (null == nowValue) {
            return "";
        } else {
            if (null != beforeValue) {
                return nowValue.equals(beforeValue) ? nowValue.toString() : nowValue.toString() + "&";
            } else {
                return nowValue.toString() + "&";
            }
        }
    }
}
