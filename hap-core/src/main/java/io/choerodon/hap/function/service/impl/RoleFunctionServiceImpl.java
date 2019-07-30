package io.choerodon.hap.function.service.impl;

import io.choerodon.hap.cache.impl.RoleFunctionCache;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.RoleFunction;
import io.choerodon.hap.function.mapper.RoleFunctionMapper;
import io.choerodon.hap.function.service.IFunctionService;
import io.choerodon.hap.function.service.IRoleFunctionService;
import io.choerodon.hap.message.components.DefaultRoleResourceListener;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.message.IMessagePublisher;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 角色功能服务接口实现.
 *
 * @author liuxiawang
 * @author njq.niu@hand-china.com
 */
@Service
@Dataset("RoleFunction")
public class RoleFunctionServiceImpl extends BaseServiceImpl<RoleFunction> implements IRoleFunctionService, IDatasetService<RoleFunction> {

    @Autowired
    private RoleFunctionMapper rolefunctionMapper;

    @Autowired
    private RoleFunctionCache roleFunctionCache;

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    private IFunctionService functionService;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Long[] getRoleFunctionById(Long roleId) {
        Long[] roleFuction = roleFunctionCache.getValue(roleId.toString());
        if (roleFuction == null) {
            roleFunctionCache.reload();
            return roleFunctionCache.getValue(roleId.toString());
        }
        return roleFuction;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<RoleFunction> batchUpdate(List<RoleFunction> roleFunctions) {
        if (CollectionUtils.isNotEmpty(roleFunctions)) {
            RoleFunction rf = roleFunctions.get(0);
            Long[] ids = new Long[roleFunctions.size()];
            Long roleId = rf.getRoleId();
            int i = 0;
            rolefunctionMapper.deleteByRoleId(roleId);
            for (RoleFunction rolefunction : roleFunctions) {
                if (rolefunction.getFunctionId() != null) {
                    rolefunctionMapper.insertSelective(rolefunction);
                    ids[i++] = rolefunction.getFunctionId();
                } else {
                    ids = null;
                }
            }
            removeCache(roleId);
            if (ArrayUtils.isNotEmpty(ids)) {
                roleFunctionCache.setValue(roleId.toString(), ids);
                self().reloadRoleResourceCache(roleId);
            }
        }
        return roleFunctions;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void reloadRoleResourceCache(Long roleId) {
        notifyCache(roleId);
    }

    @Override
    public void removeRoleFunctionByRoleId(Long roleId) {
        self().removeByRoleId(roleId);
        removeCache(roleId);
    }

    /**
     * 通过角色ID删除数据库角色.
     *
     * @param roleId 角色ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByRoleId(Long roleId) {
        rolefunctionMapper.deleteByRoleId(roleId);
    }

    /**
     * 清除角色功能缓存和角色资源二级缓存.
     *
     * @param roleId 角色Id
     */
    private void removeCache(Long roleId) {
        roleFunctionCache.remove(roleId.toString());
        notifyCache(roleId);
    }

    /**
     * 发消息操作角色资源二级缓存.
     *
     * @param roleId 角色ID
     */
    private void notifyCache(Long roleId) {
        messagePublisher.publish(DefaultRoleResourceListener.CACHE_ROLE_RESOURCE, roleId);
    }

    /**
     * 将MenuItem拆分成Function
     *
     * @param menus
     * @param functions
     * @param parentId
     */
    @Override
    public void getFunction(List<MenuItem> menus, List<Function> functions, Long parentId) {
        Integer count = 0;
        if (menus != null && menus.size() > 0) {
            for (MenuItem menu : menus) {
                Function fun = new Function();
                fun.setFunctionId(menu.getId());
                fun.setResourceUrl(menu.getUrl());
                fun.setFunctionName(menu.getText());
                fun.setParentFunctionId(parentId);
                fun.setFunctionIcon(menu.getIcon());
                fun.setExpand(true);
                fun.setFunctionCode(menu.getFunctionCode());
                String isChecked = (menu.getIschecked() != null) && menu.getIschecked() ? "Y" : "N";
                fun.setIsChecked(isChecked);
                functions.add(fun);
                if (menu.getChildren() != null) {
                    getFunction(menu.getChildren(), functions, menu.getId());
                }

            }
        }
    }

    @Override
    public void updateMenuCheck(final List<MenuItem> menus, final Long[] ids) {
        if (CollectionUtils.isEmpty(menus) || ArrayUtils.isEmpty(ids)) {
            return;
        }
        for (MenuItem menuItem : menus) {
            if (menuItem.getChildren() != null && !menuItem.getChildren().isEmpty()) {
                updateMenuCheck(menuItem.getChildren(), ids);
            }
            for (Long id : ids) {
                if (menuItem.getId().equals(id)) {
                    menuItem.setIschecked(Boolean.TRUE);
                    break;
                }
            }
        }
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            RoleFunction roleFunction = new RoleFunction();
            BeanUtils.populate(roleFunction, body);
            List<MenuItem> menus = functionService.selectAllMenus();
            Long roleId = roleFunction.getRoleId();
            if (roleId != null) {
                Long[] ids = self().getRoleFunctionById(roleId);
                self().updateMenuCheck(menus, ids);
            }
            List<Function> functions = new ArrayList<>();
            self().getFunction(menus, functions, null);
            return functions;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<RoleFunction> mutations(List<RoleFunction> objs) {
        return self().batchUpdate(objs);
    }
}
