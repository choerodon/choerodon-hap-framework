/*
 * #{copyright}#
 */
package io.choerodon.hap.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 修订,历史记录入口方法标记.
 *
 * @author shengyang.zhou@hand-china.com
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditEntry {
    String value() default "";
}