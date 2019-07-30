package io.choerodon.hap.fnd.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.fnd.dto.Company;
import io.choerodon.mybatis.service.IBaseService;

/**
 * 公司服务接口.
 *
 * @author jialong.zuo@hand-china.com
 * @since 2016/10/9.
 */
public interface ICompanyService extends IBaseService<Company>, ProxySelf<ICompanyService> {
}
