/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment;

/**
 * 处理链接口.
 * @author xiaohua
 *
 */
public interface FileChain {
    /**
     * 处理链.
     * 
     * @throws Exception 异常
     */
    void doProcess() throws Exception;

    /**
     * 添加处理器.
     * 
     * @param processor FileProcessor
     */
    void addProcessor(FileProcessor processor);

}
