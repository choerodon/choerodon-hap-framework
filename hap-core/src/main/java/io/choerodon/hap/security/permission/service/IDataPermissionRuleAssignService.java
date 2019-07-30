package io.choerodon.hap.security.permission.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleAssign;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/8
 */
public interface IDataPermissionRuleAssignService extends IBaseService<DataPermissionRuleAssign>, ProxySelf<IDataPermissionRuleAssignService> {


    /**
     * 选择规则分配.
     *
     * @param dto      查询参数
     * @param page     当前页数
     * @param pageSize 分页大小
     * @param request  IRequest环境
     * @return 查询当前detail所分配的规则
     * @throws IllegalAccessException 参数非法异常
     */
    List<DataPermissionRuleAssign> selectRuleAssign(DataPermissionRuleAssign dto, int page, int pageSize, IRequest request) throws IllegalAccessException;

    /**
     * 删除规则分配.
     *
     * @param dataMaskRuleAssigns 将要删除的RuleAssign
     */
    void removeDataMaskRuleAssign(List<DataPermissionRuleAssign> dataMaskRuleAssigns);

    /**
     * 更新规则分配.
     *
     * @param request             IRequest
     * @param dataMaskRuleAssigns 将要更新的RuleAssign
     * @return 更新的RuleAssign
     */
    List<DataPermissionRuleAssign> updateDataMaskRuleAssign(IRequest request, List<DataPermissionRuleAssign> dataMaskRuleAssigns);
}