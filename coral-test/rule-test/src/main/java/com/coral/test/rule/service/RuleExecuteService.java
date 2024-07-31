package com.coral.test.rule.service;

import reactor.core.publisher.Mono;

/**
 * 规则执行
 *
 * @author huss
 * @date 2024/7/3 11:00
 * @packageName com.coral.test.rule.service
 * @className RuleExecuteService
 */
public interface RuleExecuteService {

    /**
     * 执行
     */
    Mono<Void> execute();

}
