package io.choerodon.hap.account.service.impl;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.exception.UserException;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Dataset("AccountPassword")
public class AccountPasswordServiceImpl implements IDatasetService<User> {
    @Autowired
    private IUserService userService;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        return null;
    }

    @Override
    public List<User> mutations(List<User> objs) {
        for (User user : objs) {
            if (user.get__status().equals(DTOStatus.ADD)) {
                try {
                    userService.resetPassword( user, user.getPasswordAgain());
                } catch (UserException e) {
                    throw new DatasetException(e.getDescriptionKey(), e.getParameters());
                }
            }
        }
        return objs;
    }
}
