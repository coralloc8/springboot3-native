package com.coral.test.spring.natives.repository;

import com.coral.test.spring.natives.dto.PageInfo;
import com.coral.test.spring.natives.dto.req.EventLogPageQueryDTO;
import com.coral.test.spring.natives.dto.req.EventLogQueryDTO;
import com.coral.test.spring.natives.model.EventLog;
import com.coral.test.spring.natives.model.EventLogFullInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 事件日志
 *
 * @author huss
 * @date 2024/4/1 13:53
 * @packageName com.coral.test.spring.natives.repository
 * @className EventLogRepository
 */
interface EventLogCustomRepository extends IRepository {

    /**
     * 查询事件日志
     *
     * @param eventLogQuery 查询入参
     * @return
     */
    Flux<EventLog> findEventLogs(EventLogQueryDTO eventLogQuery);

    /**
     * 分页查询事件日志
     *
     * @param eventLogPageQuery 分页查询入参
     * @return PageInfo<EventLog>
     */
    Mono<PageInfo<EventLog>> findEventLogPages(EventLogPageQueryDTO eventLogPageQuery);

    /**
     * 查询事件完整日志
     *
     * @param eventLogQuery 查询入参
     * @return
     */
    Flux<EventLogFullInfo> findEventFullLogs(EventLogQueryDTO eventLogQuery);


}
