package com.coral.test.rule.core.web.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 异步方式
 *
 * @author huss
 * @date 2024/3/29 17:38
 * @packageName com.coral.test.spring.natives.core.web
 * @className ReactiveHttpContextHolder
 */
public class ReactiveHttpContextHolder {

    //获取当前请求对象
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(contextView -> Mono.justOrEmpty(contextView.get(Info.CONTEXT_KEY).getRequest()));
    }

    //获取当前response
    public static Mono<ServerHttpResponse> getResponse() {
        return Mono.deferContextual(contextView -> Mono.justOrEmpty(contextView.get(Info.CONTEXT_KEY).getResponse()));
    }

    public static final class Info {
        public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;
    }

}
