package com.coral.test.spring.simple.core.response;


import com.coral.test.spring.simple.core.enums.ErrorMessageEnum;
import com.coral.test.spring.simple.core.exception.IErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author huss
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefResult<T> implements IResult<T> {

    private Integer code;

    private String message;

    private T data;


    @JsonIgnore
    public boolean isSuccess() {
        return Objects.nonNull(getCode()) && ErrorMessageEnum.SUCCESS.getCode().equals(getCode());
    }

    public static <T> IResult<T> success(T data) {
        return new DefResult<>(ErrorMessageEnum.SUCCESS.getCode(), ErrorMessageEnum.SUCCESS.getMessage(), data);
    }

    public static <T> IResult<T> error(Integer errCode, String errMessage) {
        return new DefResult<>(errCode, errMessage, null);
    }

    public static <T> IResult<T> error() {
        return new DefResult<>(ErrorMessageEnum.SYS_ERR.getCode(), ErrorMessageEnum.SYS_ERR.getMessage(), null);
    }

    public static <T> IResult<T> error(IErrorCode<Integer> errorCode, T data) {
        return new DefResult<>(errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static <T> IResult<T> error(T data) {
        return new DefResult<>(ErrorMessageEnum.SYS_ERR.getCode(), ErrorMessageEnum.SYS_ERR.getMessage(), data);
    }

    public static <T> IResult<T> error(IErrorCode<Integer> errorCode) {
        return new DefResult<>(errorCode.getCode(), errorCode.getMessage(), null);
    }


}
