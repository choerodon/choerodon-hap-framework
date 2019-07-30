package io.choerodon.hap.mail.mapper;

import io.choerodon.hap.mail.dto.MessageEmailAccount;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 邮件账户Mapper.
 *
 * @author qiang.zeng@hand-china.com
 */
public interface MessageEmailAccountMapper extends Mapper<MessageEmailAccount> {
    /**
     * 根据邮件配置Id删除邮件账户.
     *
     * @param configId 邮件配置Id
     * @return int
     */
    int deleteByConfigId(Long configId);

    /**
     * 条件查询邮件账户.
     *
     * @param record 邮件账户
     * @return 邮件账户列表
     */
    List<MessageEmailAccount> selectMessageEmailAccounts(MessageEmailAccount record);

    /**
     * 条件查询邮件账户密码.
     *
     * @param record 邮件账户
     * @return 邮件账户列表
     */
    List<MessageEmailAccount> selectMessageEmailAccountPassword(MessageEmailAccount record);

    /**
     * 根据账户编码查询邮件账户.
     *
     * @param accountCode 账户编码
     * @return 邮件账户
     */
    MessageEmailAccount selectByAccountCode(@Param("accountCode") String accountCode);

    /**
     * 根据账户Id和账户编码查询邮件账户.
     *
     * @param accountId   账户Id
     * @param accountCode 账户编码
     * @return 邮件账户
     */
    MessageEmailAccount getMsgEmailAccountByCode(@Param("accountId") Long accountId,
                                                 @Param("accountCode") String accountCode);

    /**
     * 条件查询邮件账户.
     *
     * @param record 邮件账户
     * @return 邮件账户
     */
    MessageEmailAccount selectMessageEmailAccount(MessageEmailAccount record);

    /**
     * 查询邮件账户所在邮件配置状态.
     *
     * @param record 邮件账户
     * @return 邮件配置状态
     */
    String selectMessageEmailConfig(MessageEmailAccount record);

    /**
     * 根据邮件配置Id查询邮件账户
     *
     * @param configId 邮件配置Id
     * @return 邮件账户列表
     */
    List<MessageEmailAccount> selectByConfigId(Long configId);
}