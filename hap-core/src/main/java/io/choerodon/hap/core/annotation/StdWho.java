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
 * 加在Service 的 方法参数上.
 * <p>
 * 表示这个参数(或者Collection)中的 dto 需要自动填充 who 字段
 *
 * @author shengyang.zhou@hand-china.com
 */
@Inherited
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface StdWho {
}