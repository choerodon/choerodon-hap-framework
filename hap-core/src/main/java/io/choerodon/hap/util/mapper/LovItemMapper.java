package io.choerodon.hap.util.mapper;



import io.choerodon.hap.util.dto.LovItem;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * lov子项 mapper.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/2/1
 */
public interface LovItemMapper extends Mapper<LovItem> {

    /**
     * 根据lovId查询lov子项列表.
     *
     * @param lovId lovId
     * @return lov子项列表
     */
    List<LovItem> selectByLovId(Long lovId);

    /**
     * 根据lovId删除lov子项.
     *
     * @param lovId lovId
     * @return int
     */
    int deleteByLovId(Long lovId);

    /**
     * 根据lov编码查询lov子项列表.
     *
     * @param lovCode lov编码
     * @return lov子项列表
     */
    List<LovItem> selectByLovCode(String lovCode);
}