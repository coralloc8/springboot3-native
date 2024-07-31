package com.coral.test.rule.core.opendoc.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huss
 * @version 1.0
 * @className EnumSchema
 * @description 枚举
 * @date 2021/9/14 19:26
 */
@Schema(description = "枚举")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnumSchema {

    @Schema(description = "编码")
    private String code;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "描述")
    private String description;
}
