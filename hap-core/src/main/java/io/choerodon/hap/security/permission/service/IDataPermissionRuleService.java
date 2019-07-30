package io.choerodon.hap.security.permission.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.security.permission.dto.DataPermissionRule;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/8
 */
public interface IDataPermissionRuleService extends IBaseService<DataPermissionRule>, ProxySelf<IDataPermissionRuleService> {

    /**
     * 删除规则并发送消息.
     *
     * @param dataPermissionRules 将要删除的规则
     */
    void removeRule(List<DataPermissionRule> dataPermissionRules);

    /**
     * 通过ruleDtail删除规则.
     *
     * @param dataPermissionRules 将要删除的规则
     */
    void removeRuleWithDetail(List<DataPermissionRule> dataPermissionRules);

    /**
     * 选择未被选择的规则.
     *
     * @param dataPermissionRule 查询参数
     * @param iRequest           IRequest环境
     * @param page               当前页数
     * @param pageSize           分页大小
     * @return 返回未被选中的屏蔽规则
     */
    List<DataPermissionRule> selectRuleWithoutTableSelect(DataPermissionRule dataPermissionRule, IRequest iRequest, int page, int pageSize);
}