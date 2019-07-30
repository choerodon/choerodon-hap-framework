package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.system.dto.ProfileValue;
import io.choerodon.hap.system.mapper.ProfileValueMapper;
import io.choerodon.hap.system.service.IProfileValueService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @since 2018/11/28
 */

@Service
@Dataset("ProfileValue")
@Transactional(rollbackFor = Exception.class)
public class ProfileValueServiceImpl extends BaseServiceImpl<ProfileValue> implements IProfileValueService, IDatasetService<ProfileValue> {

    @Autowired
    private ProfileValueMapper profileValueMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        ProfileValue profileValue = new ProfileValue();
        profileValue.setProfileId((long) (Integer) body.get("profileId"));
        return profileValueMapper.selectProfileValues(profileValue);
    }

    @Override
    public List<ProfileValue> mutations(List<ProfileValue> profileValues) {
        for (ProfileValue profileValue : profileValues) {
            switch (profileValue.get__status()) {
                case DTOStatus.DELETE:
                    profileValueMapper.deleteByPrimaryKey(profileValue);
                    break;
                default:
                    break;
            }
        }
        return profileValues;
    }
}
