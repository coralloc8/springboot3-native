package com.coral.test.spring.natives.core.exception;

/**
 * @author huss
 * @version 1.0
 * @className IErrorMessage
 * @description 错误信息
 * @date 2021/3/24 16:35
 */
public interface IErrorCode<K> {

    /**
     * 获取错误码
     *
     * @return
     */
    K getCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getMessage();

}
