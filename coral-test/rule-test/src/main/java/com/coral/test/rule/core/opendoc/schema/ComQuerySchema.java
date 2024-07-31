package com.coral.test.rule.core.opendoc.schema;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author huss
 * @version 1.0
 * @className ComQuerySchema
 * @description 关键字搜索
 * @date 2021/9/18 10:07
 */
@Schema(description = "关键字搜索")
public interface ComQuerySchema {

    @Schema(description = "查询关键字")
    /**
     *  查询关键字
     * @return String
     */
    String getKeyword();
}
