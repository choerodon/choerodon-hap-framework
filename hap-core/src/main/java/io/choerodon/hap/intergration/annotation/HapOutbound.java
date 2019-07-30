/*
 * #{copyright}#
 */

package io.choerodon.hap.intergration.annotation;

import java.lang.annotation.*;

/**
 * 出站请求引用此注解
 * @author xiangyu.qi@hand-china.com
 */
@Inherited
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HapOutbound {

    String apiName() default "";

    String sysName() default "";
}