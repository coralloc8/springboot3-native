package com.coral.test.spring.natives.controller;

import com.coral.test.spring.natives.config.NativeProperty;
import com.coral.test.spring.natives.core.response.IResult;
import com.coral.test.spring.natives.dto.PageInfo;
import com.coral.test.spring.natives.dto.req.EventLogPageQueryDTO;
import com.coral.test.spring.natives.dto.req.EventLogQueryDTO;
import com.coral.test.spring.natives.dto.req.EventLogSaveDTO;
import com.coral.test.spring.natives.dto.res.EventLogFullInfoDTO;
import com.coral.test.spring.natives.dto.res.EventLogInfoDTO;
import com.coral.test.spring.natives.dto.res.NativeInfoDTO;
import com.coral.test.spring.natives.service.EventLogService;
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
public class EventLogController extends BaseController implements EventLogApi {

    @Resource
    private EventLogService eventLogService;


    @GetMapping("/native")
    @Override
    public Mono<IResult<NativeInfoDTO>> findNativeInfo() {
        return this.buildResult(Mono.just(new NativeInfoDTO(NativeProperty.USERNAME, NativeProperty.SEX)));
    }

    @GetMapping("/{eventLogId}")
    @Override
    public Mono<IResult<EventLogInfoDTO>> findEventLog(@PathVariable("eventLogId") String eventLogId) {
        // 能获取到上下文
//        return eventLogService.findEventLog(eventLogId)
//                .flatMap(info -> ReactiveHttpContextHolder.getRequest().map(req -> {
//                    log.info("#######################【headers2】:{}", req.getHeaders());
//                    return DefResult.success(info);
//                }));
        return this.buildResult(eventLogService.findEventLog(eventLogId));
    }

    @PostMapping("/search")
    @Override
    public Mono<IResult<List<EventLogInfoDTO>>> findEventLogs(@RequestBody EventLogQueryDTO eventLogQuery) {
        return this.buildResult(eventLogService.findEventLogs(eventLogQuery));
    }


    @PostMapping("/search/full")
    @Override
    public Mono<IResult<List<EventLogFullInfoDTO>>> findEventFullLogs(@RequestBody EventLogQueryDTO eventLogQuery) {
        return this.buildResult(eventLogService.findEventFullLogs(eventLogQuery));
    }


    @PostMapping
    @Override
    public Mono<IResult<EventLogInfoDTO>> saveEventLog(@RequestBody EventLogSaveDTO eventLogSave) {
        return this.buildResult(eventLogService.saveEventLog(eventLogSave));
    }

    @PostMapping("/page")
    @Override
    public Mono<IResult<PageInfo<EventLogInfoDTO>>> findEventLogPages(@RequestBody EventLogPageQueryDTO eventLogPageQuery) {
        return this.buildResult(eventLogService.findEventLogPages(eventLogPageQuery));
    }


}
