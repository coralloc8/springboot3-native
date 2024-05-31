package com.coral.test.spring.natives.model;

import com.coral.test.spring.natives.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author huss
 * @version 1.0
 * @className Event
 * @description 事件
 * @date 2022/11/15 14:15
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table("event_log")
public class EventLog implements Serializable {

    @Id
    private Long id;

    /**
     * 事件日志ID
     */
    private String eventLogId;

    /**
     * 事件ID
     */
    private String eventId;

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
     * 接收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 响应数据
     */
    private String responseData;

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 事件状态
     */
    private EventStatus status;


}
