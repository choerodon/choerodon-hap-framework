package io.choerodon.hap.system.service;


import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.system.dto.SysConfig;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 系统配置服务接口.
 *
 * @author hailin.xu@hand-china.com
 */
public interface ISysConfigService extends IBaseService<SysConfig>, ProxySelf<ISysConfigService> {
    /**
     * 根据配置代码获取配置值.
     *
     * @param configCode 配置代码
     * @return 配置值
     */

    String getConfigValue(String configCode);

    /**
     * 更新系统图片时间戳.
     */
    String updateSystemImageVersion(String type);

    /**
     * 根据配置代码获取配置信息.
     *
     * @param configCode 配置代码
     * @return 配置信息
     */
    SysConfig selectByCode(String configCode);

}
