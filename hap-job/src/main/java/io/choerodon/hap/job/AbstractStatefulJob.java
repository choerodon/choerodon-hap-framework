/*
 * #{copyright}#
 */
package io.choerodon.hap.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import io.choerodon.hap.job.examples.BadJob1;

/**
 * 
 * 有状态的JOB.
 * 
 * JOB运行时可以已将需要临时保存的数据，保存在JobData中。
 * 
 * <p>
 * {@code JobDataMap dataMap = context.getJobDetail().getJobDataMap();} <br>
 * 
 * {@code int denominator = dataMap.getInt("denominator");}
 * 
 * @see BadJob1
 * 
 * @author shiliyan
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public abstract class AbstractStatefulJob extends AbstractJob {

}
