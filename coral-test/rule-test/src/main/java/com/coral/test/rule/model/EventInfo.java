package com.coral.test.rule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 事件信息
 *
 * @author huss
 * @date 2024/4/8 17:11
 * @packageName com.coral.test.spring.natives.model
 * @className EventInfo
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table("event_info")
public class EventInfo {

    @Id
    private Long id;

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
     * 事件入参
     */
    private String eventRequest;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
