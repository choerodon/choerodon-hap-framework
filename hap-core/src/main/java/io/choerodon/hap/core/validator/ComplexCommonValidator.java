package io.choerodon.hap.core.validator;

import io.choerodon.hap.system.dto.DTOClassInfo;
import io.choerodon.web.validator.FieldErrorWithBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import tk.mybatis.mapper.entity.EntityField;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.List;
import java.util.Set;

/**
 * 通用校验器.
 * <p>
 * 可以校验的类型:
 * <ol>
 * <li>单个对象</li>
 * <li>List(拆解后,调用1)</li>
 * <li>对象中包含的 Children 元素
 * <p>
 * Children的类型可以是 List,也可以是单个对象</li>
 * </ol>
 *
 * @author shengyang.zhou@hand-china.com
 */
@Component(value = "validator")
public class ComplexCommonValidator extends OptionalValidatorFactoryBean {

    private Logger logger = LoggerFactory.getLogger(ComplexCommonValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        Validator targetValidator = getValidator();
        if (targetValidator != null) {
            if (target instanceof List) {
                // 如果是 List,则拆解,递归
                for (Object obj : (List) target) {
                    validate(obj, errors);
                }
            } else {
                // 单个对象,则调用方法进行校验
                processConstraintViolations(targetValidator.validate(target), errors);
                for (EntityField f : DTOClassInfo.getChildrenFields(target.getClass())) {
                    try {
                        Object children = PropertyUtils.getProperty(target, f.getName());
                        if (children != null) {
                            validate(children, errors);
                        }
                    } catch (Exception e) {
                        if (logger.isErrorEnabled()) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }

        }
    }

    protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String field = determineField(violation);
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
                    String errorCode = determineErrorCode(cd);
                    Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, cd);
                    if (errors instanceof BindingResult) {
                        // Can do custom FieldError registration with invalid
                        // value from ConstraintViolation,
                        // as necessary for Hibernate Validator compatibility
                        // (non-indexed set path in field)
                        BindingResult bindingResult = (BindingResult) errors;
                        String nestedField = bindingResult.getNestedPath() + field;
                        if ("".equals(nestedField)) {
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                            bindingResult.addError(new ObjectError(errors.getObjectName(), errorCodes, errorArgs,
                                    violation.getMessage()));
                        } else {
                            Object rejectedValue = getRejectedValue(field, violation, bindingResult);
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
                            // 改成自定义的 Error 对象,支持附加 targetBean 属性
                            FieldErrorWithBean fieldErrorWithBean = new FieldErrorWithBean(errors.getObjectName(),
                                    nestedField, rejectedValue, false, errorCodes, errorArgs, violation.getMessage());
                            fieldErrorWithBean.setTargetBean(violation.getRootBean());
                            bindingResult.addError(fieldErrorWithBean);
                        }
                    } else {
                        // got no BindingResult - can only do standard
                        // rejectValue call
                        // with automatic extraction of the current field value
                        errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                    }
                } catch (NotReadablePropertyException ex) {
                    StringBuilder sb = new StringBuilder(256);
                    sb.append("JSR-303 validated property '").append(field);
                    sb.append("' does not have a corresponding accessor for Spring data binding - ");
                    sb.append("check your DataBinder's configuration (bean property versus direct field access)");
                    throw new IllegalStateException(sb.toString(), ex);
                }
            }
        }
    }
}
