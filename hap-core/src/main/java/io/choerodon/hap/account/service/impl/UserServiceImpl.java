package io.choerodon.hap.account.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.hap.account.constants.UserConstants;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.mapper.UserMapper;
import io.choerodon.hap.account.mapper.UserRoleMapper;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.cache.impl.ResourceItemAssignCache;
import io.choerodon.hap.cache.impl.RoleFunctionCache;
import io.choerodon.hap.cache.impl.UserCache;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.core.util.ValidateUtils;
import io.choerodon.hap.function.dto.Function;
import io.choerodon.hap.function.dto.MenuItem;
import io.choerodon.hap.function.dto.Resource;
import io.choerodon.hap.function.dto.ResourceItemAssign;
import io.choerodon.hap.function.mapper.ResourceItemAssignMapper;
import io.choerodon.hap.function.service.IFunctionService;
import io.choerodon.hap.function.service.IRoleFunctionService;
import io.choerodon.hap.function.service.IRoleResourceItemService;
import io.choerodon.hap.iam.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.infra.common.util.ParamUtils;
import io.choerodon.hap.security.CustomUserDetails;
import io.choerodon.hap.security.IUserSecurityStrategy;
import io.choerodon.hap.security.PasswordManager;
import io.choerodon.hap.security.service.impl.UserSecurityStrategyManager;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户服务接口 - 实现类.
 *
 * @author njq.niu@hand-china.com
 * @author yuliao.chen@hand-china.com
 */
@Service
@Dataset("AccountUser")
public class UserServiceImpl extends BaseServiceImpl<User> implements IUserService, IDatasetService<User> {

    @Autowired
    private PasswordManager passwordManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSecurityStrategyManager userSecurityStrategyManager;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private IFunctionService functionService;

    @Autowired
    private IRoleFunctionService roleFunctionService;

    @Autowired
    private IRoleResourceItemService roleResourceItemService;

    @Autowired
    private ResourceItemAssignCache resourceItemAssignCache;

    @Autowired
    private ResourceItemAssignMapper resourceItemAssignMapper;

    @Autowired
    private RoleFunctionCache roleFunctionCache;

    @Autowired
    private UserCache userCache;

    @Autowired
    private ChoerodonRoleService roleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User insertSelective(@StdWho User record) {
        if (StringUtils.isEmpty(record.getPassword())) {
            record.setPassword(passwordManager.getDefaultPassword());
        }
        //用户名保存为小写
        record.setUserName(record.getUserName().toLowerCase());
        record.setPasswordEncrypted(passwordManager.encode(record.getPassword()));
        List<IUserSecurityStrategy> userSecurityStrategies = userSecurityStrategyManager.getUserSecurityStrategyList();
        for (IUserSecurityStrategy userSecurityStrategy : userSecurityStrategies) {
            record = userSecurityStrategy.beforeCreateUser(RequestHelper.getCurrentRequest(), record);
        }
        record = super.insertSelective(record);
        //更新密码修改时间
        userMapper.updatePassword(record.getUserId(), record.getPasswordEncrypted());
        return record;
    }

    @Override
    public void validateEmail(String email) throws UserException {
        if (!ValidateUtils.validateEmail(email)) {
            throw new UserException(UserException.EMAIL_FORMAT, new Object[]{});
        }
    }

    @Override
    public void validatePhone(String phone) throws UserException {
        if (!ValidateUtils.validatePhone(phone)) {
            throw new UserException(UserException.PHONE_FORMAT, new Object[]{});
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User login(User user) throws UserException {
        if (user == null || org.apache.commons.lang3.StringUtils.isAnyBlank(user.getUserName(), user.getPassword())) {
            throw new UserException(UserException.ERROR_USER_PASSWORD, UserException.ERROR_USER_PASSWORD, null);
        }
        User user1 = userMapper.selectByUserName(user.getUserName());
        if (user1 == null) {
            throw new UserException(UserException.ERROR_USER_PASSWORD, UserException.ERROR_USER_PASSWORD, null);
        }
        if (User.STATUS_LOCK.equals(user1.getStatus())) {
            throw new UserException(UserException.ERROR_USER_EXPIRED, UserException.ERROR_USER_EXPIRED, null);
        }
        if (user1.getStartActiveDate() != null && user1.getStartActiveDate().getTime() > System.currentTimeMillis()) {
            throw new UserException(UserException.ERROR_USER_EXPIRED, UserException.ERROR_USER_EXPIRED, null);
        }
        if (user1.getEndActiveDate() != null && user1.getEndActiveDate().getTime() < System.currentTimeMillis()) {
            throw new UserException(UserException.ERROR_USER_EXPIRED, UserException.ERROR_USER_EXPIRED, null);
        }
        if (!passwordManager.matches(user.getPassword(), user1.getPasswordEncrypted())) {
            throw new UserException(UserException.ERROR_USER_PASSWORD, UserException.ERROR_USER_PASSWORD, null);
        }
        return user1;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User selectByUserName(String userName) {
        userName = userName.toLowerCase();
        // 暂时去掉用户登录缓存
      /*  User user = userCache.getValue(userName);
        if (null == user) {
            user = userMapper.selectByUserName(userName);
            if (null != user) {
                userCache.setValue(userName, user);
            }
        }
        */
        return userMapper.selectByUserName(userName);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<User> selectUserNameByEmployeeCode(String employeeCode) {
        return userMapper.selectUserNameByEmployeeCode(employeeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateByPrimaryKeySelective(@StdWho User record) {
        int count = userMapper.updateBasic(record);
        checkOvn(count, record);
        userCache.remove(record.getUserName());
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, String userName, String password) {
        userMapper.updatePassword(userId, passwordManager.encode(password));
        userCache.remove(userName);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void validatePassword(String newPwd, String newPwdAgain) throws UserException {
        List<IUserSecurityStrategy> userSecurityStrategies = userSecurityStrategyManager.getUserSecurityStrategyList();
        for (IUserSecurityStrategy userSecurityStrategy : userSecurityStrategies) {
            userSecurityStrategy.validatePassword(newPwd, newPwdAgain);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotFirstLogin(Long userId, String status) {
        userMapper.updateFirstLogin(userId, status);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<User> selectUsers(User user, int pageNum, int pageSize) {
        if (user != null && StringUtil.isNotEmpty(user.getUserName())) {
            user.setUserName(user.getUserName().toLowerCase());
        }
        PageHelper.startPage(pageNum, pageSize);
        return userMapper.selectUsersOption(user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MenuItem> queryFunction(Long userId) {
        List<MenuItem> menus = functionService.selectAllMenus();
        Set<Long> userFunctionIds = null;
        if (userId != null) {
            userFunctionIds = getUserFunctionIds(RequestHelper.getCurrentRequest().getAllRoleId());
        }
        return updateMenu(menus, userFunctionIds);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MenuItem> queryResourceItems(Long userId, Long functionId) {
        Function function = new Function();
        function.setFunctionId(functionId);
        List<Resource> resources = roleResourceItemService.queryHtmlResources(function);
        List<MenuItem> menus = updateResourceMenu(roleResourceItemService.createResources(resources));
        //如果功能下没有设置权限组件 直接返回
        if (CollectionUtils.isEmpty(menus)) {
            return menus;
        }
        List<Long> allAssignElementIds = getAllAssignElementIds(userId, Arrays.asList(RequestHelper.getCurrentRequest().getAllRoleId()));
        //如果用户没有分配权限组件，直接返回
        if (CollectionUtils.isEmpty(allAssignElementIds)) {
            return menus;
        }
        return updateMenuCheck(menus, allAssignElementIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ResourceItemAssign> updateResourceItemAssign(List<ResourceItemAssign> resourceItemAssignList, Long userId, Long functionId) {
        resourceItemAssignMapper.deleteByUserIdAndFunctionId(userId, functionId);
        if (CollectionUtils.isNotEmpty(resourceItemAssignList)) {
            for (ResourceItemAssign resourceItemAssign : resourceItemAssignList) {
                resourceItemAssign.setAssignType(ResourceItemAssign.ASSIGN_TYPE_USER);
                resourceItemAssign.setFunctionId(functionId);
                resourceItemAssignMapper.insertSelective(resourceItemAssign);
            }
        }
        resourceItemAssignCache.load(ResourceItemAssign.ASSIGN_TYPE_USER + BaseConstants.UNDER_LINE + userId);
        return resourceItemAssignList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResourceItems(Long userId, Long functionId) {
        resourceItemAssignMapper.deleteByUserIdAndFunctionId(userId, functionId);
        resourceItemAssignCache.load(ResourceItemAssign.ASSIGN_TYPE_USER + BaseConstants.UNDER_LINE + userId);
    }

    @Override
    public void resetPassword(User user, String passwordAgain) throws UserException {
        String password = user.getPassword();
        validatePassword(password, passwordAgain);
        user = userMapper.selectByPrimaryKey(user);
        self().updatePassword(user.getUserId(), user.getUserName(), password);
    }

    @Override
    public void firstAndExpiredLoginUpdatePassword(String newPassword, String newPasswordAgain) throws UserException {
        IRequest request = RequestHelper.getCurrentRequest();
        Long accountId = request.getUserId();
        String userName = request.getUserName();
        validatePassword(newPassword, newPasswordAgain);
        self().updatePassword(accountId, userName, newPassword);
        self().updateNotFirstLogin(accountId, User.NOT_FIRST_LOGIN_STATUS);
    }

    @Override
    public void updateOwnerPassword(String oldPassword, String newPassword, String newPasswordAgain)
            throws UserException {
        IRequest request = RequestHelper.getCurrentRequest();
        Long accountId = request.getUserId();
        String userName = request.getUserName();

        validatePassword(newPassword, newPasswordAgain);

        if (StringUtils.isEmpty(oldPassword)) {
            throw new UserException(UserException.PASSWORD_NOT_EMPTY, null);
        }
        User tmp = new User();
        tmp.setUserId(accountId);
        User userInDB = selectByPrimaryKey(tmp);
        String pwd = userInDB.getPasswordEncrypted();
        if (!passwordManager.matches(oldPassword, pwd)) {
            throw new UserException(UserException.USER_PASSWORD_WRONG, null);
        }

        if (passwordManager.matches(newPassword, pwd)) {
            throw new UserException(UserException.USER_PASSWORD_SAME, null);
        }

        self().updatePassword(accountId, userName, newPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<User> batchUpdate(List<User> list) {
        Criteria criteria = new Criteria();
        criteria.update(User.FIELD_EMPLOYEE_ID, User.FIELD_EMAIL, User.FIELD_PHONE,
                User.FIELD_STATUS, User.FIELD_END_ACTIVE_DATE, User.FIELD_START_ACTIVE_DATE,
                User.FIELD_DESCRIPTION);
        criteria.updateExtensionAttribute();
        for (User user : list) {
            switch (user.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(user);
                    break;
                case DTOStatus.UPDATE:
                    userCache.remove(user.getUserName());
                    self().updateByPrimaryKeyOptions(user, criteria);
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(User user) {
        int ret = super.deleteByPrimaryKey(user);
        userRoleMapper.deleteByUserId(user.getUserId());
        userCache.remove(user.getUserName());
        return ret;
    }

    @Override
    public User convertToUser(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            User user = new User();
            user.setUserId(customUserDetails.getUserId());
            user.setUserName(customUserDetails.getUsername());
            user.setEmployeeId(customUserDetails.getEmployeeId());
            user.setEmployeeCode(customUserDetails.getEmployeeCode());
            Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
            List<String> roleCodes = new ArrayList<>();
            for (GrantedAuthority authority : authorities) {
                if (!authority.getAuthority().equals("ROLE_USER")) {
                    roleCodes.add(authority.getAuthority());
                }
            }
            user.setRoleCode(roleCodes);
            return user;
        }
        return selectByUserName(userDetails.getUsername());
    }

    @Override
    public PageInfo<User> pagingQueryUsersWithRoles(int page, int size, RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        PageInfo<User> userPageInfo = PageHelper.startPage(page, size).doSelectPageInfo(() -> userMapper.fuzzyQueryUsersByRoleAssignmentSearchDTO(roleAssignmentSearchDTO, ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam())));
        List<User> users = userPageInfo.getList();
        if (CollectionUtils.isNotEmpty(users)) {
            for (User user : users) {
                user.setUserRoles(roleService.selectRolesAssignToUserByUserId(user.getUserId()));
            }
        }
        return userPageInfo;
    }

    @Override
    public List<User> fuzzyQueryUsersByUserName(String userName) {
        List<User> activeUsers = new ArrayList<>();
        if (StringUtils.isNotEmpty(userName)) {
            User user = new User();
            user.setUserName(userName);
            List<User> users = userMapper.selectUsersOption(user);
            if (CollectionUtils.isNotEmpty(users)) {
                activeUsers = users;
            }
        }
        return activeUsers;
    }

    /**
     * 获取所有分配元素ID.
     * 1、功能分配 - 角色资源组件分配
     * 2、用户管理 - 用户资管组件分配
     *
     * @param userId 用户ID
     * @return 所有分配情况的权限分配元素ID (反逻辑)
     */
    @Override
    public List<Long> getAllAssignElementIds(Long userId, List<Long> roleIds) {
        List<Long> allAssignElementIds = new ArrayList<>();
        if (userId != null) {
            if (CollectionUtils.isEmpty(roleIds)) {
                return null;
            }
            //角色分配权限组件元素ID
            List<Long> roleAssignElementIds = getAllRoleAssignElementIds(roleIds);

            //用户分配权限组件元素ID
            ResourceItemAssign[] userAssigns = resourceItemAssignCache.getValue(ResourceItemAssign.ASSIGN_TYPE_USER + BaseConstants.UNDER_LINE + userId);
            if (ArrayUtils.isEmpty(userAssigns)) {
                //如果用户没有设置权限 返回角色合并后配置的权限
                if (CollectionUtils.isNotEmpty(roleAssignElementIds)) {
                    allAssignElementIds.addAll(roleAssignElementIds);
                }
                return allAssignElementIds;
            }
            for (ResourceItemAssign userAssign : userAssigns) {
                //如果用户配置了权限 则把用户权限为N的取出来
                if (UserConstants.NO.equalsIgnoreCase(userAssign.getEnable())) {
                    allAssignElementIds.add(userAssign.getElementId());
                }
            }
            // 遍历角色拥有的权限  添加用户没有覆盖的权限
            if (CollectionUtils.isNotEmpty(roleAssignElementIds)) {
                for (Long roleAssignElementId : roleAssignElementIds) {
                    boolean check = true;
                    for (ResourceItemAssign userAssign : userAssigns) {
                        if (roleAssignElementId.equals(userAssign.getElementId())) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        allAssignElementIds.add(roleAssignElementId);
                    }
                }
            }
        }
        return allAssignElementIds;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            User user = new User();
            BeanUtils.populate(user, body);
            if (StringUtils.isEmpty(sortname)) {
                user.setSortname("userId");
                user.setSortorder("DESC");
            } else {
                user.setSortname(sortname);
                user.setSortorder(isDesc ? "desc" : "asc");
            }
            return self().selectUsers(user, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<User> mutations(List<User> userList) {
        for (User user : userList) {
            switch (user.get__status()) {
                case DTOStatus.ADD:
                    self().batchUpdate(Collections.singletonList(user));
                    break;
                case DTOStatus.UPDATE:
                    self().batchUpdate(Collections.singletonList(user));
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(user);
                    break;
            }
        }
        return userList;
    }

    /**
     * 去除用户没有权限的菜单.
     *
     * @param menus           所有的菜单对象集合
     * @param userFunctionIds 用户所有功能ID
     * @return 用户拥有的菜单对象集合
     */
    private List<MenuItem> updateMenu(List<MenuItem> menus, Set<Long> userFunctionIds) {
        if (CollectionUtils.isEmpty(menus) || CollectionUtils.isEmpty(userFunctionIds)) {
            return new ArrayList<>();
        }
        Iterator<MenuItem> iterator = menus.iterator();
        while (iterator.hasNext()) {
            MenuItem menu = iterator.next();
            boolean hasChild = false;

            if (CollectionUtils.isNotEmpty(menu.getChildren())) {
                hasChild = CollectionUtils.isNotEmpty(updateMenu(menu.getChildren(), userFunctionIds));
            }

            boolean hasFunction = userFunctionIds.contains(menu.getId());

            if (!hasFunction && !hasChild) {
                iterator.remove();
            }
        }
        return menus;
    }

    /**
     * 合并用户所拥有的所有功能.
     *
     * @param allRoleIds 用户角色
     * @return 功能ID
     */
    private Set<Long> getUserFunctionIds(Long[] allRoleIds) {
        if (ArrayUtils.isEmpty(allRoleIds)) {
            return null;
        }
        Set<Long> userFunctionIds = new HashSet<>();
        for (Long roleId : allRoleIds) {
            Long[] functionIds = roleFunctionService.getRoleFunctionById(roleId);
            if (ArrayUtils.isNotEmpty(functionIds)) {
                userFunctionIds.addAll(Arrays.asList(functionIds));
            }
        }
        return userFunctionIds;
    }

    /**
     * 修改菜单是否勾选.
     *
     * @param menus               菜单
     * @param allAssignElementIds 所有分配元素ID
     * @return 勾选后的菜单
     */
    private List<MenuItem> updateMenuCheck(List<MenuItem> menus, List<Long> allAssignElementIds) {
        if (CollectionUtils.isEmpty(menus) || CollectionUtils.isEmpty(allAssignElementIds)) {
            return menus;
        }
        for (MenuItem menuItem : menus) {
            if (CollectionUtils.isNotEmpty(menuItem.getChildren())) {
                updateMenuCheck(menuItem.getChildren(), allAssignElementIds);
            }
            if (allAssignElementIds.contains(menuItem.getId())) {
                menuItem.setIschecked(Boolean.FALSE);
            }
        }
        return menus;
    }

    /**
     * 获取当前角色下的功能ID与总计的角色功能ID集合的交集.
     *
     * @param allRoleFunctionIds 总计的角色功能ID集合
     * @param roleFunctionIds    当前角色下的角色功能ID集合
     * @return 角色功能ID集合，取交集
     */
    private List<Long> getSameFunctionIds(Set<Long> allRoleFunctionIds, Set<Long> roleFunctionIds) {
        List<Long> sameFunctions = new ArrayList<>();
        for (Long allRoleFunction : allRoleFunctionIds) {
            if (roleFunctionIds.contains(allRoleFunction)) {
                sameFunctions.add(allRoleFunction);
            }
        }
        return sameFunctions;
    }

    /**
     * 合并角色权限组件元素ID.
     *
     * @param allFunctionElements 总计的分配权限组件元素ID集合
     * @param functionElementIds  角色分配时的元素ID
     * @return 元素ID(未分配的元素ID)
     */
    private List<Long> compareFunctionElements(List<Long> allFunctionElements, List<Long> functionElementIds) {
        //如果任意一个角色没有组件分配 直接返回
        if (CollectionUtils.isEmpty(allFunctionElements) || CollectionUtils.isEmpty(functionElementIds)) {
            return new ArrayList<>();
        }
        // 交集 获取两个角色都分配了的组件
        allFunctionElements.retainAll(functionElementIds);
        return allFunctionElements;
    }

    /**
     * 获取角色分配元素情况下的分配记录 获取Map<功能ID, 权限组件元素ID集合>.
     *
     * @param assigns 一个角色下的权限组件分配
     * @return 功能ID与对应的权限组件元素集合
     */
    private Map<Long, List<Long>> getFunctionElements(ResourceItemAssign[] assigns) {
        Map<Long, List<Long>> function = new HashMap<>(16);
        if (ArrayUtils.isNotEmpty(assigns)) {
            for (ResourceItemAssign assign : assigns) {
                List<Long> functionElements = function.get(assign.getFunctionId());
                if (CollectionUtils.isEmpty(functionElements)) {
                    functionElements = new ArrayList<>();
                    function.put(assign.getFunctionId(), functionElements);
                }
                functionElements.add(assign.getElementId());
            }
        }
        return function;
    }

    /**
     * 获取角色分配时的权限组件元素ID.
     *
     * @param roleIds 用户角色Id集合
     * @return 权限组件元素ID
     */
    private List<Long> getAllRoleAssignElementIds(List<Long> roleIds) {
        List<Long> allRoleAssignElementIds = new ArrayList<>();
        //key 功能ID value 功能对应的元素ID
        Map<Long, List<Long>> allFunctionElements = new HashMap<>(16);
        boolean firstRole = true;
        Set<Long> allRoleFunctionIds = new HashSet<>();
        for (Long roleId : roleIds) {
            //获取角色分配的所有功能ID
            Long[] roleFunctionIdList = roleFunctionCache.getValue(roleId.toString());
            //如果角色没有分配任何功能  即不会影响其他角色的功能组件分配 直接进入下一个循环
            if (ArrayUtils.isEmpty(roleFunctionIdList)) {
                continue;
            }
            Set<Long> roleFunctionIds = new HashSet<>();
            CollectionUtils.addAll(roleFunctionIds, roleFunctionIdList);

            //获取当前角色分配的元素 按功能划分  获取Map<功能Id,权限组件元素Id集合>
            Map<Long, List<Long>> functionElements = getFunctionElements(resourceItemAssignCache.getValue(ResourceItemAssign.ASSIGN_TYPE_ROLE + BaseConstants.UNDER_LINE + roleId));
            if (firstRole) {
                firstRole = false;
                allRoleFunctionIds = roleFunctionIds;
                allFunctionElements = functionElements;
                continue;
            }
            // 总计的功能ID与当前角色下的功能ID的相同功能ID
            List<Long> sameFunctionIds = getSameFunctionIds(allRoleFunctionIds, roleFunctionIds);

            //遍历其他角色所拥有的全部功能
            for (Long roleFunctionId : roleFunctionIds) {
                //如果与当前角色拥有不同的功能 将其加入 Map<功能, 组件分配集合>
                if (!sameFunctionIds.contains(roleFunctionId)) {
                    allFunctionElements.put(roleFunctionId, functionElements.get(roleFunctionId));
                }
            }

            /*
             * 两个角色具有相同的功能  将功能下面的组件分配合并   将其加入 Map<功能Id, 组件分配Id集合>中
             *
             * 可能有两个情况
             * 新增一个key:假如角色1有功能 F1，F2  只在F1下面分配了元素 此时Map中只有F1
             *             角色2有功能F2  角色1和角色2都有F2  合并后 此时Map中有F1和F2，新增了F2
             *
             * 覆盖原先的key：假如角色1有功能 F1，F2 在F1，F2下面都分配了元素 此时Map中有F1，F2，
             *             角色2有功能F2   角色1和角色2都有F2  合并后 此时Map中有F1和F2，F2被覆盖了
             *
             */
            for (Long sameFunctionId : sameFunctionIds) {
                List<Long> elements = compareFunctionElements(allFunctionElements.get(sameFunctionId), functionElements.get(sameFunctionId));
                allFunctionElements.put(sameFunctionId, elements);
            }
            allRoleFunctionIds.addAll(roleFunctionIds);
        }
        allFunctionElements.forEach((k, v) -> {
            if (v != null) {
                allRoleAssignElementIds.addAll(v);
            }
        });
        return allRoleAssignElementIds;
    }

    /**
     * 修改菜单.
     *
     * @param menus HTML资源菜单
     * @return 去掉服务端变量的HTML资源菜单
     */
    private List<MenuItem> updateResourceMenu(List<MenuItem> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return menus;
        }
        removeVariableMenu(menus);
        //如果删除服务器端变量后 该资源下没有了权限组件  将资源删除
        Iterator<MenuItem> iterator = menus.iterator();
        while (iterator.hasNext()) {
            MenuItem menu = iterator.next();
            if (CollectionUtils.isEmpty(menu.getChildren())) {
                iterator.remove();
            }
        }
        return menus;
    }

    /**
     * 用户管理 删除服务端变量菜单.
     *
     * @param menus 菜单
     */
    private void removeVariableMenu(List<MenuItem> menus) {
        Iterator<MenuItem> iterator = menus.iterator();
        while (iterator.hasNext()) {
            MenuItem menu = iterator.next();
            if (UserConstants.SERVER_VARIABLE.equalsIgnoreCase(menu.getText())) {
                iterator.remove();
                break;
            } else {
                if (CollectionUtils.isNotEmpty(menu.getChildren())) {
                    removeVariableMenu(menu.getChildren());
                }
            }
        }
    }
}
