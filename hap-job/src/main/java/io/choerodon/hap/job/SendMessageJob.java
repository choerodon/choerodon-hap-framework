/*
 * #{copyright}#
 */

package io.choerodon.hap.job;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.hap.mail.service.IEmailService;


/**
 * 发送消息,邮件的job.
 * 
 * @author Clerifen Li
 */
public class SendMessageJob extends AbstractJob {


    public static final String SUMMARY = "summary";

    private Logger logger = LoggerFactory.getLogger(SendMessageJob.class);

    @Autowired
    private IEmailService mailService;


    @Override
    public void safeExecute(JobExecutionContext context) throws Exception {
        try {
            String priority = context.getMergedJobDataMap().getString("priority");
            String type = context.getMergedJobDataMap().getString("type");
            String batchStr = context.getMergedJobDataMap().getString("batch");
            Integer batch = 20;
            try {
                batch = Integer.parseInt(batchStr);
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("batch not specified.");
                }
            }
            boolean vip = "VIP".equalsIgnoreCase(priority);
            if (logger.isDebugEnabled()) {
                logger.debug("begin send message,type:{}, priority:{}, batch:{}", type, priority, batch);
            }

            Map<String, Object> param = new HashMap<>();
            param.put("batch", batch);
            param.put("isVipQueue",vip);
       //     mailService.sendMessage(vip, param);
            mailService.sendMessages(param);
            setExecutionSummary((String) param.get(SUMMARY));
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
