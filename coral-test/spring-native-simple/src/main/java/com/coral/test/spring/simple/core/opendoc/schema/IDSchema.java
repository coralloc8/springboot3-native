package com.coral.test.spring.simple.core.opendoc.schema;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author huss
 * @version 1.0
 * @className IDCollection
 * @description ID集合
 * @date 2021/9/18 10:14
 */
@Schema(description = "ID集合")
public interface IDSchema<T> {

    @Schema(description = "id列表")
    /**
     * id列表
     * @return List
     */
    List<T> getIds();
}
