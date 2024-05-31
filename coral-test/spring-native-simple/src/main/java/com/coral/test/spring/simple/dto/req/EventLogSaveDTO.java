package com.coral.test.spring.simple.dto.req;

import com.coral.test.spring.simple.enums.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件日志保存
 *
 * @author huss
 * @date 2024/4/1 14:35
 * @packageName com.coral.test.spring.natives.dto.req
 * @className EventLogSaveDTO
 */
@Schema(description = "事件日志保存")
@Data
@Valid
public class EventLogSaveDTO {
    /**
     * 事件ID
     */
    @Schema(description = "事件ID")
    @NotBlank(message = "事件ID不能为空")
    private String eventId;

    /**
     * 项目标识
     */
    @Schema(description = "项目标识")
    @NotBlank(message = "项目标识不能为空")
    private String project;

    /**
     * 方法编码
     */
    @Schema(description = "方法编码")
    @NotBlank(message = "方法编码不能为空")
    private String funcCode;

    /**
     * 发送时间
     */
    @Schema(description = "发送时间")
    @NotBlank(message = "发送时间不能为空")
    private LocalDateTime sendTime;

    /**
     * 接收时间
     */
    @Schema(description = "接收时间")
    private LocalDateTime receiveTime;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private String responseData;

    /**
     * 错误编码
     */
    @Schema(description = "错误编码")
    private String errorCode;

    /**
     * 错误消息
     */
    @Schema(description = "错误消息")
    private String errorMsg;

    /**
     * 事件状态
     */
    @Schema(description = "事件状态")
    @NotBlank(message = "事件状态不能为空")
    private EventStatus status;
}
