package io.choerodon.hap.task.info;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程维护.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/11/22
 **/

public class ThreadManageInfo {
    public static ConcurrentHashMap<String, Thread> threadHashMap = new ConcurrentHashMap<>();
}
