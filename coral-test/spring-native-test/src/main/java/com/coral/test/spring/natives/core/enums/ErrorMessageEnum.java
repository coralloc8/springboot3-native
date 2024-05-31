package com.coral.test.spring.natives.core.enums;


import com.coral.test.spring.natives.core.exception.IErrorCode;

/**
 * @author
 */
public enum ErrorMessageEnum implements IErrorCode<Integer> {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 系统未知异常
     */
    SYS_ERR(-1, "系统未知异常"),

    /**
     * 不合法的参数
     */
    ILLEGAL_PARAMETER(10002, "不合法的参数"),
    /**
     * 不合法的请求格式
     */
    ILLEGAL_REQUEST_FORMAT(10003, "不合法的请求格式"),

    /**
     * 日期格式错误
     */
    DATE_FORMAT_ERROR(10004, "日期格式错误"),

    /**
     * 时间区间不合法
     */
    ILLEGAL_TIME_INTERVAL(10005, "时间区间不合法"),


    /**
     * 部分必填参数为空
     */
    REQUIRED_PARAMETERS_EMPTY(10007, "部分必填参数为空"),


    /**
     * 不支持此操作
     */
    OPERATION_NOT_SUPPORT(10009, "不支持此操作"),






    ;

    ErrorMessageEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
