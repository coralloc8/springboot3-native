package com.coral.test.rule.core.utils.validator;



import com.coral.test.rule.core.utils.validator.ValidatorErrorMsg;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huss
 * @version 1.0
 * @className ValidatorFactory
 * @description 参数校验
 * @date 2021/3/24 15:27
 */
public class ValidatorFactory {
    /**
     * @Null 限制只能为null
     * @NotNull 限制必须不为null
     * @AssertFalse 限制必须为false
     * @AssertTrue 限制必须为true
     * @DecimalMax(value) 限制必须为一个不大于指定值的数字
     * @DecimalMin(value) 限制必须为一个不小于指定值的数字
     * @Digits(integer,fraction) 限制必须为一个小数，且整数部分的位数不能超过integer，小数部分的位数不能超过fraction
     * @Future 限制必须是一个将来的日期
     * @Max(value) 限制必须为一个不大于指定值的数字
     * @Min(value) 限制必须为一个不小于指定值的数字
     * @Past 限制必须是一个过去的日期
     * @Pattern(value) 限制必须符合指定的正则表达式
     * @Size(max,min) 限制字符长度必须在min到max之间
     * @Past 验证注解的元素值（日期类型）比当前时间早
     * @NotEmpty 验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）
     * @NotBlank 验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格
     * @Email 验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式
     */

    private static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class)
            //
            .configure()//
            .failFast(false)
            //
            .buildValidatorFactory()//
            .getValidator();

    /**
     * 获取校验器
     *
     * @return
     */
    public static Validator getValidator() {
        return VALIDATOR;

    }

    /**
     * 数据校验
     *
     * @param obj
     * @return
     */
    public static List<ValidatorErrorMsg> validate(Object obj) {
        return convert(getValidator().validate(obj));
    }

    /**
     * 校验后的数据转换成通用错误
     *
     * @param constraintViolations
     * @return
     */
    public static List<ValidatorErrorMsg> convert(Set<ConstraintViolation<Object>> constraintViolations) {
        if (constraintViolations == null || constraintViolations.isEmpty()) {
            return Collections.emptyList();
        }

        return constraintViolations.stream().map(con -> {
            ValidatorErrorMsg validatorErrorMsg = new ValidatorErrorMsg();
            validatorErrorMsg.setProperty(con.getPropertyPath().toString());
            validatorErrorMsg.setMessage(con.getMessage());
            validatorErrorMsg.setMessageKey(con.getPropertyPath().toString());
            return validatorErrorMsg;
        }).collect(Collectors.toList());

    }

}
