package io.choerodon.hap.mail.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.mail.dto.MessageEmailProperty;
import io.choerodon.hap.mail.mapper.MessageEmailPropertyMapper;
import io.choerodon.hap.mail.service.IMessageEmailPropertyService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务器属性服务接口实现.
 *
 * @author qiang.zeng@hand-china.com
 */
@Service
@Dataset("EmailProperty")
public class MessageEmailPropertyServiceImpl extends BaseServiceImpl<MessageEmailProperty> implements IMessageEmailPropertyService, IDatasetService<MessageEmailProperty> {

    @Autowired
    private MessageEmailPropertyMapper messageEmailPropertyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByConfigId(MessageEmailProperty obj) {
        if (obj.getPropertyId() == null) {
            return 0;
        }
        int result = messageEmailPropertyMapper.deleteByPrimaryKey(obj);
        checkOvn(result, obj);
        return result;
    }


    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            MessageEmailProperty emailAccount = new MessageEmailProperty();
            BeanUtils.populate(emailAccount, body);
            return super.select(emailAccount, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.MessageEmailAccount", e);
        }
    }

    @Override
    public List<MessageEmailProperty> mutations(List<MessageEmailProperty> objs) {
        for (MessageEmailProperty emailProperty : objs) {
            if (emailProperty.get__status().equals(DTOStatus.DELETE)) {
                self().deleteByConfigId(emailProperty);
            }
        }
        return objs;
    }
}