/*
 * #{copyright}#
 */

package io.choerodon.hap.core.impl;

import io.choerodon.hap.core.ITlTableNameProvider;

/**
 * 去除_B 后缀,添加_TL 后缀.
 * <p>
 * 例如:SYS_ROLE_B --&gt; SYS_ROLE_TL
 *
 * @author shengyang.zhou@hand-china.com
 */
public final class DefaultTlTableNameProvider implements ITlTableNameProvider {
    private static DefaultTlTableNameProvider ourInstance = new DefaultTlTableNameProvider();
    private static final String MULTI_TABLE_SUFFIX = "_TL";
    private static final String BASE_TABLE_SUFFIX = "_B";

    public static DefaultTlTableNameProvider getInstance() {
        return ourInstance;
    }

    private DefaultTlTableNameProvider() {
    }

    @Override
    public String getTlTableName(String baseTableName) {
        int len = baseTableName.length();
        String tlTableName;
        if (baseTableName.toUpperCase().endsWith(BASE_TABLE_SUFFIX)) {
            tlTableName = new StringBuilder(baseTableName).delete(len - 2, len).append(MULTI_TABLE_SUFFIX).toString();
        } else {
            tlTableName = baseTableName + MULTI_TABLE_SUFFIX;
        }
        return tlTableName;
    }
}
