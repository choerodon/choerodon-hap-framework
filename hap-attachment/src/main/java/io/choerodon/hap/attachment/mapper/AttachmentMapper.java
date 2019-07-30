/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment.mapper;

import io.choerodon.hap.attachment.dto.Attachment;
import io.choerodon.mybatis.common.Mapper;

import java.util.Map;

/**
 * Created by xiaohua on 16/2/1.
 * @author hua.xiao@hand-china.com
 */
public interface  AttachmentMapper extends Mapper<Attachment> {


    /**
     * 根据Attachment对象查找单个Attachment.
     * 
     * @param attachment Attachment对象
     * @return  Attachment Attachment对象
     */
    Attachment selectAttachment(Attachment attachment);
    

    /**
     * 更新来源主键.
     * 
     * @param param 参数
     */
    int upgradeSourceKey(Map<String, Object> param);

}
