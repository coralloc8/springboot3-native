package com.coral.test.spring.simple.model;

import com.coral.test.spring.simple.enums.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件日志完整信息
 *
 * @author huss
 * @date 2024/4/1 14:34
 * @packageName com.coral.test.spring.natives.dto.res
 * @className EventLogFullInfo
 */
@Data
public class EventLogFullInfo {
    /**
     * 事件日志ID
     */
    private String eventLogId;

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件key
     */
    private String eventKey;

    /**
     * 事件来源
     */
    private String eventSource;

    /**
     * 项目标识
     */
    private String project;

    /**
     * 方法编码
     */
    private String funcCode;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 事件状态
     */
//    private EventStatus status;
}
