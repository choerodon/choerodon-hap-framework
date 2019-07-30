package io.choerodon.hap.attachment.service;

import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.exception.CategorySourceTypeRepeatException;
import io.choerodon.hap.attachment.exception.StoragePathNotExsitException;
import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.web.core.IRequest;

import java.util.List;

/**
 * 分类接口.
 *
 * @author xiaohua
 */
public interface IAttachCategoryService extends ProxySelf<IAttachCategoryService> {

    /**
     * 插入分类.
     *
     * @param requestContext IRequest
     * @param categor        AttachCategory
     * @return AttachCategory 返回插入的AttachCategory
     */
    AttachCategory insert(IRequest requestContext, @StdWho AttachCategory categor);

    /**
     * 根据AttachCategory 携带的参数查找附件分类.
     *
     * @param requestContext IRequest
     * @param sourceType     业务表code
     * @return AttachCategory 返回分类
     */
    AttachCategory selectAttachByCode(IRequest requestContext, String sourceType);

    /**
     * 根据sourceCode 对应附件路径 .<br>
     * 比如：/attachment/image/2015/1
     *
     * @param requestContext IRequest
     * @param sourceType     业务code
     * @return String 存储路径
     * @throws StoragePathNotExsitException 存储路径不存在异常
     */
    String selectPathByCode(IRequest requestContext, String sourceType) throws StoragePathNotExsitException;

    /**
     * 根据查查询参数查询category.
     *
     * @param category AttachCategory
     * @return List AttachCategory列表
     */
    List<AttachCategory> selectCategories(AttachCategory category);

    /**
     * 批量更新.
     *
     * @param requestCtx IRequest
     * @param categories AttachCateogory列表
     * @return List AttachCateogory列表
     */
    List<AttachCategory> batchUpdate(IRequest requestCtx, @StdWho List<AttachCategory> categories);

    /**
     * 根据主健查询AttachCategory.
     *
     * @param requestContext IRequest
     * @param categoryId     分类目录主健id
     * @return AttachCategory 返回此目录
     */
    AttachCategory selectByPrimaryKey(IRequest requestContext, Long categoryId);

    /**
     * 归档.
     *
     * @param category AttachCategory
     * @return false or true
     */
    boolean remove(AttachCategory category);

    /**
     * 返回Cateogries列表.
     *
     * @param requestContext IRequest
     * @param category       AttachCategory参数
     * @return AttachCategory列表
     */
    List<AttachCategory> selectCategories(IRequest requestContext, AttachCategory category);

    /**
     * 验证 新增的Category sourceType是否存在.
     *
     * @param categories AttachCategory列表
     */
    void validateType(List<AttachCategory> categories) throws CategorySourceTypeRepeatException;

    /**
     * 查询父目录节点.
     *
     * @param requestContext IRequest
     * @param categoryId     分类Id
     * @return List
     */
    List<AttachCategory> selectCategoryBreadcrumbList(IRequest requestContext, Long categoryId);

    /*
     * 查询所有节点
     */
    List<AttachCategory> queryTree(IRequest requestContext, AttachCategory category);

}
