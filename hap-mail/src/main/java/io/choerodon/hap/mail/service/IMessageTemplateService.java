package io.choerodon.hap.mail.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.mail.dto.MessageTemplate;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 消息模板服务接口.
 *
 * @author Clerifen Li
 */
public interface IMessageTemplateService extends IBaseService<MessageTemplate>, ProxySelf<IMessageTemplateService> {
    /**
     * 新建消息模板.
     *
     * @param request IRequest
     * @param obj     消息模板
     * @return 消息模板
     */
    MessageTemplate createMessageTemplate(IRequest request, @StdWho MessageTemplate obj);

    /**
     * 更新消息模板.
     *
     * @param request IRequest
     * @param obj     消息模板
     * @return 消息模板
     */
    MessageTemplate updateMessageTemplate(IRequest request, @StdWho MessageTemplate obj);

    /**
     * 根据模板Id查询消息模板.
     *
     * @param request IRequest
     * @param objId   模板Id
     * @return 消息模板
     */
    MessageTemplate selectMessageTemplateById(IRequest request, Long objId);

    /**
     * 条件查询消息模板.
     *
     * @param request         IRequest
     * @param messageTemplate 消息模板
     * @param page            页码
     * @param pageSize        页数
     * @return 消息模板列表
     */
    List<MessageTemplate> selectMessageTemplates(IRequest request, MessageTemplate messageTemplate, int page, int pageSize);

    /**
     * 删除单个消息模板.
     *
     * @param request  IRequest
     * @param template 消息模板
     * @return int
     */
    int deleteMessageTemplate(IRequest request, MessageTemplate template);

    /**
     * 批量删除消息模板.
     *
     * @param request      IRequest
     * @param templateList 消息模板 列表
     * @return int
     */
    int batchDelete(IRequest request, List<MessageTemplate> templateList);

}
