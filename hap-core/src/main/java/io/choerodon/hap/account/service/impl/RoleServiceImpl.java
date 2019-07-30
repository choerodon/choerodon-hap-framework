package io.choerodon.hap.account.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.account.dto.Role;
import io.choerodon.hap.account.dto.RoleExt;
import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.RoleException;
import io.choerodon.hap.account.mapper.RoleMapper;
import io.choerodon.hap.account.mapper.UserRoleMapper;
import io.choerodon.hap.account.service.IRole;
import io.choerodon.hap.account.service.IRoleService;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.cache.impl.UserCache;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.function.service.IRoleFunctionService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色服务接口 - 实现类.
 *
 * @author shengyang.zhou@hand-china.com
 * @author njq.niu@hand-china.com
 */
@Service
@Dataset("AccountRole")
public class RoleServiceImpl extends BaseServiceImpl<Role> implements IRoleService, IDatasetService<Role> {

    private static final String CACHE_ROLE_CODE = "role";

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private IRoleFunctionService roleFunctionService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserCache userCache;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Role> selectRoles(Role role, int pageNum, int pageSize) {
        Criteria criteria = new Criteria(role);
        criteria.where(Role.FIELD_ROLE_ID, new WhereField(Role.FIELD_ROLE_CODE, Comparison.LIKE),
                new WhereField(Role.FIELD_ROLE_NAME, Comparison.LIKE),
                new WhereField(Role.FIELD_ROLE_DESCRIPTION, Comparison.LIKE));
        return super.selectOptions(role, criteria, pageNum, pageSize);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    @SuppressWarnings("unchecked")
    public List<IRole> selectRoleNotUserRoles(RoleExt roleExt, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return (List) roleMapper.selectRoleNotUserRoles(roleExt);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    @SuppressWarnings("unchecked")
    public List<IRole> selectRolesByUser(User user) {
        return (List) roleMapper.selectUserRoles(user.getUserId());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    @SuppressWarnings("unchecked")
    public List<IRole> selectUserRolesByUserId(Long userId) {
        return (List) roleMapper.selectUserRolesByUserId(userId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void checkUserRoleExists(Long userId, Long roleId) throws RoleException {
        if (roleMapper.selectUserRoleCount(userId, roleId) != 1) {
            throw new RoleException(RoleException.MSG_INVALID_USER_ROLE, RoleException.MSG_INVALID_USER_ROLE, null);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public IRole selectRoleByCode(String roleCode) {
        IRole role = getRoleCache().getValue(roleCode);
        if (role == null) {
            Role record = new Role();
            record.setRoleCode(roleCode);
            role = roleMapper.selectOne(record);
            if (role != null) {
                getRoleCache().setValue(roleCode, role);
            }
        }
        return role;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<IRole> selectActiveRolesByUser(User user) {
        if (null == user.getRoleCode()) {
            user = userService.selectByUserName(user.getUserName());
        }

        List<IRole> roles = new ArrayList<>();
        for (String roleCode : user.getRoleCode()) {
            roles.add(selectRoleByCode(roleCode));
        }

        return roles.stream().filter(IRole::isActive).collect(Collectors.toList());
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Role record) {
        int ret = super.deleteByPrimaryKey(record);
        userRoleMapper.deleteByRoleId(record.getRoleId());
        roleFunctionService.removeRoleFunctionByRoleId(record.getRoleId());
        getRoleCache().remove(record.getRoleCode());
        userCache.clearAll();
        return ret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateByPrimaryKey(@StdWho Role record) {
        int count = roleMapper.updateByPrimaryKey(record);
        checkOvn(count, record);
        getRoleCache().remove(record.getRoleCode());
        getRoleCache().setValue(record.getRoleCode(), record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role insertSelective(Role record) {
        int count = roleMapper.insertSelective(record);
        checkOvn(count, record);
        getRoleCache().setValue(record.getRoleCode(), record);
        return record;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Role role = new Role();
            BeanUtils.populate(role, body);
            role.setSortname(sortname);
            role.setSortorder(isDesc ? "desc" : "asc");
            return self().selectRoles(role, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<Role> mutations(List<Role> objs) {
        for (Role role : objs) {
            switch (role.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(role);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(role);
                    break;
                case DTOStatus.UPDATE:
                    self().updateByPrimaryKey(role);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }

    /**
     * 获取角色redis缓存.
     *
     * @return 角色redis缓存
     */
    private Cache<IRole> getRoleCache() {
        return cacheManager.getCache(CACHE_ROLE_CODE);
    }
}
