package io.choerodon.hap.account.service.impl;

import io.choerodon.hap.account.dto.UserRole;
import io.choerodon.hap.account.mapper.UserRoleMapper;
import io.choerodon.hap.account.service.IUserRoleService;
import io.choerodon.hap.cache.impl.UserCache;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 用户角色分配服务接口 - 实现.
 *
 * @author xiawang.liu@hand-china.com
 * @author lijian.yin@hand-china.com
 */

@Service
@Dataset("UserRole")
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole> implements IUserRoleService, IDatasetService<UserRole> {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserCache userCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserRole> batchUpdate(List<UserRole> list) {
        for (UserRole userRole : list) {
            switch (userRole.get__status()) {
                case DTOStatus.ADD:
                    self().insertSelective(userRole);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(userRole);
                    break;
                default:
                    break;
            }
            userCache.remove(userRole.getUserName());
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(UserRole record) {
        if (record.getSurId() != null) {
            return super.deleteByPrimaryKey(record);
        }
        int updateCount = userRoleMapper.deleteByRecord(record);
        checkOvn(updateCount, record);
        return updateCount;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            UserRole userRole = new UserRole();
            BeanUtils.populate(userRole, body);
            return userRoleMapper.selectUserRoles(userRole);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("user role");
        }
    }

    @Override
    public List<UserRole> mutations(List<UserRole> objs) {
        for (UserRole userRole : objs) {
            switch (userRole.get__status()) {
                case DTOStatus.ADD:
                    insertSelective(userRole);
                    break;
                case DTOStatus.UPDATE:
                    updateByPrimaryKeySelective(userRole);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(userRole);
                    break;
                default:
                    break;
            }
        }
        return objs;
    }
}