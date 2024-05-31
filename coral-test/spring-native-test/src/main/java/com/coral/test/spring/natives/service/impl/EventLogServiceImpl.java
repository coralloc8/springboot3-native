package com.coral.test.spring.natives.service.impl;

import com.coral.test.spring.natives.convert.EventLogConvert;
import com.coral.test.spring.natives.core.utils.id.IdCreatorFactory;
import com.coral.test.spring.natives.dto.PageInfo;
import com.coral.test.spring.natives.dto.req.EventLogPageQueryDTO;
import com.coral.test.spring.natives.dto.req.EventLogQueryDTO;
import com.coral.test.spring.natives.dto.req.EventLogSaveDTO;
import com.coral.test.spring.natives.dto.res.EventLogFullInfoDTO;
import com.coral.test.spring.natives.dto.res.EventLogInfoDTO;
import com.coral.test.spring.natives.model.EventLog;
import com.coral.test.spring.natives.repository.EventLogRepository;
import com.coral.test.spring.natives.service.EventLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 事件日志服务
 *
 * @author huss
 * @date 2024/4/1 13:53
 * @packageName com.coral.test.spring.natives.service.impl
 * @className EventLogServiceImpl
 */
@Slf4j
@Service
public class EventLogServiceImpl implements EventLogService {

    @Resource
    private EventLogRepository eventLogRepository;

    @Override
    public Mono<EventLogInfoDTO> findEventLog(String eventLogId) {
        return eventLogRepository.findEventLogByEventLogId(eventLogId)
                .flatMap(log -> Mono.justOrEmpty(EventLogConvert.INSTANCE.convertEventLogInfoDTO(log)));
    }

    @Override
    public Flux<EventLogInfoDTO> findEventLogs(EventLogQueryDTO eventLogQuery) {
        return eventLogRepository.findEventLogs(eventLogQuery)
                .flatMap(log -> Mono.justOrEmpty(EventLogConvert.INSTANCE.convertEventLogInfoDTO(log)));
    }


    @Override
    public Flux<EventLogFullInfoDTO> findEventFullLogs(EventLogQueryDTO eventLogQuery) {
        return eventLogRepository.findEventFullLogs(eventLogQuery)
                .flatMap(log -> Mono.justOrEmpty(EventLogConvert.INSTANCE.convertEventLogFullInfoDTO(log)));
    }

    @Override
    public Mono<EventLogInfoDTO> saveEventLog(EventLogSaveDTO eventLogSave) {
        EventLog eventLog = EventLogConvert.INSTANCE.convertEventLog(eventLogSave);
        eventLog.setEventLogId(IdCreatorFactory.createIncrementId().toString());
        eventLog.setReceiveTime(LocalDateTime.now());
        return eventLogRepository.save(eventLog)
                .flatMap(log -> Mono.justOrEmpty(EventLogConvert.INSTANCE.convertEventLogInfoDTO(log)));
    }

    @Override
    public Mono<PageInfo<EventLogInfoDTO>> findEventLogPages(EventLogPageQueryDTO eventLogPageQuery) {
        return eventLogRepository.findEventLogPages(eventLogPageQuery)
                .map(info -> {
                    List<EventLogInfoDTO> logs = info.getRecords().stream()
                            .map(EventLogConvert.INSTANCE::convertEventLogInfoDTO)
                            .collect(Collectors.toList());
                    return info.copy(logs);
                });
    }
}
