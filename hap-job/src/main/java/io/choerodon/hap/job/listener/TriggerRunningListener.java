/*
 * #{copyright}#
 */
package io.choerodon.hap.job.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;

/**
 * @author shiliyan
 *
 */
public class TriggerRunningListener extends DefaultTriggerListener {

    @Override
    public String getName() {
        return "TriggerRunningListener";
    }

    /* (non-Javadoc)
     * @see DefaultTriggerListener#triggerFired(org.quartz.Trigger, org.quartz.JobExecutionContext)
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
    }

    /* (non-Javadoc)
     * @see DefaultTriggerListener#vetoJobExecution(org.quartz.Trigger, org.quartz.JobExecutionContext)
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    /* (non-Javadoc)
     * @see DefaultTriggerListener#triggerMisfired(org.quartz.Trigger)
     */
    @Override
    public void triggerMisfired(Trigger trigger) {
    }

    /* (non-Javadoc)
     * @see DefaultTriggerListener#triggerComplete(org.quartz.Trigger, org.quartz.JobExecutionContext, org.quartz.Trigger.CompletedExecutionInstruction)
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) {
    }

}
