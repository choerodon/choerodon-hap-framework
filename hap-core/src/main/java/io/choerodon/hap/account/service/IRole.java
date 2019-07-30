package io.choerodon.hap.account.service;

/**
 * 角色扩展接口.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface IRole {

    /**
     * 获取角色ID.
     *
     * @return 角色ID
     */
    Long getRoleId();

    /**
     * 获取角色代码.
     *
     * @return 角色代码
     */
    String getRoleCode();

    /**
     * 获取角色名称.
     *
     * @return 角色名称
     */
    String getRoleName();

    /**
     * 获取启用标记.
     *
     * @return 启用标记
     */
    boolean isEnabled();

    /**
     * 是否有效.
     *
     * @return 是否有效
     */
    boolean isActive();
}
