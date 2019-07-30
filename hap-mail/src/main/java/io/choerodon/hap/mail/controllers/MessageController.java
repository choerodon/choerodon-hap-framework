package io.choerodon.hap.mail.controllers;

import io.choerodon.hap.mail.ReceiverTypeEnum;
import io.choerodon.hap.mail.dto.Message;
import io.choerodon.hap.mail.dto.MessageReceiver;
import io.choerodon.hap.mail.dto.MessageTransaction;
import io.choerodon.hap.mail.mapper.MessageReceiverMapper;
import io.choerodon.hap.mail.mapper.MessageTransactionMapper;
import io.choerodon.hap.mail.service.IMessageService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对消息的操作.
 *
 * @author xiawang.liu@hand-china.com
 */

@RestController
@RequestMapping(value = {"/sys/message", "/api/sys/message"})
public class MessageController extends BaseController {

    @Autowired
    private IMessageService messageService;
    @Autowired
    private MessageTransactionMapper messageTransactionMapper;
    @Autowired
    private MessageReceiverMapper messageReceiverMapper;

    /**
     * 查询消息.
     *
     * @param request  HttpServletRequest
     * @param message  Message
     * @param page     page
     * @param pagesize pagesize
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData getMessageBySubject(HttpServletRequest request, Message message,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(messageService.selectMessages(requestContext, message, page, pagesize));
    }

    /**
     * 查询消息地址.
     *
     * @param request         HttpServletRequest
     * @param messageReceiver MessageReceiver
     * @param page            page
     * @param pagesize        pagesize
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryMessageAddresses")
    public ResponseData getMessageAddressesByMessageId(HttpServletRequest request, MessageReceiver messageReceiver,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(
                messageService.selectMessageAddressesByMessageId(requestContext, messageReceiver, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sendTest")
    public ResponseData sendTestMessage(HttpServletRequest request, @RequestBody Map<String, Object> param) throws
            Exception {
        IRequest iRequest = createRequestContext(request);
        String str = param.get("receivers").toString();
        String[] receivers = StringUtils.split(str, ";");
        List<MessageReceiver> receiverList = new ArrayList<>();
        for (String r : receivers) {
            MessageReceiver mr = new MessageReceiver();
            mr.setMessageAddress(r);
            mr.setMessageType(ReceiverTypeEnum.NORMAL.getCode());
            receiverList.add(mr);
        }

        List<Integer> attachments = (List<Integer>) param.get("attachments");
        List<Long> attachment = null;
        if (null != attachments) {
            attachment = new ArrayList<>();
            for (Integer s : attachments) {
                attachment.add(Long.valueOf(s));
            }
        }
        if ("template".equals(param.get("mode"))) {
            //兼容以前的模板
            if (param.get("accountCode") != null && StringUtils.isNotEmpty(param.get("accountCode").toString())) {
                messageService.addEmailMessage(iRequest.getUserId(), param.get("accountCode").toString(), param.get("templateCode").toString(), null, attachment, receiverList);
            } else {
                messageService.sendMessage(iRequest, param.get("templateCode").toString(), null, receiverList, attachment);
            }
        }
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/error_mess")
    public String errorMess(Long messageId) {
        MessageTransaction messageTransaction = new MessageTransaction();
        messageTransaction.setMessageId(messageId);
        List<MessageTransaction> mess = messageTransactionMapper.select(messageTransaction);
        return mess.get(mess.size() - 1).getTransactionMessage();
    }

    @PostMapping(value = "/messageContent")
    public String messageContent(HttpServletRequest request, Long messageId) {
        IRequest iRequest = createRequestContext(request);
        Message message = new Message();
        message.setMessageId(messageId);
        Message msg = messageService.selectMessageContent(iRequest, message);
        return msg.getContent();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/messageReceiver")
    public ResponseData messageReceiver(HttpServletRequest request, Long messageId) {
        return new ResponseData(messageReceiverMapper.selectByMessageId(messageId));
    }
}