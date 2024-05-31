package com.coral.test.spring.natives.dto.res;

import com.coral.test.spring.natives.core.opendoc.constants.DefConstant;
import com.coral.test.spring.natives.enums.EventStatus;
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
public class EventLogFullInfoDTO {
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
     * 事件名称
     */
    @Schema(description = "事件名称")
    private String eventName;

    /**
     * 事件key
     */
    @Schema(description = "事件key")
    private String eventKey;

    /**
     * 事件来源
     */
    @Schema(description = "事件来源")
    private String eventSource;

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
     * 事件状态
     */
    @Schema(description = "事件状态", format = DefConstant.DOC_FORMAT_ENUM)
    private EventStatus status;
}
