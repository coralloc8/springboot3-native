package com.coral.test.rule.core.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 获取ServerWebExchange
 *
 * @author huss
 * @date 2024/3/29 17:41
 * @packageName com.coral.test.spring.natives.core.filter
 * @className ReactiveRequestContextFilter
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRequestContextFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("[ReactiveRequestContextFilter] filter start.");

        //设置当前请求
        return chain.filter(exchange)
                .contextWrite(context -> context.put(ReactiveHttpContextHolder.Info.CONTEXT_KEY, exchange));
    }


}
