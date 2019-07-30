package io.choerodon.hap.security.permission.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.security.permission.dto.DataPermissionTableRule;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/8
 */
public interface IDataPermissionTableRuleService extends IBaseService<DataPermissionTableRule>, ProxySelf<IDataPermissionTableRuleService> {

    /**
     * 删除表规则.
     *
     * @param list 将要删除的规则
     */
    void removeRule(List<DataPermissionTableRule> list);

    /**
     * 更新分配表规则.l
     *
     * @param request IRequest环境
     * @param list    将要更新的规则
     * @return 更新过后的规则
     */
    List<DataPermissionTableRule> updateRule(IRequest request, List<DataPermissionTableRule> list);
}