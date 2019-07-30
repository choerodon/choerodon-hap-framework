package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.ProfileValue;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 配置维护行 Mapper
 *
 * @author Frank.li
 * @since 2016/6/9.
 */
public interface ProfileValueMapper extends Mapper<ProfileValue> {

    /**
     * 根据配置头ID删除配置行数据.
     *
     * @param key 头ID: profileId
     * @return 影响数量
     */
    int deleteByProfileId(ProfileValue key);

    /**
     * 查询用户配置维护值.
     *
     * @param value levelName: 用户名
     *              levelValue：用户ID
     * @return 配置维护值列表
     */
    List<ProfileValue> selectLevelValuesForUser(ProfileValue value);

    /**
     * 查询角色配置维护值.
     *
     * @param value levelName: 角色Code
     *              levelValue：角色ID
     * @return 配置维护值列表
     */
    List<ProfileValue> selectLevelValuesForRole(ProfileValue value);


    /**
     * 查询各层级ProfileValue.
     *
     * @param example 配置维护值
     * @return 配置维护值列表
     */
    List<ProfileValue> selectProfileValues(ProfileValue example);


    /**
     * 根据 profile id 和 user id 查询 该user id 在该配置文件下的所有值， 并按照 LEVEL_ID降序排列
     *
     * @param profileId 配置文件ID
     * @param userId    用户ID
     * @return 配置文件值List
     */
    List<ProfileValue> selectByProfileIdAndUserId(@Param("profileId") Long profileId, @Param("userId") Long userId);

    /**
     * 根据request和profileName按优先级获取配置值.
     *
     * @param profileName 配置名称
     * @return 按优先级获取配置文件值List
     */
    List<ProfileValue> selectPriorityValues(String profileName);
}