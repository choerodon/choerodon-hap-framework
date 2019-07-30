/*
 * #{copyright}#
 */
package io.choerodon.hap.job;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

/**
 * @author shiliyan
 *
 */
public abstract class AbstractJob implements Job, JobListener {

    public static final String JOB_RUNNING_INFO_ID = "JOB_RUNNING_INFO_ID";

    private String executionSummary;

    /**
     * 
     * 自动处理所有Job运行时发生的异常.
     * 
     * <p>
     * 
     * @param context
     *            Job运行时的上下文。
     * 
     * @throws JobExecutionException
     *             if there is an exception while executing the job.
     */
    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            safeExecute(context);
        } catch (Exception e) {
            if (StringUtils.isEmpty(getExecutionSummary())) {
                setExecutionSummary(ExceptionUtils.getRootCauseMessage(e));
            }
            JobExecutionException e2 = new JobExecutionException(e);

            if (isRefireImmediatelyWhenException()) {
                // this job will refire immediately
                e2.setRefireImmediately(true);
            } else {
                // Quartz will automatically unschedule
                // all triggers associated with this job1
                // so that it does not run again

                try {
                    context.getScheduler().pauseTrigger(context.getTrigger().getKey());
                } catch (SchedulerException e1) {
                    e1.printStackTrace();
                }
            }
            throw e2;
        }
    }

    /**
     * 
     * 运行时所有的异常将会被自动处理.
     * 
     * @param context
     *            Job运行时的上下文。
     * @throws Exception
     *             运行时异常或实现子类主动抛出的异常
     */
    public abstract void safeExecute(JobExecutionContext context) throws Exception;

    /**
     * 
     * 发生异常时是否立即重新执行JOB或将JOB挂起.
     * <p>
     * 
     * @return {@code true} Job运行产生异常时，立即重新执行JOB. <br>
     *         {@code false} Job运行产生异常时，挂起JOB等候管理员处理.
     */
    protected boolean isRefireImmediatelyWhenException() {
        return false;
    }

    public String getExecutionSummary() {
        return executionSummary;
    }

    public void setExecutionSummary(String executionSummary) {
        this.executionSummary = executionSummary;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {

    }
}
