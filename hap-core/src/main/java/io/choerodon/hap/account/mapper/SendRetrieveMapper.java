package io.choerodon.hap.account.mapper;

import io.choerodon.hap.account.dto.SendRetrieve;
import io.choerodon.mybatis.common.Mapper;

/**
 * 发送次数限制接口.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface SendRetrieveMapper extends Mapper<SendRetrieve> {

    int insertRecord(SendRetrieve record);

    int query(SendRetrieve sendRetrieve);
}