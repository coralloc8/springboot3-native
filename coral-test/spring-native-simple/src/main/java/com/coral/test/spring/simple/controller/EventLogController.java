package com.coral.test.spring.simple.controller;

import com.coral.test.spring.simple.core.response.DefResult;
import com.coral.test.spring.simple.core.response.IResult;
import com.coral.test.spring.simple.dto.PageInfo;
import com.coral.test.spring.simple.dto.req.EventLogPageQueryDTO;
import com.coral.test.spring.simple.dto.req.EventLogQueryDTO;
import com.coral.test.spring.simple.dto.req.EventLogSaveDTO;
import com.coral.test.spring.simple.dto.res.EventLogFullInfoDTO;
import com.coral.test.spring.simple.dto.res.EventLogInfoDTO;
import com.coral.test.spring.simple.service.EventLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 事件日志
 *
 * @author huss
 * @date 2024/3/28 17:16
 * @packageName org.com.coral.test.spring.natives.controller
 * @className EventLogController
 */
@Slf4j
@RequestMapping("/event/logs")
@RestController
public class EventLogController implements EventLogApi {

    @Resource
    private EventLogService eventLogService;


    @GetMapping("/{eventLogId}")
    @Override
    public Mono<IResult<EventLogInfoDTO>> findEventLog(@PathVariable("eventLogId") String eventLogId) {
        // 能获取到上下文
//        return eventLogService.findEventLog(eventLogId)
//                .flatMap(info -> ReactiveHttpContextHolder.getRequest().map(req -> {
//                    log.info("#######################【headers2】:{}", req.getHeaders());
//                    return DefResult.success(info);
//                }));
        return eventLogService.findEventLog(eventLogId).map(DefResult::success);
    }

    @PostMapping("/search")
    @Override
    public Mono<IResult<List<EventLogInfoDTO>>> findEventLogs(@RequestBody EventLogQueryDTO eventLogQuery) {
        return eventLogService.findEventLogs(eventLogQuery).collectList().map(DefResult::success);
    }


    @PostMapping("/search/full")
    @Override
    public Mono<IResult<List<EventLogFullInfoDTO>>> findEventFullLogs(@RequestBody EventLogQueryDTO eventLogQuery) {
        return eventLogService.findEventFullLogs(eventLogQuery).collectList().map(DefResult::success);
    }


    @PostMapping
    @Override
    public Mono<IResult<EventLogInfoDTO>> saveEventLog(@RequestBody EventLogSaveDTO eventLogSave) {
        return eventLogService.saveEventLog(eventLogSave).map(DefResult::success);
    }

    @PostMapping("/page")
    @Override
    public Mono<IResult<PageInfo<EventLogInfoDTO>>> findEventLogPages(@RequestBody EventLogPageQueryDTO eventLogPageQuery) {
        return eventLogService.findEventLogPages(eventLogPageQuery).map(DefResult::success);
    }


}
