package io.choerodon.hap.task.log;

import ch.qos.logback.core.OutputStreamAppender;

/**
 * @author peng.jiang@hand-china.com
 * @since 2017/11/28
 **/
public class TaskAppender<E> extends OutputStreamAppender<E> {

    @Override
    public void start() {
        TaskOutputStream outputStream = new TaskOutputStream();
        setOutputStream(outputStream);
        super.start();
    }


}
