package com.coral.test.rule.runner;

import com.coral.test.rule.service.RuleExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 规则执行器执行
 *
 * @author huss
 * @date 2024/7/26 17:45
 * @packageName com.coral.test.rule.runner
 * @className RuleExecutorRunner
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class RuleExecutorRunner implements ApplicationRunner {

    private final RuleExecuteService ruleExecuteService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("##### 同步自动运行规则 开始 #####");
        ruleExecuteService.execute().subscribe();
        log.info("##### 同步自动运行规则 结束 #####");
    }

}
