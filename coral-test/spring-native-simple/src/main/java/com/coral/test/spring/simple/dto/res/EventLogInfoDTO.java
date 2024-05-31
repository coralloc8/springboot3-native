package com.coral.test.spring.simple.dto.res;

import com.coral.test.spring.simple.enums.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件日志信息
 *
 * @author huss
 * @date 2024/4/1 14:34
 * @packageName com.coral.test.spring.natives.dto.res
 * @className EventLogInfo
 */
@Schema(description = "事件日志信息")
@Data
public class EventLogInfoDTO {
    /**
     * 事件日志ID
     */
    @Schema(description = "事件日志ID")
    private String eventLogId;

    /**
     * 事件ID
     */
    @Schema(description = "事件ID")
    private String eventId;

    /**
     * 项目标识
     */
    @Schema(description = "项目标识")
    private String project;

    /**
     * 方法编码
     */
    @Schema(description = "方法编码")
    private String funcCode;

    /**
     * 发送时间
     */
    @Schema(description = "发送时间")
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
    private EventStatus status;
}
