package io.choerodon.hap.job.listener;

import io.choerodon.hap.mail.ReceiverTypeEnum;
import io.choerodon.hap.mail.dto.MessageReceiver;
import io.choerodon.hap.mail.service.IMessageService;
import io.choerodon.base.helper.ApplicationContextHelper;
import io.choerodon.base.util.BaseConstants;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shiliyan
 */
public class JobNoticeListener extends DefaultJobListener {

    private static final String VETOED = "Vetoed";

    private static final String EXECUTE_FAILED = "Execute Failed";

    private static final String EXECUTE_FINISH = "Execute Finish";

    private static final String STATUS2 = "status";

    private static final String SCHEDULED_FIRE_TIME = "scheduledFireTime";

    private static final String JOB_NAME = "jobName";

    private static final String JOB_INTERNAL_NOTIFICATION = "job_internal_notification";

    private static final String JOB_INTERNAL_EMAIL_ADDRESS = "job_internal_emailAddress";

    private static final String JOB_INTERNAL_EMAIL_ACCOUNT = "job_internal_email_account";

    private static final String JOB_INTERNAL_EMAIL_TEMPLATE = "job_internal_email_template";

    private static final String name = "JobNoticeListener";

    private IMessageService messageService;

    private String mailTemplate;

    public JobNoticeListener(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * JobListener Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /*
     * Object[] arguments = { new Integer(7), new
     * Date(System.currentTimeMillis()), "a disturbance in the Force" };
     * 
     * String result = MessageFormat.format( "At {1,time} on {1,date}, there was
     * {2} on planet {0,number,integer}.", arguments);
     */

    public String getName() {
        return name;
    }

    /**
     * @see org.quartz.JobListener#jobToBeExecuted(JobExecutionContext)
     */
    public void jobToBeExecuted(JobExecutionContext context) {
    }

    /**
     * @see org.quartz.JobListener#jobWasExecuted(JobExecutionContext,
     * JobExecutionException)
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (jobException != null) {
            sendMail(context, EXECUTE_FAILED);
        } else {
            sendMail(context, EXECUTE_FINISH);
        }
    }

    /**
     * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    public void jobExecutionVetoed(JobExecutionContext context) {
        sendMail(context, VETOED);
    }

    // job结束后发送邮件，
    private void sendMail(JobExecutionContext context, String status) {
        JobDetail jobDetail = context.getJobDetail();
        String messageAddress = (String) jobDetail.getJobDataMap().get(JOB_INTERNAL_EMAIL_ADDRESS);
        String isSend = (String) jobDetail.getJobDataMap().get(JOB_INTERNAL_NOTIFICATION);
        String templateCode = (String) jobDetail.getJobDataMap().get(JOB_INTERNAL_EMAIL_TEMPLATE);
        if (StringUtils.isNotEmpty(templateCode)) {
            mailTemplate = templateCode;
        }
        String accountCode = (String) jobDetail.getJobDataMap().get(JOB_INTERNAL_EMAIL_ACCOUNT);
        // String msg = "" + "您好，" + "计划任务" + "{}" + "于" + "{}" + "被" + "{}" +
        // "";

        if (Boolean.valueOf(isSend) && StringUtils.isNotBlank(messageAddress)) {

            Map<String, Object> templateData = new HashMap<>();

            String jobName = jobDetail.getKey().getName();

            Date scheduledFireTime = context.getScheduledFireTime();
            templateData.put(JOB_NAME, jobName);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BaseConstants.DATE_TIME_FORMAT);
            String format = simpleDateFormat.format(scheduledFireTime);
            templateData.put(SCHEDULED_FIRE_TIME, format);
            templateData.put(STATUS2, status);

            String[] addresses = StringUtils.split(messageAddress, ";");
            List<MessageReceiver> receivers = new ArrayList<>();
            for (String address : addresses) {
                MessageReceiver messageReceiver = new MessageReceiver();
                messageReceiver.setMessageAddress(address);
                messageReceiver.setMessageType(ReceiverTypeEnum.NORMAL.getCode());
                receivers.add(messageReceiver);
            }
            try {
                ApplicationContext wac = ApplicationContextHelper.getApplicationContext();
                messageService = (IMessageService) wac.getBean("messageServiceImpl");
                if (StringUtils.isNotEmpty(accountCode)) {
                    messageService.addEmailMessage(null, accountCode, this.mailTemplate, templateData, null, receivers);
                } else {
                    messageService.sendMessage(null, this.mailTemplate, templateData, receivers, null);
                }
            } catch (Exception e) {
                if (getLog().isErrorEnabled()) {
                    getLog().error(e.getMessage(), e);
                }
            }
        }
    }

}
