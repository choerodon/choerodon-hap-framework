/*
 * #{copyright}#
 */
package io.choerodon.hap.attachment.mapper;

import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by xiaohua on 16/2/1.
 * 
 * @author hua.xiao@hand-china.com
 */
public interface SysFileMapper extends Mapper<SysFile> {

    /**
     * 根据attachmentId SysFile 参数查找Sysfile列表.
     *
     * @param attachmentIds
     *            attachmentId列表
     * @param file
     *            SysFile参数
     * @return List SysFile列表
     */
    List<SysFile> selectFilesByAttachIds(@Param("attachmentIds") List<Long> attachmentIds, @Param("sysFile") SysFile file);

    /**
     * 根据id列表查找出所属文件对象集合.
     *
     * @param fileIds
     *            List
     * @return List SysFile对象列表
     */
    List<SysFile> selectFilesByIds(@Param("fileIds") List<String> fileIds);

    /* 查询附件下的所有文件 */
    List<SysFile> selectFilesByTypeAndKey(@Param("sourceType") String sourceType, @Param("sourceKey") String sourceKey);

    /* 局部刷新新增记录 */
    List<SysFile> queryfiles(@Param("sourceType") String sourceType, @Param("sourceKey") String sourceKey);

    /* 删除一条记录 */
    void deletefiles(@Param("fileId") Long fileId);

    List<SysFile> selectFilesByCategoryId(Long categoryId);

}
