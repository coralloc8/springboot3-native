package com.coral.test.rule.core.redis;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 异步redis
 *
 * @author huss
 * @date 2024/6/5 14:55
 * @packageName com.coral.test.spring.natives.core.redis
 * @className ReactiveRedisTemplateFactory
 */
public class ReactiveRedisTemplateFactory {
    private final ReactiveRedisTemplate reactiveRedisTemplate;

    private ReactiveRedisTemplateFactory(ReactiveRedisTemplate reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public static ReactiveRedisTemplateFactory getInstance(ReactiveRedisTemplate reactiveRedisTemplate) {
        return new ReactiveRedisTemplateFactory(reactiveRedisTemplate);
    }


    /**
     * 如果不存在，设值，存在则直接返回值
     *
     * @param cacheKey
     * @param flux
     * @param duration
     * @param <R>
     * @return
     */
    public <R> Flux<R> getIfAbsent(String cacheKey, Flux<R> flux, Duration duration) {
        return reactiveRedisTemplate.hasKey(cacheKey).flatMapMany(res -> {
            if (!(boolean) res) {
                return flux.flatMap(r -> reactiveRedisTemplate.opsForValue()
                        .setIfAbsent(cacheKey, r, duration)
                        .flatMap(e -> reactiveRedisTemplate.opsForValue().get(cacheKey))
                );
            }
            return reactiveRedisTemplate.opsForValue().get(cacheKey);
        });
    }
}
