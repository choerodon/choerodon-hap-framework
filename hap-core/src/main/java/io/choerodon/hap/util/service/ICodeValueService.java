package io.choerodon.hap.util.service;


import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:59 2018/11/5
 * Description:
 */
public interface ICodeValueService extends IBaseService<CodeValue> {
    /**
     * 根据快码查询快码值列表.
     *
     * @param code 快码
     * @return 快码值列表
     */
    List<CodeValue> selectCodeValueByCode(Code code);

}
