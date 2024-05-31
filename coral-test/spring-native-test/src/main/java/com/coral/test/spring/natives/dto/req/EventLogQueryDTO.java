package com.coral.test.spring.natives.dto.req;

import com.coral.test.spring.natives.enums.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;

/**
 * 事件日志查询
 *
 * @author huss
 * @date 2024/4/1 14:35
 * @packageName com.coral.test.spring.natives.dto.req
 * @className EventLogQueryDTO
 */
@Schema(description = "事件日志查询")
@Data
public class EventLogQueryDTO {

    /**
     * 事件日志id
     */
    @Schema(description = "事件日志id")
    private String eventLogId;

    /**
     * 事件来源
     */
    @Schema(description = "事件来源")
    private String eventSource;

    /**
     * 事件名称
     */
    @Schema(description = "事件名称")
    private String eventName;

    /**
     * 事件关键字
     */
    @Schema(description = "事件关键字")
    private String eventKey;

    /**
     * 项目名
     */
    @Schema(description = "项目名")
    private String project;

    /**
     * 功能码
     */
    @Schema(description = "功能码集合")
    private Collection<String> funcCodes;

    @Schema(description = "事件状态")
    private EventStatus status;
}
