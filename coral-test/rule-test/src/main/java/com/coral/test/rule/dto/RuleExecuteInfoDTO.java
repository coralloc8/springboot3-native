package com.coral.test.rule.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 规则执行信息
 *
 * @author huss
 * @date 2024/7/2 10:45
 * @packageName com.coral.test.rule.dto.req
 * @className RuleExecuteInfoDTO
 */
@ToString(callSuper = true)
@Schema(description = "规则执行信息")
@Valid
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RuleExecuteInfoDTO {

    @NotBlank(message = "说明")
    private String desc;

    @NotBlank(message = "接口服务名")
    private String apiService;

    @NotBlank(message = "接口关键字")
    private String apiKey;

    @NotBlank(message = "api接口地址不能为空")
    @Schema(description = "api接口地址")
    private String url;

    @NotBlank(message = "请求方式不能为空")
    @Schema(description = "请求方式")
    private String method;

    @NotBlank(message = "请求内容类型不能为空")
    @Schema(description = "请求内容类型")
    private String contentType;

    @Schema(description = "参数")
    private Object data;

    public String getContentType() {
        return StringUtils.isBlank(contentType) ? "application/x-www-form-urlencoded" : contentType;
    }
}
