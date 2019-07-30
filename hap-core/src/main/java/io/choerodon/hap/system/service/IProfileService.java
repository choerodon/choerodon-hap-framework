
package io.choerodon.hap.system.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.system.dto.Profile;
import io.choerodon.hap.system.dto.ProfileValue;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 配置文件Service.
 *
 * @author frank.li
 * @since 2016/6/9.
 */
public interface IProfileService extends IBaseService<Profile>, ProxySelf<IProfileService> {

    int LEVEL_USER = 30;
    int LEVEL_ROLE = 20;
    int LEVEL_GLOBAL = 10;

    /**
     * 分页查找配置文件值列表.
     *
     * @param value    配置文件值
     * @param page     当前页
     * @param pagesize 页大小
     * @return 配置文件值列表
     */
    List<ProfileValue> selectLevelValues(ProfileValue value, Long levelId, int page, int pagesize);

    /**
     * 查找配置文件列表.
     *
     * @param profile  配置文件
     * @param page     当前页
     * @param pagesize 页大小
     * @return 配置文件列表
     */
    List<Profile> selectProfiles(Profile profile, int page, int pagesize);

    /**
     * 查找配置文件值列表
     *
     * @param value 配置文件值
     * @return 配置文件值列表
     */
    List<ProfileValue> selectProfileValues(ProfileValue value);

    /**
     * 插入头行
     *
     * @param request IRequest
     * @param profile 配置文件
     * @return 配置文件
     */
    Profile createProfile(IRequest request, @StdWho Profile profile);

    /**
     * 批量删除头行.
     *
     * @param request  IRequest
     * @param Profiles 配置文件列表
     * @return 是否删除成功
     */
    boolean batchDelete(IRequest request, List<Profile> Profiles);

    /**
     * 批量删除配置文件值列表.
     *
     * @param requestContext IRequest
     * @param values         配置文件值列表
     * @return 是否删除成功
     */
    boolean batchDeleteValues(IRequest requestContext, List<ProfileValue> values);

    /**
     * 更新头行.
     *
     * @param requestContext IRequest
     * @param profile        配置文件
     * @return 配置文件
     */
    Profile updateProfile(IRequest requestContext, @StdWho Profile profile);

    /**
     * 批量更新头行.
     *
     * @param request  IRequest
     * @param Profiles 配置文件列表
     * @return 配置文件列表
     */
    List<Profile> batchUpdate(IRequest request, @StdWho List<Profile> Profiles);

    /**
     * 根据配置文件的名字/用户，查找用户在该配置文件下的值. 优先顺序 用户>角色>全局 若当前用户 在 用户、角色、全局三层 均没有值，返回 null
     *
     * @param userId      用戶Id
     * @param profileName 配置文件名字
     * @return 配置文件值
     */
    String getValueByUserIdAndName(Long userId, String profileName);

    /**
     * 根据request和profileName按优先级获取配置文件值.
     *
     * @param request     请求上下文
     * @param profileName 配置文件
     * @return 配置文件值
     */
    String getProfileValue(IRequest request, String profileName);
}
