package com.coral.test.spring.natives.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 事件日志分页查询
 *
 * @author huss
 * @date 2024/4/1 14:35
 * @packageName com.coral.test.spring.natives.dto.req
 * @className EventLogQueryDTO
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "事件日志分页查询")
@Valid
@Data
public class EventLogPageQueryDTO extends EventLogQueryDTO {

    @Schema(description = "分页信息")
    @NotNull(message = "分页信息不能为空")
    private PageQueryDTO page;
}
