package com.coral.test.rule.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class RuleTestController extends BaseController implements RuleTestApi {


//    @PostMapping
//    @Override
//    public Mono<IResult<EventLogInfoDTO>> saveEventLog(@RequestBody EventLogSaveDTO eventLogSave) {
//        return this.buildResult(eventLogService.saveEventLog(eventLogSave));
//    }


}
