package io.choerodon.hap.util.mapper;



import io.choerodon.hap.util.dto.Code;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 快速编码Mapper.
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9.
 */
public interface CodeMapper extends Mapper<Code> {

    /**
     * 查询快码列表.
     *
     * @param code 编码
     * @return 快码列表
     */
    List<Code> selectCodes(Code code);

    /**
     * 根据快码代码获取快码.
     *
     * @param codeName 快码代码
     * @return 快码
     */
    Code getByCodeName(String codeName);

}