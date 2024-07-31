package com.coral.test.rule.core.utils.validator;

import com.coral.test.rule.core.utils.validator.EnumValueValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author huss
 * @version 1.0
 * @className EnumValue
 * @description 自定义校验
 * @date 2021/11/17 17:56
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {EnumValueValidator.class})
public @interface EnumValue {

    //默认错误消息
    String message() default "必须为指定值";

    /**
     * 枚举类
     *
     * @return
     */
    Class<? extends Enum<?>> enumClass();

    //分组
    Class<?>[] groups() default {};

    //指定多个时使用
    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        EnumValue[] value();
    }

}