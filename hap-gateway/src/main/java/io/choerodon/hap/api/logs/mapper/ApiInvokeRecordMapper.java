package io.choerodon.hap.api.logs.mapper;

import io.choerodon.hap.api.logs.dto.ApiInvokeRecord;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ApiInvokeRecordMapper.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/23.
 */
public interface ApiInvokeRecordMapper extends Mapper<ApiInvokeRecord> {

    /**
     * 通过Id获取记录，记录详情.
     *
     * @param recordId 调用记录Id
     * @return 调用记录列表
     */
    List<ApiInvokeRecord> selectById(@Param(value = "recordId") Long recordId);

    /**
     * 查询调用记录列表.
     *
     * @param apiInvokeRecord 调用记录
     * @return 调用记录列表
     */
    List<ApiInvokeRecord> selectList(ApiInvokeRecord apiInvokeRecord);

}