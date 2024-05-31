package com.coral.test.spring.natives.core.exception;


import com.coral.test.spring.natives.core.utils.StrFormatter;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huss
 * @version 1.0
 * @className BaseServiceException
 * @description 基础服务异常
 * @date 2021/3/24 16:34
 */
public class BaseServiceException extends RuntimeException implements IException {
    /**
     * 错误信息
     */
    private IErrorCode error;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 错误信息中的动态值
     */
    private String[] params;


    private BaseServiceException() {
        super();
    }

    /**
     * @param error  错误信息
     * @param params 错误信息中的动态参数值
     */
    public BaseServiceException(IErrorCode error, String... params) {
        this(error, "", params);
    }

    public BaseServiceException(IErrorCode error, String message) {
        this(error, message, null);
    }

    public BaseServiceException(IErrorCode error, String message, String... params) {
        super(StringUtils.isBlank(message) ? StrFormatter.format(error.getMessage(), params) : message);
        this.error = error;
        this.message = message;
        this.params = params;
    }




    @Override
    public String getErrCode() {
        return error.getCode().toString();
    }

    @Override
    public String getErrMessage() {
        String message = this.message;
        if (StringUtils.isBlank(message)) {
            message = StrFormatter.format(error.getMessage(), params);
        }
        return StringUtils.isNotBlank(message) ? message : "";
    }

    @Override
    public Throwable getError() {
        return this;
    }
}
