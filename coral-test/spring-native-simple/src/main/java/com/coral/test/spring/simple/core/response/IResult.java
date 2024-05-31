package com.coral.test.spring.simple.core.response;


/**
 * @author huss
 */
public interface IResult<T> {

    /**
     * 返回代码
     *
     * @return Integer
     */
    Integer getCode();

    /**
     * 响应信息
     *
     * @return String
     */
    String getMessage();


    /**
     * 响应数据
     * @return T
     */
    T getData();

}
