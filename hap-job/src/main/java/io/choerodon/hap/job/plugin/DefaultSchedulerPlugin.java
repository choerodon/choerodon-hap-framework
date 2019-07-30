/*
 * #{copyright}#
 */
package io.choerodon.hap.job.plugin;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;

/**
 * @author shiliyan
 *
 */
public class DefaultSchedulerPlugin implements SchedulerPlugin {

    /* (non-Javadoc)
     * @see org.quartz.spi.SchedulerPlugin#initialize(java.lang.String, org.quartz.Scheduler, org.quartz.spi.ClassLoadHelper)
     */
    @Override
    public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.SchedulerPlugin#start()
     */
    @Override
    public void start() {

    }

    /* (non-Javadoc)
     * @see org.quartz.spi.SchedulerPlugin#shutdown()
     */
    @Override
    public void shutdown() {

    }

}
