package com.coral.test.spring.simple.core.exception;

/**
 * @author huss
 * @version 1.0
 * @className IException
 * @description 通用错误
 * @date 2021/3/24 16:35
 */
public interface IException {

    /**
     * 获取错误码
     *
     * @return
     */
    String getErrCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getErrMessage();

    /**
     * 获取错误异常
     *
     * @return
     */
    Throwable getError();

}
