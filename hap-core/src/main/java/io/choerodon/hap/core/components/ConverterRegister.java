package io.choerodon.hap.core.components;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.ConverterFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 注册自定义的BeanUtils转换器.
 *
 * @author qiang.zeng@hand-china.com
 * @since 2018/1/2
 */
@Component
public class ConverterRegister implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ConverterRegister.class);
    @Autowired
    private CustomDateConverter customDateConverter;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (BeanUtilsBean.getInstance().getConvertUtils().lookup(Date.class) instanceof ConverterFacade) {
            ConvertUtils.register(customDateConverter, java.util.Date.class);
            logger.debug("beanUtils register customDateConverter");
        }
        return bean;
    }
}
