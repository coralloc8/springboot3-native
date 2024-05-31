package com.coral.test.spring.simple.repository;

import com.coral.test.spring.simple.model.EventLog;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * 事件日志
 *
 * @author huss
 * @date 2024/4/1 13:53
 * @packageName com.coral.test.spring.natives.repository
 * @className EventLogRepository
 */
@Repository
public interface EventLogRepository extends R2dbcRepository<EventLog, Long>, EventLogCustomRepository, IRepository {

    Mono<EventLog> findEventLogByEventLogId(@NonNull String eventLogId);


}
