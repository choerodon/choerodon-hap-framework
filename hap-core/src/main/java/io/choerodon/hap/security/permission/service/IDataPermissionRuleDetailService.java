package io.choerodon.hap.security.permission.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.security.permission.dto.DataPermissionRuleDetail;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/12/8
 */
public interface IDataPermissionRuleDetailService extends IBaseService<DataPermissionRuleDetail>, ProxySelf<IDataPermissionRuleDetailService> {

    /**
     * 选择规则明细.
     *
     * @param dto      查询参数
     * @param page     当前页数
     * @param pageSize 分页大小
     * @param request  IRequest环境
     * @return 查询当前规则下的规则明细
     * @throws IllegalAccessException 参数非法异常
     */
    List<DataPermissionRuleDetail> selectRuleManageDetail(DataPermissionRuleDetail dto, int page, int pageSize, IRequest request) throws IllegalAccessException;

    /**
     * 通过规则分配删除规则Detail.
     *
     * @param dataMaskRuleManageDetails 将要删除的RuleDetails
     */
    void removeDataMaskRuleDetailWithAssign(List<DataPermissionRuleDetail> dataMaskRuleManageDetails);

    /**
     * 删除规则Detail.
     *
     * @param dataMaskRuleManageDetails 删除规则并更新缓存
     */
    void removeDataMaskRuleDetail(List<DataPermissionRuleDetail> dataMaskRuleManageDetails);

    /**
     * 更新规则Detail.
     *
     * @param iRequest IRequest环境
     * @param dto      将要更新的规则明细
     * @return 更新过后的规则明细
     */
    List<DataPermissionRuleDetail> updateDataMaskRuleDetail(IRequest iRequest, List<DataPermissionRuleDetail> dto);

    void updateCache(Long ruleId);

    void deletePostFilter(Long detailId, Long ruleId);
}