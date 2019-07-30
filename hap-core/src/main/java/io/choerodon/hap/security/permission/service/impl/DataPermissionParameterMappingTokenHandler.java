package io.choerodon.hap.security.permission.service.impl;

import io.choerodon.web.core.IRequest;
import org.apache.ibatis.parsing.TokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/9/5.
 */
public class DataPermissionParameterMappingTokenHandler implements TokenHandler {

    private IRequest iRequest;

    public DataPermissionParameterMappingTokenHandler(IRequest iRequest) {
        this.iRequest = iRequest;
    }

    private static Logger log = LoggerFactory.getLogger(DataPermissionParameterMappingTokenHandler.class);

    @Override
    public String handleToken(String content) {
        String value;
        value = getValueByBean(content, iRequest);
        if (!"".equals(value)) {
            return value;
        }
        return "";
    }

    public String getValueByBean(String key, Object object) {
        String value = "";
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(key)) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(object);
                    value = getValue(fieldValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return value;
    }


    //类型处理demo
    private String getValue(Object fieldValue) {
        String value = "";
        if (fieldValue instanceof List) {
            List fe = (List) fieldValue;
            for (int i = 0; i < fe.size(); i++) {
                String temp = fe.get(i).toString();
                if (fe.get(i) instanceof String) {
                    temp = "'" + temp + "'";
                }
                value += temp;
                if (i != fe.size() - 1) {
                    value += ",";
                }
            }
        } else {
            value = fieldValue.toString();
            if (fieldValue instanceof String) {
                value = "'" + value + "'";
            }
        }
        return value;
    }


}
