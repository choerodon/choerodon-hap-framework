/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment;

import java.util.List;

/**
 * 文件处理器，增加文件处理的后续环节.
 * @author xiaohua
 *
 */
public interface FileProcessor {

  /**
   * 处理逻辑.
   * @param chain 处理链上下文
   * @param uploader 文件上传Uploader
   * @param fileInfos  文件上传结果信息
   * @throws Exception 异常
   */
    void process(FileChain chain, Uploader uploader, List<FileInfo> fileInfos) throws Exception;

}
