package com.coral.test.spring.simple.service;


import com.coral.test.spring.simple.dto.PageInfo;
import com.coral.test.spring.simple.dto.req.EventLogPageQueryDTO;
import com.coral.test.spring.simple.dto.req.EventLogQueryDTO;
import com.coral.test.spring.simple.dto.req.EventLogSaveDTO;
import com.coral.test.spring.simple.dto.res.EventLogFullInfoDTO;
import com.coral.test.spring.simple.dto.res.EventLogInfoDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 事件日志服务
 *
 * @author huss
 * @date 2024/4/1 13:52
 * @packageName com.coral.test.spring.natives.service
 * @className EventLogService
 */
public interface EventLogService extends IService {

    /**
     * 根据事件ID查询事件日志信息
     *
     * @param eventLogId
     * @return
     */
    Mono<EventLogInfoDTO> findEventLog(String eventLogId);

    /**
     * 查询事件日志信息
     *
     * @param eventLogQuery
     * @return
     */
    Flux<EventLogInfoDTO> findEventLogs(EventLogQueryDTO eventLogQuery);


    /**
     * 查询事件日志完整信息
     *
     * @param eventLogQuery
     * @return
     */
    Flux<EventLogFullInfoDTO> findEventFullLogs(EventLogQueryDTO eventLogQuery);

    /**
     * 保存事件日志
     *
     * @param eventLogSave
     * @return
     */
    Mono<EventLogInfoDTO> saveEventLog(EventLogSaveDTO eventLogSave);

    /**
     * 分页查询事件日志
     *
     * @param eventLogPageQuery
     * @return
     */
    Mono<PageInfo<EventLogInfoDTO>> findEventLogPages(EventLogPageQueryDTO eventLogPageQuery);
}
