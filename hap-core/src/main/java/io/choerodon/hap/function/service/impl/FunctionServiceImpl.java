package io.choerodon.hap.function.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.FunctionResource;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.mapper.FunctionMapper;
import io.choerodon.hap.function.mapper.FunctionResourceMapper;
import io.choerodon.hap.function.mapper.RoleFunctionMapper;
import io.choerodon.hap.function.service.IFunctionService;
import io.choerodon.hap.function.service.IResourceService;
import io.choerodon.hap.function.service.IRoleFunctionService;
import io.choerodon.hap.message.components.DefaultRoleResourceListener;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.hap.util.dto.Language;
import io.choerodon.hap.util.service.ILanguageProvider;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.redis.impl.HashStringRedisCacheGroup;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 功能服务接口实现.
 *
 * @author wuyichu
 */
@Service
@Dataset("Function")
public class FunctionServiceImpl extends BaseServiceImpl<Function> implements IFunctionService, IDatasetService<Function> {

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private FunctionResourceMapper functionResourceMapper;

    @Autowired
    private RoleFunctionMapper roleFunctionMapper;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IRoleFunctionService roleFunctionService;

    @Autowired
    @Qualifier("functionCache")
    private HashStringRedisCacheGroup<Function> functionCache;

    @Autowired
    private ILanguageProvider languageProvider;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Function> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Function function = new Function();
            BeanUtils.populate(function, body);
            function.setSortname(sortname);
            function.setSortorder(isDesc ? "desc" : "asc");
            Criteria criteria = new Criteria(function);
            criteria.where(new WhereField(Function.FIELD_FUNCTION_CODE, Comparison.LIKE), Function.FIELD_FUNCTION_NAME, Function.FIELD_PARENT_FUNCTION_NAME, Function.FIELD_PARENT_FUNCTION_ID, new WhereField(Function.FIELD_MODULE_CODE, Comparison.LIKE), Function.FIELD_RESOURCE_ID);
            return super.selectOptions(function, criteria, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Function> mutations(List<Function> functionList) {
        for (Function function : functionList) {
            switch (function.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(function);
                    break;
                case DTOStatus.UPDATE:
                    self().updateByPrimaryKey(function);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(function);
                    break;
            }
        }
        return functionList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Function insertSelective(@StdWho Function function) {
        if (function == null) {
            return null;
        }
        super.insertSelective(function);
        reloadFunctionCache(function);
        return function;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Function updateByPrimaryKey(Function function) {
        if (function == null) {
            return null;
        }
        Criteria criteria = new Criteria(function);
        criteria.update(Function.FIELD_FUNCTION_CODE, Function.FIELD_MODULE_CODE, Function.FIELD_FUNCTION_NAME, Function.FIELD_FUNCTION_ICON, Function.FIELD_FUNCTION_SEQUENCE,
                Function.FIELD_FUNCTION_DESCRIPTION, Function.FIELD_PARENT_FUNCTION_ID, Function.FIELD_RESOURCE_ID);
        criteria.updateExtensionAttribute();
        super.updateByPrimaryKeyOptions(function, criteria);
        reloadFunctionCache(function);
        return function;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Function selectByPrimaryKey(Function function) {
        if (function == null || function.getFunctionId() == null) {
            return null;
        }
        return functionMapper.selectByPrimaryKey(function.getFunctionId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Function> batchUpdate(List<Function> functions) {
        if (CollectionUtils.isEmpty(functions)) {
            return functions;
        }
        for (Function function : functions) {
            if (function.getFunctionId() == null) {
                self().insertSelective(function);
            } else {
                self().updateByPrimaryKey(function);
            }
        }
        return functions;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Function> functions) {
        int result = 0;
        if (CollectionUtils.isEmpty(functions)) {
            return result;
        }
        for (Function function : functions) {
            deleteByPrimaryKey(function);
            result++;
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Function> selectFunction(Function function, int page, int pageSize) {
        Criteria criteria = new Criteria(function);
        criteria.unSelect(Function.FIELD_PARENT_FUNCTION_NAME);
        return super.selectOptions(function, criteria, page, pageSize);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Resource> selectExitResourcesByFunction(Function function, Resource resource,
                                                        int page, int pageSize) {
        if (function == null || function.getFunctionId() == null) {
            return null;
        }
        if (pageSize != 0) {
            PageHelper.startPage(page, pageSize);
        }
        Map<String, Object> params = new HashMap<>(2);
        params.put("function", function);
        params.put("resource", resource);
        return functionMapper.selectExistsResourcesByFunction(params);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Resource> selectNotExitResourcesByFunction(Function function, Resource resource,
                                                           int page, int pageSize) {
        if (function == null || function.getFunctionId() == null) {
            return null;
        }
        PageHelper.startPage(page, pageSize);
        Map<String, Object> params = new HashMap<>(2);
        params.put("function", function);
        params.put("resource", resource);
        return functionMapper.selectNotExistsResourcesByFunction(params);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Function updateFunctionResources(Function function, List<Resource> resources) {
        if (function != null && CollectionUtils.isNotEmpty(resources)) {
            IRequest request = RequestHelper.getCurrentRequest();
            for (Resource resource : resources) {
                if (DTOStatus.ADD.equals(resource.get__status())) {
                    FunctionResource functionResource = new FunctionResource();
                    functionResource.setResourceId(resource.getResourceId());
                    functionResource.setFunctionId(function.getFunctionId());
                    functionResource.setObjectVersionNumber(1L);
                    functionResource.setCreatedBy(request.getUserId());
                    functionResource.setCreationDate(new Date());
                    functionResource.setLastUpdateDate(new Date());
                    functionResource.setLastUpdatedBy(request.getUserId());
                    functionResourceMapper.insertSelective(functionResource);
                } else if (DTOStatus.DELETE.equals(resource.get__status())) {
                    functionResourceMapper.deleteFunctionResource(function.getFunctionId(),
                            resource.getResourceId());
                }
            }
            notifyCache(null);
        }
        return function;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MenuItem> selectAllMenus() {

        List<Function> functions = functionCache.getGroupAll(RequestHelper.getCurrentRequest().getLocale());
        MenuItem root = castFunctionsToMenuItem(functions);
        return root.getChildren();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MenuItem> selectRoleFunctions() {
        IRequest request = RequestHelper.getCurrentRequest();
        List<Function> functions = functionCache.getGroupAll(RequestHelper.getCurrentRequest().getLocale());
        Set<Long> allRoleFunctionIds = new HashSet<>();
        Long[] roleIds = request.getAllRoleId();
        //取出登录用户下所有角色的功能ID集合 去重角色拥有的相同功能
        for (Long roleId : roleIds) {
            Long[] roleFunctionIds = roleFunctionService.getRoleFunctionById(roleId);
            if (ArrayUtils.isNotEmpty(roleFunctionIds)) {
                allRoleFunctionIds.addAll(Arrays.asList(roleFunctionIds));
            }
        }
        Map<Long, Function> functionMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(functions)) {
            for (Function f : functions) {
                functionMap.put(f.getFunctionId(), f);
            }
        }
        Map<Long, MenuItem> menuMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(allRoleFunctionIds)) {
            for (Long functionId : allRoleFunctionIds) {
                createMenuRecursive(menuMap, functionMap, functionId);
            }
        }
        List<MenuItem> itemList = new ArrayList<>();
        menuMap.forEach((k, v) -> {
            if (v.getParent() == null) {
                itemList.add(v);
            }
            if (v.getChildren() != null) {
                Collections.sort(v.getChildren());
            }
        });
        Collections.sort(itemList);
        return itemList;
    }

    /**
     * 创建所有的功能菜单.
     *
     * @param functions 所有功能集合
     * @return 功能菜单根节点
     */
    private MenuItem castFunctionsToMenuItem(List<Function> functions) {
        MenuItem root = new MenuItem();
        List<MenuItem> children = new ArrayList<>();
        root.setChildren(children);

        Map<Long, Function> idToFuncMap = new HashMap<>(16);
        for (Function f : functions) {
            idToFuncMap.put(f.getFunctionId(), f);
        }

        Map<Long, MenuItem> map = new HashMap<>(16);
        Iterator<Function> iterator = functions.iterator();
        //创建一级菜单
        while (iterator.hasNext()) {
            Function function = iterator.next();
            if (idToFuncMap.get(function.getParentFunctionId()) == null) {
                MenuItem rootChild = createMenuItem(function);
                map.put(function.getFunctionId(), rootChild);
                children.add(rootChild);
                iterator.remove();
            }
        }

        processFunctions(map, functions);
        map.forEach((k, v) -> {
            if (v.getChildren() != null) {
                Collections.sort(v.getChildren());
            }
        });
        Collections.sort(children);
        return root;
    }

    /**
     * 构建二级与二级以后的菜单.
     *
     * @param map       功能菜单Map
     * @param functions 所有功能Map
     */
    private void processFunctions(Map<Long, MenuItem> map, List<Function> functions) {
        Iterator<Function> iterator = functions.iterator();
        int functionSize = functions.size();
        while (iterator.hasNext()) {
            Function function = iterator.next();
            MenuItem parent = map.get(function.getParentFunctionId());
            if (parent != null) {
                MenuItem item = createMenuItem(function);
                map.put(function.getFunctionId(), item);
                List<MenuItem> children = parent.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    parent.setChildren(children);
                }
                children.add(item);
                iterator.remove();
            }
        }

        if (functions.size() == functionSize) {
            // 功能定义存在循环
            detectCircle(functions);
        }
        //构建下一级菜单
        if (!functions.isEmpty()) {
            processFunctions(map, functions);
        }
    }

    /**
     * 检测循环链.
     *
     * @param functions 功能集合
     */
    private void detectCircle(List<Function> functions) {
        Map<Long, Function> tmpFuncMap = new HashMap<>(16);
        for (Function f : functions) {
            tmpFuncMap.put(f.getFunctionId(), f);
        }
        List<Function> tmpList = new ArrayList<>();
        for (Function f : functions) {
            tmpList.clear();
            Function f0 = f;
            tmpList.add(f0);
            while (f0.getParentFunctionId() != null) {
                f0 = tmpFuncMap.get(f0.getParentFunctionId());
                int idx = tmpList.indexOf(f0);
                if (idx != -1) {
                    tmpList.add(f0);
                    String msg = tmpList.stream().skip(idx)
                            .map(a -> a.getFunctionName() + "(" + a.getFunctionId() + ")")
                            .reduce((a, b) -> a + "-->" + b).get();
                    throw new RuntimeException(msg);
                }
                tmpList.add(f0);
            }
        }
    }

    /**
     * 递归创建当前登录用户的功能菜单.
     *
     * @param menuMap     功能菜单Map
     * @param functionMap 所有功能Map
     * @param functionId  当前登录用户拥有的功能Id
     * @return 功能菜单
     */
    private MenuItem createMenuRecursive(Map<Long, MenuItem> menuMap, Map<Long, Function> functionMap, Long functionId) {
        MenuItem menuItem = menuMap.get(functionId);
        if (menuItem == null) {
            Function function = functionMap.get(functionId);
            if (function == null) {
                // role has a function that dose not exists.
                return null;
            }
            menuItem = createMenuItem(function);
            menuMap.put(functionId, menuItem);
            // create parent menuItem
            Long parentId = function.getParentFunctionId();
            if (parentId != null) {
                MenuItem parentMenuItem = createMenuRecursive(menuMap, functionMap, parentId);
                if (parentMenuItem != null) {
                    List<MenuItem> children = parentMenuItem.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parentMenuItem.setChildren(children);
                    }
                    menuItem.setParent(parentMenuItem);
                    children.add(menuItem);
                }
            }
        }
        return menuItem;
    }

    /**
     * 创建功能菜单.
     *
     * @param function 功能
     * @return 功能菜单叶子节点
     */
    private MenuItem createMenuItem(Function function) {
        MenuItem menu = new MenuItem();
        menu.setText(function.getFunctionName());
        menu.setIcon(function.getFunctionIcon());
        menu.setFunctionCode(function.getFunctionCode());
        menu.setUrl(function.getResourceUrl());
        menu.setId(function.getFunctionId());
        menu.setScore(function.getFunctionSequence());
        menu.setSymbol(function.getResourceType());
        return menu;
    }

    /**
     * 重新加载功能缓存.
     *
     * @param function 功能
     */
    private void reloadFunctionCache(Function function) {
        IRequest old = RequestHelper.getCurrentRequest();
        try {
            IRequest iRequest = RequestHelper.newEmptyRequest();
            RequestHelper.setCurrentRequest(iRequest);
            for (Language language : languageProvider.getSupportedLanguages()) {
                iRequest.setLocale(language.getLangCode());
                Function f = functionMapper.selectForReloadCache(function);
                if (f != null) {
                    functionCache.getGroup(language.getLangCode()).setValue(function.getFunctionId().toString(), f);
                }
            }
        } finally {
            RequestHelper.setCurrentRequest(old);
        }
    }

    /**
     * 删除功能缓存.
     *
     * @param functionId 功能Id
     */
    private void deleteFunctionCache(Long functionId) {
        for (Language language : languageProvider.getSupportedLanguages()) {
            functionCache.getGroup(language.getLangCode()).remove(functionId.toString());
        }
    }

    /**
     * 根据主键Id级联删除功能.
     *
     * @param function 功能
     * @return int
     */
    @Override
    public int deleteByPrimaryKey(Function function) {
        if (function == null || function.getFunctionId() == null) {
            return 0;
        }
        List<Function> children = functionMapper.selectFunctionIdByParentId(function.getFunctionId());
        children.forEach(this::deleteByPrimaryKey);
        int updateCount = functionMapper.deleteByPrimaryKey(function);
        checkOvn(updateCount, function);
        functionResourceMapper.deleteByFunctionId(function.getFunctionId());
        roleFunctionMapper.deleteByFunctionId(function.getFunctionId());
        deleteFunctionCache(function.getFunctionId());
        return updateCount;
    }

    /**
     * 发消息操作二级缓存.
     *
     * @param roleId 角色ID
     */
    private void notifyCache(Long roleId) {
        messagePublisher.publish(DefaultRoleResourceListener.CACHE_ROLE_RESOURCE, roleId);
    }
}
