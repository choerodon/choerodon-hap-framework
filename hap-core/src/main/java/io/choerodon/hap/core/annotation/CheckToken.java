/*
 * #{copyright}#
 */

package io.choerodon.hap.core.annotation;

import java.lang.annotation.*;

/**
 * 防篡改token启用标记.
 *
 * @author qiang.zeng@hand-china.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckToken {

    /**
     * 是否进行防篡改token校验.
     *
     * @return true or false
     */
    boolean check() default true;
}