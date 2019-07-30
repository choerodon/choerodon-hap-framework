package io.choerodon.hap.util.service;



import io.choerodon.hap.util.dto.Lov;
import io.choerodon.hap.util.dto.LovItem;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;
import java.util.Locale;

/**
 * 通用lov服务接口.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/1/31
 */
public interface ILovService extends IBaseService<Lov> {

    /**
     * 删除lov配置.
     *
     * @param items 删除参数
     * @return 删除结果
     */
    boolean batchDeleteLov(List<Lov> items);

    /**
     * 批量删除item.
     *
     * @param items 参数
     * @return 结果
     */
    boolean batchDeleteItems(List<LovItem> items);

    /**
     * 删除LovItem.
     *
     * @param item 参数
     * @return 结果
     */
    boolean deleteLovItem(LovItem item);

    /**
     * 加载lov配置.
     *
     * @param lovId id
     * @return LOV
     */
    Lov loadLov(Long lovId);

    /**
     * 创建lov配置.
     *
     * @param lov Lov
     * @return LOV
     */
    Lov createLov(Lov lov);

    /**
     * 更新lov配置.
     *
     * @param lov Lov
     * @return LOV
     */
    Lov updateLov(Lov lov);

    /**
     * 条件查询lov配置.
     *
     * @param lov      Lov
     * @param page     页码
     * @param pageSize 数量
     * @return lov列表
     */
    List<Lov> selectLovs(Lov lov, int page, int pageSize);

    /**
     * 查询LovItem配置.
     *
     * @param lovItem lov配置
     * @return lov配置列表
     */
    List<LovItem> selectLovItems(LovItem lovItem);

    /**
     * 根据lov代码获取lov的配置.
     *
     * @param contextPath contextPath
     * @param locale      locale
     * @param lovCode     lovCode
     * @return lov配置
     */
    String getLov(String contextPath, Locale locale, String lovCode);

    /**
     * 通用lov获取数据.
     *
     * @param code     code
     * @param obj      obj
     * @param page     页码
     * @param pageSize 数量
     * @return DTO数据
     */
    List<?> selectDatas(String code, Object obj, int page, int pageSize);

    /**
     * 根据lov编码查询lov头行配置项.
     *
     * @param lov lov编码
     * @return lov头行配置项
     */
    Lov selectLovDefine(Lov lov);

    /**
     * 根据Lov代码从redis缓存查询Lov.
     *
     * @param code Lov代码
     * @return Lov
     */
    Lov selectLovByCache(String code);

    /**
     * 根据Lov代码查询lov.
     *
     * @param code 代码
     * @return Lov
     */
    Lov selectByCode(String code);
}
