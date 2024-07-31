package com.coral.test.rule.core.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.Stream;

/**
 * @author huss
 * @version 1.0
 * @className EnumValueValidator
 * @description 枚举校验
 * @date 2021/11/17 17:58
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        return Stream.of(enumConstants).filter(e -> e.equals(value)).findAny().isPresent();
    }
}