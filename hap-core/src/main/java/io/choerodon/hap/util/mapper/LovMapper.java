package io.choerodon.hap.util.mapper;



import io.choerodon.hap.util.dto.Lov;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * lov mapper.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/2/1
 */
public interface LovMapper extends Mapper<Lov> {

    /**
     * 根据Lov代码查询lov.
     *
     * @param code 代码
     * @return Lov
     */
    Lov selectByCode(String code);

    /**
     * 条件查询lov.
     *
     * @param lov Lov
     * @return Lov列表
     */
    List<Lov> selectLovs(Lov lov);

}