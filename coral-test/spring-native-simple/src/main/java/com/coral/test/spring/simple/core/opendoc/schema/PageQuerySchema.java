package com.coral.test.spring.simple.core.opendoc.schema;

import com.coral.test.spring.simple.core.enums.GlobalOrder;
import com.coral.test.spring.simple.core.opendoc.constants.DefConstant;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author huss
 * @version 1.0
 * @className PageQuerySchema
 * @description 分页查询
 * @date 2021/9/18 10:10
 */
@Schema(description = "分页查询")
public interface PageQuerySchema {

    /**
     * 分页页数
     */
    @Schema(description = "分页页数", requiredMode = Schema.RequiredMode.REQUIRED, example = DefConstant.PAGE_CURRENT, defaultValue = DefConstant.PAGE_CURRENT)
    /**
     * 分页页数
     * @return Integer
     */
    Integer getPageNum();

    /**
     * 每页显示数量
     */
    @Schema(description = "每页显示数量", requiredMode = Schema.RequiredMode.REQUIRED, example = DefConstant.PAGE_SIZE, defaultValue = DefConstant.PAGE_SIZE)
    /**
     * 分页页数
     * @return Integer
     */
    Integer getPageSize();

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = DefConstant.ORDER_KEY, defaultValue = DefConstant.ORDER_KEY)
    /**
     * 排序字段
     * @return String
     */
    String getSortField();

    @Schema(description = "排序方式", example = DefConstant.ORDER_DESC, defaultValue = DefConstant.ORDER_DESC)
    /**
     *排序方式
     * @return GlobalOrder
     */
    GlobalOrder getOrder();
}
