package com.coral.test.rule.service;

import com.coral.test.rule.RuleApplicationTest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 规则执行测试
 *
 * @author huss
 * @date 2024/7/8 9:58
 * @packageName com.coral.test.rule.service
 * @className RuleParseServiceImplTest
 */
@Slf4j
public class RuleExecuteServiceTest extends RuleApplicationTest {


    @Resource
    private RuleExecuteService ruleExecuteService;


    @Test
    @DisplayName("规则执行")
    public void execute() throws InterruptedException {
        ruleExecuteService.execute().blockOptional();
        Thread.sleep(60 * 1000);
    }





}
