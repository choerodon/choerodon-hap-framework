package io.choerodon.hap.attachment.service;

import io.choerodon.hap.attachment.dto.Attachment;
import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.exception.UniqueFileMutiException;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 文件接口.
 *
 * @author xiaohua
 */
public interface ISysAttachmentService extends IBaseService<SysFile>, ProxySelf<ISysAttachmentService> {

    /**
     * 插入.
     *
     * @param requestContext IRequest
     * @param file           文件对象
     * @return 文件对象
     */
    SysFile insert(IRequest requestContext, SysFile file);

    /**
     * 删除文件.
     *
     * @param requestContext IRequest
     * @param file           文件对象
     * @return 文件对象
     */
    SysFile delete(IRequest requestContext, SysFile file);

    /**
     * 返回文件列表.
     *
     * @param requestContext IRequest
     * @param file           文件对象
     * @param page           页数
     * @param pageSize       单页条数
     * @return 文件列表
     */
    List<SysFile> selectFiles(IRequest requestContext, SysFile file, int page, int pageSize);

    /**
     * 根据参数和分类Id返回文件列表.
     *
     * @param requestContext IRequest
     * @param file           文件对象
     * @param categoryId     分类主健id
     * @param page           页数
     * @param pageSize       单页条数
     * @return 文件列表
     */
    List<SysFile> selectFilesByCategoryId(IRequest requestContext, SysFile file, Long categoryId, int page,
                                          int pageSize);


    /**
     * 根据业务类型和业务主键查找文件列表.
     *
     * @param requestContext IRequest
     * @param sourceType     业务类型
     * @param sourceKey      业务主键
     * @return 文件列表
     */
    List<SysFile> selectFilesBySourceTypeAndSourceKey(IRequest requestContext, String sourceType, String sourceKey);


    /**
     * 文件和附件一同保存.
     *
     * @param requestContext IRequest
     * @param attach         附件对象
     * @param file           文件对象
     * @return 文件对象
     */
    SysFile insertFileAndAttach(IRequest requestContext, @StdWho Attachment attach, @StdWho SysFile file);

    /**
     * 对于分类唯一的来调用.
     *
     * @param requestContext IRequest
     * @param attach         附件对象
     * @param file           文件对象
     * @return 返回更新或者插入的文件对象
     * @throws UniqueFileMutiException 此分类下有多个附件 异常
     */
    SysFile updateOrInsertFile(IRequest requestContext, @StdWho Attachment attach, @StdWho SysFile file)
            throws UniqueFileMutiException;

    /**
     * 根据id找到这个文件.
     *
     * @param requestContext IRequest
     * @param fileId         文件对象id
     * @return 文件对象
     */
    SysFile selectByPrimaryKey(IRequest requestContext, Long fileId);

    /**
     * 根据业务类型和业务主键 查找文件列表.
     *
     * @param requestContext IRequest
     * @param sourceType     业务类型
     * @param sourceKey      业务主键
     * @return 文件列表
     */
    List<SysFile> selectFilesByTypeAndKey(IRequest requestContext, String sourceType, String sourceKey);

    List<SysFile> selectFilesByTypeAndKey(IRequest requestContext, String sourceType, Long sourceKey);

    /**
     * 批量删除文件.
     *
     * @param requestContext IRequest
     * @param sysFiles       文件列表
     */
    void removeFiles(IRequest requestContext, List<SysFile> sysFiles);

    /**
     * 根据id列表查找文件列表.
     *
     * @param requestContext IRequest
     * @param fileIds        文件Id列表
     * @return 文件列表
     */
    List<SysFile> selectByIds(IRequest requestContext, List<String> fileIds);

    /**
     * 删除图片.
     *
     * @param requestContext IRequest
     * @param file           文件对象
     * @return 文件对象
     */
    SysFile deleteImage(IRequest requestContext, SysFile file);

    /**
     * 删除上传文件.
     *
     * @param requestContext IRequest
     * @param file           文件对象
     */
    void deletefiles(IRequest requestContext, SysFile file);


    /**
     * 通过业务类型和业务主键查询数据.
     *
     * @param requestContext IRequest
     * @param sourceType     业务类型
     * @param sourceKey      业务主键
     * @return 文件列表
     */
    List<SysFile> queryFilesByTypeAndKey(IRequest requestContext, String sourceType, String sourceKey);
}
