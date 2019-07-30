/*
 * #{copyright}#
 */

package io.choerodon.hap.job;

import java.util.Date;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shengyang.zhou@hand-china.com
 */
@DisallowConcurrentExecution
public class HelloWorldJob extends AbstractJob {

    private static Logger log = LoggerFactory.getLogger(HelloWorldJob.class);

    @Override
    public void safeExecute(JobExecutionContext context) {

        JobDetail detail = context.getJobDetail();
        JobKey key = detail.getKey();
        TriggerKey triggerKey = context.getTrigger().getKey();
        String msg = "############# Hello World! - . jobKey:" + key + ", triggerKey:" + triggerKey + ", execTime:" + new Date();
        if (log.isInfoEnabled()) {
            log.info(msg);
        }
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("#############");
    }

    @Override
    public boolean isRefireImmediatelyWhenException() {
        return false;
    }

}