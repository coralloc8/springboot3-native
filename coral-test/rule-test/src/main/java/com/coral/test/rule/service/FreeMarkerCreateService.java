package com.coral.test.rule.service;

import reactor.core.publisher.Mono;

/**
 * 模板创建服务
 *
 * @author huss
 * @date 2024/7/24 10:43
 * @packageName com.coral.test.rule.service
 * @className FreeMarkerCreateService
 */
public interface FreeMarkerCreateService {
    Mono<String> create(String templateName, Object model);
}
