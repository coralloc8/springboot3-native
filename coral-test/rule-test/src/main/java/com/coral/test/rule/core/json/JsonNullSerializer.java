package com.coral.test.rule.core.json;

import java.lang.annotation.*;

/**
 * @author huss
 * @version 1.0
 * @className NullConversion
 * @description NullSerializer 序列化 会针对各种类型做初始化处理
 * @date 2021/4/9 9:27
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonNullSerializer {
    boolean value() default true;
}
