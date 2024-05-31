package com.coral.test.spring.natives.core.utils.validator;

import com.coral.test.spring.natives.core.exception.ValidateException;
import lombok.NonNull;

import java.util.List;

/**
 * @author huss
 * @version 1.0
 * @className IValidate
 * @description 数据校验
 * @date 2022/1/21 10:42
 */
public interface IValidate {

    /**
     * 数据校验
     */
    default void validate() {
        List<ValidatorErrorMsg> errors = ValidatorFactory.validate(this);
        if (!errors.isEmpty()) {
//            String error = errors.stream().map(ValidatorErrorMsg::getMessage).collect(Collectors.joining(";"));
            throw new ValidateException(errors);
        }
    }

    /**
     * 新增错误校验
     *
     * @param property
     * @param message
     */
    default void addErrorValidate(@NonNull String property, @NonNull String message) {
        ValidatorErrorMsg validatorErrorMsg = new ValidatorErrorMsg();
        validatorErrorMsg.setProperty(property);
        validatorErrorMsg.setMessageKey(property);
        validatorErrorMsg.setMessage(message);
        String error = validatorErrorMsg.getMessage();
        throw new ValidateException(error);
    }
}
