package com.coral.test.spring.natives.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NativeInfoDTO
 *
 * @author huss
 * @date 2024/4/18 18:03
 * @packageName com.coral.test.spring.natives.dto.res
 * @className NativeInfoDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Nacos Native 配置信息")
@Data
public class NativeInfoDTO {

    @Schema(description = "用户名称")
    private String username;

    @Schema(description = "性别")
    private String sex;


}
