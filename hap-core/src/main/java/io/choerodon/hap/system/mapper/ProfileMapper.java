package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.Profile;
import io.choerodon.mybatis.common.Mapper;

/**
 * 配置维护Mapper
 *
 * @author Frank.li
 * @since 2016/6/9.
 */
public interface ProfileMapper extends Mapper<Profile> {

    /**
     * 查询配置
     *
     * @param profileName 配置名称
     * @return 配置
     */
    Profile selectByName(String profileName);
}