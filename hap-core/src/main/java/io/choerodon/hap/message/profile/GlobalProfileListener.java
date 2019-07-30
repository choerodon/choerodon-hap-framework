/*
 * #{copyright}#
 */

package io.choerodon.hap.message.profile;

import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface GlobalProfileListener {

    /**
     * 获取 需要监听的 profile name 列表.
     * 
     * @return 不应该返回null.
     */
    List<String> getAcceptedProfiles();

    /**
     * profile 更新 通知.
     * <p>
     * 
     * @param profileName
     *            profileName 的值 是 {@link #getAcceptedProfiles()} 中的一个
     * @param profileValue
     *            新的值
     */
    void updateProfile(String profileName, String profileValue);

}
