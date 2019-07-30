package io.choerodon.hap.function.service.impl;

import io.choerodon.hap.account.constants.UserConstants;
import io.choerodon.hap.cache.impl.ResourceItemAssignCache;
import io.choerodon.hap.cache.impl.ResourceItemCache;
import io.choerodon.hap.cache.impl.ResourceItemElementCache;
import io.choerodon.hap.cache.impl.RoleResourceItemCache;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.dto.ResourceItem;
import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.hap.function.dto.ResourceItemCount;
import io.choerodon.hap.function.dto.ResourceItemElement;
import io.choerodon.hap.function.dto.RoleFunction;
import io.choerodon.hap.function.dto.RoleResourceItem;
import io.choerodon.hap.function.mapper.FunctionMapper;
import io.choerodon.hap.function.mapper.ResourceItemAssignMapper;
import io.choerodon.hap.function.mapper.RoleResourceItemMapper;
import io.choerodon.hap.function.service.IRoleFunctionService;
import io.choerodon.hap.function.service.IRoleResourceItemService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.base.util.BaseConstants;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色权限组件服务接口实现.
 *
 * @author njq.niu@hand-china.com
 * @author qiang.zeng@hand-china.com
 * @since 2016年4月7日
 */
@Service
@Dataset("RoleResourceItem")
public class RoleResourceItemServiceImpl extends BaseServiceImpl<RoleResourceItem> implements IRoleResourceItemService, IDatasetService<ResourceItemAssign> {

    @Autowired
    private RoleResourceItemMapper roleResourceItemMapper;
    @Autowired
    private FunctionMapper functionMapper;
    @Autowired
    private ResourceItemCache resourceItemCache;
    @Autowired
    private RoleResourceItemCache roleResourceItemCache;
    @Autowired
    private ResourceItemAssignMapper resourceItemAssignMapper;
    @Autowired
    private ResourceItemAssignCache resourceItemAssignCache;
    @Autowired
    private ResourceItemElementCache resourceItemElementCache;
    @Autowired
    private IRoleFunctionService roleFunctionService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MenuItem> queryResourceItems(Long roleId, Long functionId) {
        Function function = new Function();
        function.setFunctionId(functionId);
        List<Resource> resources = self().queryHtmlResources(function);
        List<MenuItem> menus = self().createResources(resources);
        //如果功能下没有设置权限组件 直接返回
        if (CollectionUtils.isEmpty(menus)) {
            return menus;
        }
        Long[] roleResourceItemIds = null;
        ResourceItemAssign[] resourceItemAssigns = null;
        if (roleId != null) {
            roleResourceItemIds = roleResourceItemCache.getValue(roleId.toString());
            resourceItemAssigns = resourceItemAssignCache.getValue(ResourceItemAssign.ASSIGN_TYPE_ROLE + BaseConstants.UNDER_LINE + roleId);
        }
        //如果角色没有分配权限组件，直接返回
        if (ArrayUtils.isEmpty(roleResourceItemIds) && ArrayUtils.isEmpty(resourceItemAssigns)) {
            return menus;
        }
        updateMenuCheck(menus, roleResourceItemIds, resourceItemAssigns);
        return menus;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Resource> queryHtmlResources(Function function) {
        Resource resource = new Resource();
        resource.setType("HTML");
        Map<String, Object> params = new HashMap<>(2);
        params.put("function", function);
        params.put("resource", resource);
        return functionMapper.selectExistsResourcesByFunction(params);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MenuItem> createResources(List<Resource> resourceList) {
        MenuItem root = new MenuItem();
        List<MenuItem> children = new ArrayList<>();
        root.setChildren(children);
        if (resourceList != null) {
            ResourceItemCount itemCount = new ResourceItemCount();
            for (Resource resource : resourceList) {
                MenuItem resourceMenu = createResourceItems(resource, itemCount);
                if (resourceMenu != null) {
                    children.add(resourceMenu);
                }
            }
        }
        return root.getChildren();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ResourceItemAssign> updateResourceItemAssign(List<ResourceItemAssign> resourceItemAssignList, Long roleId, Long functionId) {
        roleResourceItemMapper.deleteByRoleIdAndFunctionId(roleId, functionId);
        resourceItemAssignMapper.deleteByRoleIdAndFunctionId(roleId, functionId);
        if (CollectionUtils.isNotEmpty(resourceItemAssignList)) {
            for (ResourceItemAssign resourceItemAssign : resourceItemAssignList) {
                if ("oldType".equalsIgnoreCase(resourceItemAssign.getAssignType())) {
                    RoleResourceItem roleResourceItem = new RoleResourceItem();
                    roleResourceItem.setRoleId(resourceItemAssign.getTypeId());
                    roleResourceItem.setResourceItemId(resourceItemAssign.getElementId());
                    roleResourceItemMapper.insertSelective(roleResourceItem);
                } else if ("newType".equalsIgnoreCase(resourceItemAssign.getAssignType())) {
                    resourceItemAssign.setAssignType(ResourceItemAssign.ASSIGN_TYPE_ROLE);
                    resourceItemAssign.setEnable("N");
                    resourceItemAssign.setFunctionId(functionId);
                    resourceItemAssignMapper.insertSelective(resourceItemAssign);
                }
            }
        }
        roleResourceItemCache.load(roleId.toString());
        resourceItemAssignCache.load(ResourceItemAssign.ASSIGN_TYPE_ROLE + BaseConstants.UNDER_LINE + roleId);
        return resourceItemAssignList;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean hasResourceItem(Long roleId, Long resourceItemId) {
        return !(roleId == null || resourceItemId == null) && roleResourceItemMapper.selectByRoleIdAndResourceItemId(roleId, resourceItemId) != null;
    }

    /**
     * 给角色的权限组件打钩.
     * <p>
     * 说明：roleResourceItemIds是老版的权限组件分配,resourceItemAssigns是新版的权限组件分配,
     * 为了兼容处理，保留了roleResourceItemIds.
     *
     * @param menus               权限组件菜单
     * @param roleResourceItemIds 角色权限组件分配Id集合
     * @param resourceItemAssigns 角色权限组件分配集合
     */
    private void updateMenuCheck(List<MenuItem> menus, Long[] roleResourceItemIds, ResourceItemAssign[] resourceItemAssigns) {
        for (MenuItem menuItem : menus) {
            if (CollectionUtils.isNotEmpty(menuItem.getChildren())) {
                updateMenuCheck(menuItem.getChildren(), roleResourceItemIds, resourceItemAssigns);
            }
            if (ResourceItemElement.DATA_OLD.equalsIgnoreCase(menuItem.getFunctionCode())) {
                if (ArrayUtils.isNotEmpty(roleResourceItemIds)) {
                    for (Long roleResourceItemId : roleResourceItemIds) {
                        if (menuItem.getId().equals(roleResourceItemId)) {
                            menuItem.setIschecked(Boolean.TRUE);
                            break;
                        }
                    }
                }

            } else if (ResourceItemElement.DATA_NEW.equalsIgnoreCase(menuItem.getFunctionCode())) {
                if (ArrayUtils.isNotEmpty(resourceItemAssigns)) {
                    for (ResourceItemAssign resourceItemAssign : resourceItemAssigns) {
                        if (menuItem.getId().equals(resourceItemAssign.getElementId())) {
                            menuItem.setIschecked(Boolean.FALSE);
                            break;
                        }
                    }
                }
            }

        }
    }

    /**
     * 页面菜单上构建组件菜单.
     *
     * @param resource  资源
     * @param itemCount 构造菜单Id的中转类
     * @return 页面菜单
     */
    private MenuItem createResourceItems(Resource resource, ResourceItemCount itemCount) {
        ResourceItem[] resourceItemList = resourceItemCache.getValue(resource.getResourceId().toString());
        MenuItem resourceMenu;
        if (ArrayUtils.isEmpty(resourceItemList)) {
            return null;
        }
        resourceMenu = new MenuItem();
        resourceMenu.setText(resource.getName() + "(" + resource.getUrl() + ")");
        resourceMenu.setId(itemCount.getResourceId());
        itemCount.setResourceId(itemCount.getResourceId() - 10L);
        resourceMenu.setIcon("fa fa-file");
        List<MenuItem> resourceMenuChildren = new ArrayList<>();
        resourceMenu.setChildren(resourceMenuChildren);

        Map<String, List<ResourceItem>> itemMap = new HashMap<>(16);
        for (ResourceItem resourceItem : resourceItemList) {
            String itemType = resourceItem.getItemType();
            switch (itemType) {
                case ResourceItem.TYPE_VARIABLE:
                    setResourceItems(ResourceItem.TYPE_VARIABLE, itemMap, resourceItem);
                    break;
                case ResourceItem.TYPE_BUTTON:
                case ResourceItem.TYPE_FORM:
                case ResourceItem.TYPE_GRID:
                    setResourceItems(ResourceItem.TYPE_NOT_VARIABLE, itemMap, resourceItem);
                    break;
                default:
                    break;
            }
        }
        if (itemMap.size() != 0) {
            List<ResourceItem> variableItemList = itemMap.get(ResourceItem.TYPE_VARIABLE);
            List<ResourceItem> otherItemList = itemMap.get(ResourceItem.TYPE_NOT_VARIABLE);
            if (CollectionUtils.isNotEmpty(variableItemList)) {
                resourceMenu(variableItemList, resourceMenuChildren, itemCount);
            }
            if (CollectionUtils.isNotEmpty(otherItemList)) {
                resourceMenu(otherItemList, resourceMenuChildren, itemCount);
            }
        }
        if (CollectionUtils.isEmpty(resourceMenu.getChildren())) {
            return null;
        }
        return resourceMenu;
    }

    /**
     * 将权限组件分类，放到不同的Map中.
     *
     * @param type         权限组件类型
     * @param itemMap      Map<类型, 权限组件集合>
     * @param resourceItem 权限组件
     */
    private void setResourceItems(String type, Map<String, List<ResourceItem>> itemMap, ResourceItem resourceItem) {
        List<ResourceItem> sets = itemMap.computeIfAbsent(type, k -> new ArrayList<>());
        sets.add(resourceItem);
    }

    /**
     * 构建权限组件菜单.
     *
     * @param resourceItemList     权限组件集合
     * @param resourceMenuChildren 页面菜单下的权限组件菜单集合
     * @param itemCount            构造菜单Id的中转类
     */
    private void resourceMenu(List<ResourceItem> resourceItemList, List<MenuItem> resourceMenuChildren, ResourceItemCount itemCount) {
        String itemType = resourceItemList.get(0).getItemType();
        if (ResourceItem.TYPE_VARIABLE.equalsIgnoreCase(itemType)) {
            MenuItem variateMenu = new MenuItem();
            variateMenu.setText(UserConstants.SERVER_VARIABLE);
            variateMenu.setId(itemCount.getVariateId());
            variateMenu.setIschecked(Boolean.FALSE);
            variateMenu.setIcon("fa fa-info-circle");
            itemCount.setVariateId(itemCount.getVariateId() - 10L);
            resourceMenuChildren.add(variateMenu);
            List<MenuItem> variateMenuChildren = new ArrayList<>();
            variateMenu.setChildren(variateMenuChildren);
            for (ResourceItem resourceItem : resourceItemList) {
                MenuItem variate = new MenuItem();
                variate.setText(resourceItem.getItemName());
                variate.setId(resourceItem.getResourceItemId());
                variate.setIcon("fa fa-pencil-square-o");
                variate.setFunctionCode(ResourceItemElement.DATA_OLD);
                variateMenuChildren.add(variate);
            }
        } else {
            for (ResourceItem resourceItem : resourceItemList) {
                MenuItem menu = createResourceItemElements(resourceItem, itemCount);
                if (menu != null) {
                    resourceMenuChildren.add(menu);
                }
            }
        }

    }

    /**
     * 组件菜单上构建元素菜单.
     *
     * @param resourceItem 权限组件
     * @param itemCount    构造菜单Id的中转类
     * @return 组件菜单
     */
    private MenuItem createResourceItemElements(ResourceItem resourceItem, ResourceItemCount itemCount) {
        MenuItem resourceItemMenu;
        ResourceItemElement[] elementList = resourceItemElementCache.getValue(resourceItem.getResourceItemId().toString());
        if (ArrayUtils.isEmpty(elementList)) {
            return null;
        }
        resourceItemMenu = new MenuItem();
        resourceItemMenu.setText(translateItemType(resourceItem.getItemType()) + "  [" + resourceItem.getItemName() + "]");
        List<MenuItem> resourceItemChildren = new ArrayList<>();
        resourceItemMenu.setChildren(resourceItemChildren);
        if (ResourceItem.TYPE_BUTTON.equalsIgnoreCase(resourceItem.getItemType())) {
            resourceItemMenu.setIcon("fa  fa-hand-o-down");
            resourceItemMenu.setId(itemCount.getBtnGroupId());
            itemCount.setBtnGroupId(itemCount.getBtnGroupId() - 10L);
            for (ResourceItemElement button : elementList) {
                resourceItemChildren.add(createResourceItemElement(button));
            }
        } else if (ResourceItem.TYPE_FORM.equalsIgnoreCase(resourceItem.getItemType())) {
            resourceItemMenu.setIcon("fa fa-wpforms");
            resourceItemMenu.setId(itemCount.getFormId());
            itemCount.setFormId(itemCount.getFormId() - 10L);
            Map<String, List<ResourceItemElement>> itemMap = new HashMap<>(16);
            for (ResourceItemElement formElement : elementList) {
                String itemType = formElement.getType();
                switch (itemType) {
                    case ResourceItemElement.TYPE_FORM_BUTTONS:
                        setResourceItemElements(ResourceItemElement.TYPE_FORM_BUTTONS, itemMap, formElement);
                        break;
                    case ResourceItemElement.TYPE_FORM_FIELD:
                        setResourceItemElements(ResourceItemElement.TYPE_FORM_FIELD, itemMap, formElement);
                        break;
                    default:
                        break;
                }
            }
            if (itemMap.size() != 0) {
                List<ResourceItemElement> btnList = itemMap.get(ResourceItemElement.TYPE_FORM_BUTTONS);
                List<ResourceItemElement> fieldList = itemMap.get(ResourceItemElement.TYPE_FORM_FIELD);
                if (CollectionUtils.isNotEmpty(btnList)) {
                    setMenuItem(resourceItemChildren, btnList, itemCount, ResourceItemElement.TYPE_FORM_BUTTONS);
                }
                if (CollectionUtils.isNotEmpty(fieldList)) {
                    setMenuItem(resourceItemChildren, fieldList, itemCount, ResourceItemElement.TYPE_FORM_FIELD);
                }
            }

        } else if (ResourceItem.TYPE_GRID.equalsIgnoreCase(resourceItem.getItemType())) {
            resourceItemMenu.setIcon("fa fa-table");
            resourceItemMenu.setId(itemCount.getGridId());
            itemCount.setGridId(itemCount.getGridId() - 10L);
            Map<String, List<ResourceItemElement>> itemMap = new HashMap<>(16);
            for (ResourceItemElement gridElement : elementList) {
                String itemType = gridElement.getType();
                switch (itemType) {
                    case ResourceItemElement.TYPE_GRID_BUTTONS:
                        setResourceItemElements(ResourceItemElement.TYPE_GRID_BUTTONS, itemMap, gridElement);
                        break;
                    case ResourceItemElement.TYPE_COLUMN_BUTTONS:
                        setResourceItemElements(ResourceItemElement.TYPE_COLUMN_BUTTONS, itemMap, gridElement);
                        break;
                    case ResourceItemElement.TYPE_COLUMN:
                        setResourceItemElements(ResourceItemElement.TYPE_COLUMN, itemMap, gridElement);
                        break;
                    default:
                        break;
                }
            }
            if (itemMap.size() != 0) {
                List<ResourceItemElement> toolbarList = itemMap.get(ResourceItemElement.TYPE_GRID_BUTTONS);
                List<ResourceItemElement> colBtnList = itemMap.get(ResourceItemElement.TYPE_COLUMN_BUTTONS);
                List<ResourceItemElement> colList = itemMap.get(ResourceItemElement.TYPE_COLUMN);
                if (CollectionUtils.isNotEmpty(toolbarList)) {
                    setMenuItem(resourceItemChildren, toolbarList, itemCount, ResourceItemElement.TYPE_GRID_BUTTONS);
                }
                if (CollectionUtils.isNotEmpty(colBtnList)) {
                    setMenuItem(resourceItemChildren, colBtnList, itemCount, ResourceItemElement.TYPE_COLUMN_BUTTONS);
                }
                if (CollectionUtils.isNotEmpty(colList)) {
                    setMenuItem(resourceItemChildren, colList, itemCount, ResourceItemElement.TYPE_COLUMN);
                }
            }
        }
        return resourceItemMenu;
    }

    /**
     * 将权限组件元素分类，放到不同的Map中.
     *
     * @param type                权限组件元素类型
     * @param itemMap             Map<类型, 权限组件元素集合>
     * @param resourceItemElement 权限组件元素
     */
    private void setResourceItemElements(String type, Map<String, List<ResourceItemElement>> itemMap, ResourceItemElement resourceItemElement) {
        List<ResourceItemElement> sets = itemMap.computeIfAbsent(type, k -> new ArrayList<>());
        sets.add(resourceItemElement);
    }

    /**
     * 主要是为了复用，抽离出来设置权限组件菜单的属性.
     *
     * @param resourceItemChildren    权限组件菜单下的权限组件元素菜单
     * @param resourceItemElementList 权限组件元素集合
     * @param itemCount               构造菜单Id的中转类
     * @param type                    权限组件元素类型
     */
    private void setMenuItem(List<MenuItem> resourceItemChildren, List<ResourceItemElement> resourceItemElementList, ResourceItemCount itemCount, String type) {
        MenuItem menuItem = new MenuItem();
        setMenuItemProperty(menuItem, itemCount, type);
        resourceItemChildren.add(menuItem);
        List<MenuItem> menuItemChildren = new ArrayList<>();
        menuItem.setChildren(menuItemChildren);
        for (ResourceItemElement element : resourceItemElementList) {
            menuItemChildren.add(createResourceItemElement(element));
        }
    }

    /**
     * 根据权限组件元素类型，构造中间菜单的屬性.
     *
     * @param menuItem  中间菜单
     * @param itemCount 构造菜单Id的中转类
     * @param type      权限组件元素类型
     */
    private void setMenuItemProperty(MenuItem menuItem, ResourceItemCount itemCount, String type) {
        switch (type) {
            case ResourceItemElement.TYPE_FORM_BUTTONS:
                menuItem.setId(itemCount.getBtnId());
                itemCount.setBtnId(itemCount.getBtnId() - 10L);
                menuItem.setText(translateElementType(ResourceItemElement.TYPE_FORM_BUTTONS));
                menuItem.setIcon("fa fa-hand-o-down");
                break;
            case ResourceItemElement.TYPE_FORM_FIELD:
                menuItem.setId(itemCount.getFieldId());
                itemCount.setFieldId(itemCount.getFieldId() - 10L);
                menuItem.setText(translateElementType(ResourceItemElement.TYPE_FORM_FIELD));
                menuItem.setIcon("fa fa-file-text");
                break;
            case ResourceItemElement.TYPE_GRID_BUTTONS:
                menuItem.setId(itemCount.getToolbarId());
                itemCount.setToolbarId(itemCount.getToolbarId() - 10L);
                menuItem.setText(translateElementType(ResourceItemElement.TYPE_GRID_BUTTONS));
                menuItem.setIcon("fa fa-bars");
                break;
            case ResourceItemElement.TYPE_COLUMN_BUTTONS:
                menuItem.setId(itemCount.getColumnButtonId());
                itemCount.setColumnButtonId(itemCount.getColumnButtonId() - 10L);
                menuItem.setText(translateElementType(ResourceItemElement.TYPE_COLUMN_BUTTONS));
                menuItem.setIcon("fa fa-hand-o-down");
                break;
            case ResourceItemElement.TYPE_COLUMN:
                menuItem.setId(itemCount.getColumnId());
                itemCount.setColumnId(itemCount.getColumnId() - 10L);
                menuItem.setText(translateElementType(ResourceItemElement.TYPE_COLUMN));
                menuItem.setIcon("fa fa-columns");
                break;
            default:
                break;
        }
    }

    /**
     * 构建权限组件元素菜单.
     *
     * @param resourceItemElement 权限组件元素
     * @return 权限组件元素菜单
     */
    private MenuItem createResourceItemElement(ResourceItemElement resourceItemElement) {
        MenuItem menuItem = new MenuItem();
        menuItem.setText(resourceItemElement.getName());
        menuItem.setId(resourceItemElement.getElementId());
        menuItem.setIschecked(Boolean.TRUE);
        menuItem.setIcon("fa fa-pencil-square-o");
        menuItem.setFunctionCode(ResourceItemElement.DATA_NEW);
        return menuItem;
    }

    /**
     * 转换权限组件类型.
     *
     * @param type 权限组件类型
     * @return String
     */
    private String translateItemType(String type) {
        switch (type) {
            case ResourceItem.TYPE_FORM:
                type = "表单";
                break;
            case ResourceItem.TYPE_GRID:
                type = "表格";
                break;
            case ResourceItem.TYPE_BUTTON:
                type = "按钮组";
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 转换权限组件元素类型.
     *
     * @param type 权限组件元素类型
     * @return String
     */
    private String translateElementType(String type) {
        switch (type) {
            case ResourceItemElement.TYPE_GRID_BUTTONS:
                type = "工具栏按钮";
                break;
            case ResourceItemElement.TYPE_COLUMN_BUTTONS:
                type = "表格列按钮";
                break;
            case ResourceItemElement.TYPE_COLUMN:
                type = "表格列";
                break;
            case ResourceItemElement.TYPE_FORM_BUTTONS:
                type = "按钮";
                break;
            case ResourceItemElement.TYPE_FORM_FIELD:
                type = "字段";
                break;
            default:
                break;
        }
        return type;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            RoleFunction roleFunction = new RoleFunction();
            BeanUtils.populate(roleFunction, body);
            Long roleId = roleFunction.getRoleId();
            Long functionId = roleFunction.getFunctionId();
            List<MenuItem> menus = self().queryResourceItems(roleId, functionId);
            List<Function> functionsDTO = new ArrayList<>();
            roleFunctionService.getFunction(menus, functionsDTO, null);
            return functionsDTO;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<ResourceItemAssign> mutations(List<ResourceItemAssign> objs) {
        return self().updateResourceItemAssign(objs, objs.get(0).getRoleId(), objs.get(0).getFunctionId());
    }
}
