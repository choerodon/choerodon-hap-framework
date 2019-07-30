/*
 * #{copyright}#
 */
package io.choerodon.hap.job.plugin;

import io.choerodon.hap.job.listener.SchedulerRunningListener;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.EverythingMatcher;
import org.quartz.spi.ClassLoadHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.hap.job.listener.JobNoticeListener;
import io.choerodon.hap.job.listener.JobRunningListener;
import org.springframework.context.ApplicationContext;

/**
 * @author shiliyan
 *
 */
public class RunningListenerPlugin extends DefaultSchedulerPlugin {

    // private static final String Running = "Install Job Running Listener.";
    //
    // private static final String Scheduler = "Install Scheduler Running
    // Listener.";
    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * Data members.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean isLogRunningInfo = false;

    private String mailTemplate;

    private ApplicationContext applicationContext;

    private Scheduler scheduler;

    protected Logger getLog() {
        return log;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * 
     * SchedulerPlugin Interface.
     * 
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * <p>
     * Called during creation of the <code>Scheduler</code> in order to give the
     * <code>SchedulerPlugin</code> a chance to initialize.
     * </p>
     * 
     * @throws SchedulerConfigException
     *             if there is an error initializing.
     */
    @Override
    public void initialize(String pname, Scheduler scheduler, ClassLoadHelper classLoadHelper)
            throws SchedulerException {
        this.scheduler = scheduler;

    }

    public void start() {
        try {
            applicationContext = (ApplicationContext) scheduler.getContext().get("applicationContext");
            // according to applicationContext-job.xml
            ListenerManager listenerManager = scheduler.getListenerManager();
            if (isLogRunningInfo()) {
                listenerManager.addJobListener(new JobRunningListener(applicationContext), EverythingMatcher.allJobs());
                listenerManager.addSchedulerListener(new SchedulerRunningListener(applicationContext));
            }

            if (mailTemplate != null && !"".equals(mailTemplate)) {
                listenerManager.addJobListener(new JobNoticeListener(mailTemplate), EverythingMatcher.allJobs());
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * Called in order to inform the <code>SchedulerPlugin</code> that it should
     * free up all of it's resources because the scheduler is shutting down.
     * </p>
     */
    public void shutdown() {
        // nothing to do...
    }

    public boolean isLogRunningInfo() {
        return isLogRunningInfo;
    }

    public void setLogRunningInfo(boolean isLogRunningInfo) {
        this.isLogRunningInfo = isLogRunningInfo;
    }

    public String getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

}
