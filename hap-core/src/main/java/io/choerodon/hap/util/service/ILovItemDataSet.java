package io.choerodon.hap.util.service;



import io.choerodon.hap.util.dto.LovItem;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * @author vista
 * @since 18-11-27 下午3:51
 */
public interface ILovItemDataSet extends IBaseService<LovItem> {
    /**
     * 根据lov编码查询lov子项列表.
     *
     * @param lovCode lov编码
     * @return lov子项列表
     */
    List<LovItem> selectByLovCode(String lovCode);
}
