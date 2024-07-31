package com.coral.test.rule.core.web.filter;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.coral.test.rule.config.FilterCache.TRACE_ID_HEADER;
import static com.coral.test.rule.config.FilterCache.TRACE_ID_MDC_KEY;

/**
 * traceId
 *
 * @author huss
 * @date 2024/4/3 11:05
 * @packageName com.coral.test.spring.natives.core.web.filter
 * @className TraceIdFilter
 */
@Component
public class TraceIdFilter implements WebFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null) {
            traceId = getTraceId(); // 实现自定义的traceId生成逻辑
        }
        MDC.put(TRACE_ID_MDC_KEY, traceId);
        exchange.getAttributes().put(TRACE_ID_HEADER, traceId);
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            MDC.remove(TRACE_ID_MDC_KEY);
        }));
    }

    private String getTraceId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
