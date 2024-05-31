package com.coral.test.spring.simple.core.opendoc.schema;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author huss
 * @version 1.0
 * @className ResultSchema
 * @description 响应结果
 * @date 2021/9/18 10:16
 */
@Schema(description = "响应结果")
public interface ResultSchema<T> {
    /**
     * 返回代码
     *
     * @return Integer
     */
    @Schema(description = "响应码")
    Integer getCode();


    /**
     * 响应信息
     *
     * @return String
     */
    @Schema(description = "响应信息")
    String getMessage();


    @Schema(description = "响应数据")
    /**
     * 响应数据
     * @return T
     */
    T getData();

    /**
     * 是否成功
     * @return
     */
    @Hidden
    boolean isSuccess();

}
