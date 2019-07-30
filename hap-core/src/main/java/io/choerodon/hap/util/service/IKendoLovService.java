package io.choerodon.hap.util.service;

import java.util.Locale;

/**
 * 基于KendoUI的通用lov的service.
 *
 * @author njq.niu@hand-china.com
 */
public interface IKendoLovService {

    /**
     * 根据lov编码获取lov的配置.
     *
     * @param contextPath 系统上下文路径
     * @param locale      当前语言
     * @param lovCode     lov编码
     * @return lov配置
     */
    String getLov(String contextPath, Locale locale, String lovCode);

}
