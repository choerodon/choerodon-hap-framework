package io.choerodon.hap.function.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 功能服务接口.
 *
 * @author wuyichu
 */
public interface IFunctionService extends IBaseService<Function>, ProxySelf<IFunctionService> {

    /**
     * 查询所有菜单.
     *
     * @return 返回所有的菜单集合
     */
    List<MenuItem> selectAllMenus();

    /**
     * 查询当前登录用户角色合并后的菜单.
     *
     * @return 当前登录用户角色合并后的菜单集合
     */
    List<MenuItem> selectRoleFunctions();

    /**
     * 根据功能条件查询.
     *
     * @param function 功能
     * @param page     页码
     * @param pageSize 页数
     * @return 满足条件的功能集合
     */
    List<Function> selectFunction(Function function, int page, int pageSize);

    /**
     * 批量删除功能.
     *
     * @param functions 功能集合
     * @return int
     */
    int batchDelete(List<Function> functions);

    /**
     * 查询功能挂靠的资源.
     *
     * @param function 功能
     * @param resource 资源
     * @param page     页码
     * @param pageSize 页数
     * @return 满足条件的resource集合
     */
    List<Resource> selectExitResourcesByFunction(Function function, Resource resource, int page,
                                                 int pageSize);

    /**
     * 查询功能没有挂靠的资源.
     *
     * @param function 功能
     * @param resource 资源
     * @param page     页码
     * @param pageSize 页数
     * @return 返回满足条件的资源
     */
    List<Resource> selectNotExitResourcesByFunction(Function function, Resource resource, int page,
                                                    int pageSize);

    /**
     * 更新功能挂靠的资源.
     *
     * @param function  功能
     * @param resources 资源集合
     * @return 修改后的功能信息
     */
    Function updateFunctionResources(Function function, List<Resource> resources);
}
