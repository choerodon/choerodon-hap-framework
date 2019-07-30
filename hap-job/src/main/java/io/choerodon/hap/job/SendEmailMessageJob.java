package io.choerodon.hap.job;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.mail.dto.Message;
import io.choerodon.hap.mail.mapper.MessageMapper;
import io.choerodon.hap.mail.service.IEmailService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 发送邮件的job.
 *
 * @author qiang.zeng@hand-china.com
 */
public class SendEmailMessageJob extends AbstractJob {


    public static final String SUMMARY = "summary";

    private Logger logger = LoggerFactory.getLogger(SendEmailMessageJob.class);

    @Autowired
    private IEmailService mailService;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {
        try {
            Map<String, Object> param = new HashMap<>();
            PageHelper.startPage(1, 20);
            List<Message> userEmailToSend = messageMapper.selectEmailSendByJob();
            if(CollectionUtils.isNotEmpty(userEmailToSend)){
                mailService.sendEmailMessage(userEmailToSend,param);
                setExecutionSummary((String) param.get(SUMMARY));
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        return false;
    }
}
