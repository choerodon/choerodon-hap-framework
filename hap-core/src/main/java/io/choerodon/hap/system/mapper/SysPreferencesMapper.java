/*
 * #{copyright}#
 */
package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.SysPreferences;
import io.choerodon.mybatis.common.Mapper;

/**
 * 系统首选项Mapper.
 * 
 * @author zhangYang
 * @author njq.niu@hand-china.com
 */
public interface SysPreferencesMapper extends Mapper<SysPreferences> {

    /**
     * 查询用户单个首选项
     * @param record preferences值
     * @return 首选项
     */
    SysPreferences selectUserPreference(SysPreferences record);

    /**
     * 根据preferences或userId更新SysPreferences
     * @param record record
     * @return
     */
    int updatePreferLine(SysPreferences record);
}