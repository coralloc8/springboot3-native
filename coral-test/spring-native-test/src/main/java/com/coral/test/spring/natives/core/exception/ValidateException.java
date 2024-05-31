package com.coral.test.spring.natives.core.exception;

import com.coral.test.spring.natives.core.enums.ErrorMessageEnum;
import com.coral.test.spring.natives.core.utils.validator.ValidatorErrorMsg;
import lombok.Getter;

import java.util.List;

public class ValidateException extends RuntimeException {

    @Getter
    private List<ValidatorErrorMsg> details;

    public ValidateException(List<ValidatorErrorMsg> details) {
        super(ErrorMessageEnum.ILLEGAL_PARAMETER.getMessage());
        this.details = details;
    }

    public ValidateException(String message) {
        super(message);
    }

    public ValidateException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
