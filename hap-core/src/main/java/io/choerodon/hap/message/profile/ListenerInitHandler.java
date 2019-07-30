/*
 * #{copyright}#
 */

package io.choerodon.hap.message.profile;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface ListenerInitHandler {
    /**
     * 为 GlobalProfileListener 初始化加载 所关心的profile.
     * 
     * @param listener
     *            需要初始化的listener
     */
    void initLoad(GlobalProfileListener listener);
}