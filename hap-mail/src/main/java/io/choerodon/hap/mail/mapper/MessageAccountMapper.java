package io.choerodon.hap.mail.mapper;

import io.choerodon.hap.mail.dto.MessageAccount;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 消息账户Mapper.
 *
 * @author shengyang.zhou@hand-china.com
 */
public interface MessageAccountMapper extends Mapper<MessageAccount> {
    /**
     * 根据账户编码查询消息账户.
     *
     * @param accountCode 账户编码
     * @return 消息账户
     */
    MessageAccount selectByUniqueCode(String accountCode);

    /**
     * 条件查询消息账户.
     *
     * @param example 消息账户
     * @return 消息账户列表
     */
    List<MessageAccount> selectMessageAccountPassword(MessageAccount example);
}