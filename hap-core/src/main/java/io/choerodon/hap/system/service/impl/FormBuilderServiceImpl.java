package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.system.dto.Form;
import io.choerodon.hap.system.service.IFormBuilderService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.redis.Cache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FormBuilderServiceImpl extends BaseServiceImpl<Form> implements IFormBuilderService {

    @Autowired
    @Qualifier("formCache")
    private Cache formCache;

    @Override
    @SuppressWarnings("unchecked")
    public List<Form> batchUpdate(List<Form> forms) {
        for (Form form : forms) {
            form.setCode(StringUtils.upperCase(form.getCode()));
            if (form.getFormId() == null) {
                self().insertSelective(form);
            } else if (form.getFormId() != null) {
                self().updateByPrimaryKey(form);

            }
            formCache.setValue(form.getCode(), form);
        }
        return forms;
    }

    public int batchDelete(List<Form> forms) {
        int c = 0;
        for (Form form : forms) {
            c += self().deleteByPrimaryKey(form);
            formCache.remove(form.getCode());
        }
        return c;
    }
}