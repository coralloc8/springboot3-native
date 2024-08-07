package com.coral.test.rule.controller;


import com.coral.test.rule.core.response.IResult;
import com.coral.test.rule.service.RuleExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 规则执行器
 *
 * @author huss
 * @date 2024/3/28 17:16
 * @packageName org.com.coral.test.spring.natives.controller
 * @className RuleExecutorController
 */
@Slf4j
@RequestMapping("/rule/exectors")
@RestController
@RequiredArgsConstructor
public class RuleExecutorController extends BaseController implements RuleExecutorApi {

    private final RuleExecuteService ruleExecuteService;


    @PostMapping("/execute")
    @Override
    public Mono<IResult<Void>> execute() {
        return buildResult(ruleExecuteService.execute());
    }
}
