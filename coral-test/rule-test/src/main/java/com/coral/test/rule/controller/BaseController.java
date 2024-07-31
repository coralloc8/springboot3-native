package com.coral.test.rule.controller;

import com.coral.test.rule.core.response.DefResult;
import com.coral.test.rule.core.response.IResult;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

/**
 * 基础类
 *
 * @author huss
 * @date 2024/4/18 14:10
 * @packageName com.coral.test.spring.natives.controller
 * @className BaseController
 */
public class BaseController {

    protected <T> Mono<IResult<T>> buildResult(Mono<T> mono) {
        return mono.map(DefResult::success).defaultIfEmpty(DefResult.success());
    }

    protected <T, R> Mono<IResult<R>> buildResult(Mono<T> mono, @NonNull Function<? super T, R> mapper) {
        return mono.map(e -> DefResult.success(mapper.apply(e))).defaultIfEmpty(DefResult.success());
    }

    protected <T> Mono<IResult<List<T>>> buildResult(Flux<T> flux) {
        return flux.collectList().map(DefResult::success).defaultIfEmpty(DefResult.success());
    }

    protected <T, R> Mono<IResult<List<R>>> buildResult(Flux<T> flux, @NonNull Function<List<? super T>, List<R>> mapper) {
        return flux.collectList().map(e -> DefResult.success(mapper.apply(e))).defaultIfEmpty(DefResult.success());
    }
}
