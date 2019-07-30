/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.hap.mybatis.util;

import io.choerodon.hap.system.dto.DTOClassInfo;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.lang.StringUtils;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.IDynamicTableName;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * OGNL静态方法
 *
 * @author liuzh
 */
public abstract class OGNL {

    /**
     * 是否包含自定义查询列
     *
     * @param parameter
     * @return
     */
    public static boolean hasSelectColumns(Object parameter) {
        if (parameter != null && parameter instanceof Example) {
            Example example = (Example) parameter;
            if (example.getSelectColumns() != null && example.getSelectColumns().size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 不包含自定义查询列
     *
     * @param parameter
     * @return
     */
    public static boolean hasNoSelectColumns(Object parameter) {
        return !hasSelectColumns(parameter);
    }

    /**
     * 判断参数是否支持动态表名
     *
     * @param parameter
     * @return true支持，false不支持
     */
    public static boolean isDynamicParameter(Object parameter) {
        if (parameter != null && parameter instanceof IDynamicTableName) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否b支持动态表名
     *
     * @param parameter
     * @return true不支持，false支持
     */
    public static boolean isNotDynamicParameter(Object parameter) {
        return !isDynamicParameter(parameter);
    }

    private static final Pattern COL_PATTERN = Pattern.compile("[\\d\\w_]+");

    /**
     * FOR INTERNAL USE ONLY
     *
     * @param parameter
     * @return
     */
    public static String getOrderByClause(Object parameter) {
        if (parameter == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(64);
        if (parameter instanceof BaseDTO) {
            String sortName = ((BaseDTO) parameter).getSortname();
            EntityField[] ids = DTOClassInfo.getIdFields(parameter.getClass());
            if (StringUtil.isNotEmpty(sortName)) {
                if (!COL_PATTERN.matcher(sortName).matches()) {
                    throw new RuntimeException("Invalid sortname:" + sortName);
                }
                String order = ((BaseDTO) parameter).getSortorder();
                if (!("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order) || order == null)) {
                    throw new RuntimeException("Invalid sortorder:" + order);
                }
                String columnName = unCamel(sortName);

                sb.append(columnName).append(" ");
                sb.append(StringUtils.defaultIfEmpty(order, "ASC"));

                if (ids.length > 0 && !ids[0].getName().equals(sortName)) {
                    sb.append(",").append(DTOClassInfo.getColumnName(ids[0])).append(" ASC");
                }
            } else {
                if (ids.length > 0) {
                    sb.append(DTOClassInfo.getColumnName(ids[0])).append(" ASC");
                }
            }
        }
        return StringUtils.trimToNull(sb.toString());
    }

    /**
     * FOR INTERNAL USE ONLY
     *
     * @param parameter
     * @return
     */
    public static String getOrderByClause_TL(Object parameter) {
        if (parameter == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(64);
        if (parameter instanceof BaseDTO) {
            String sortName = ((BaseDTO) parameter).getSortname();
            EntityField[] ids = DTOClassInfo.getIdFields(parameter.getClass());
            if (StringUtil.isNotEmpty(sortName)) {
                if (!COL_PATTERN.matcher(sortName).matches()) {
                    throw new RuntimeException("Invalid sortname:" + sortName);
                }
                String order = ((BaseDTO) parameter).getSortorder();
                if (!("ASC".equalsIgnoreCase(order) || "DESC".equalsIgnoreCase(order) || order == null)) {
                    throw new RuntimeException("Invalid sortorder:" + order);
                }
                String columnName = unCamel(sortName);
                EntityField[] mlfs = DTOClassInfo.getMultiLanguageFields(parameter.getClass());
                for (EntityField f : mlfs) {
                    if (f.getName().equals(columnName)) {
                        if (f.getAnnotation(MultiLanguageField.class) == null) {
                            sb.append("b.");
                        } else {
                            sb.append("t.");
                        }
                        break;
                    }
                }

                sb.append(columnName).append(" ");
                sb.append(StringUtils.defaultIfEmpty(order, "ASC"));

                if (ids.length > 0 && !ids[0].getName().equals(sortName)) {
                    sb.append(",b.").append(DTOClassInfo.getColumnName(ids[0])).append(" ASC");
                }
            } else {
                if (ids.length > 0) {
                    sb.append("b.").append(DTOClassInfo.getColumnName(ids[0])).append(" ASC");
                }
            }
        }
        return StringUtils.trimToNull(sb.toString());
    }

    /**
     * test weather obj is null,empty string,empty collection,map,array
     *
     * @param obj
     * @return is obj equivalents to empty
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return ((String) obj).isEmpty();
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return ((Object[]) obj).length == 0;
        }
        return false;
    }

    public static String unCamel(String str) {
        return DTOClassInfo.camelToUnderLine(str);
    }

    public static String locale() {
        return RequestHelper.getCurrentRequest(true).getLocale();
    }
}
