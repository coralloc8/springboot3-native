package com.coral.test.spring.natives.core.web.filter;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coral.test.spring.natives.config.FilterCache.*;

/**
 * 缓存 body （一般网关来做全局缓存）
 *
 * @author huss
 * @date 2024/4/1 10:21
 * @packageName com.coral.test.spring.natives.core.web.filter
 * @className RequestTraceFilter
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class RequestTraceFilter implements WebFilter, Ordered {
    private static final byte[] EMPTY_BYTES = {};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("[RequestTraceFilter] filter start.");
        final HttpMethod method = exchange.getRequest().getMethod();
        boolean isJson = MediaType.APPLICATION_JSON.includes(exchange.getRequest().getHeaders().getContentType());
        boolean isFormData = MediaType.APPLICATION_FORM_URLENCODED.includes(exchange.getRequest().getHeaders().getContentType()) ||
                MediaType.MULTIPART_FORM_DATA.includes(exchange.getRequest().getHeaders().getContentType());
        if (HttpMethod.OPTIONS.equals(method) || HttpMethod.GET.equals(method) || HttpMethod.DELETE.equals(method)) {
            return chain.filter(exchange);
        }
        try {
            Function<ServerHttpRequest, Mono<Void>> function = serverHttpRequest -> {
                if (serverHttpRequest == exchange.getRequest()) {
                    return chain.filter(exchange);
                }
                return chain.filter(exchange.mutate().request(serverHttpRequest).build());
            };
            // 使用 Gateway 自带工具 构建缓存body
            if (isJson) {
                return this.cacheRequestBodyAndRequest(exchange, true, function);
            }
            if (isFormData) {
                return cacheFormData(exchange, function);
            }
        } catch (Exception e) {
            log.error("过滤异常.", e);
        }
        return chain.filter(exchange);
    }



    private <T> Mono<T> cacheFormData(ServerWebExchange exchange, Function<ServerHttpRequest, Mono<T>> function) {
        return exchange.getFormData().map(mu -> {
            log.info(">>>>>缓存request form data");
            MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
            Charset charset = Objects.nonNull(mediaType) && Objects.nonNull(mediaType.getCharset()) ?
                    mediaType.getCharset() : StandardCharsets.UTF_8;
            String params = buildParamStr(mu, charset);
            byte[] bodyBytes = params.getBytes(charset);
            exchange.getAttributes().put(CACHE_FORM_DATA, mu);
            int contentLength = params.length();
            final ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return DataBufferUtils.read(new ByteArrayResource(bodyBytes),
                            new NettyDataBufferFactory(ByteBufAllocator.DEFAULT), contentLength
                    );
                }
            };
            return mutatedRequest.mutate()
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .build();
        }).switchIfEmpty(Mono.just(exchange.getRequest())).flatMap(function);
    }

    private String buildParamStr(MultiValueMap<String, String> formData, Charset charset) {
        return formData.entrySet().stream()
                .map(e -> {
                    List<String> values = e.getValue();
                    if (values.size() > 1) {
                        return values.stream().map(val -> e.getKey() + "=" + URLEncoder.encode(val, charset))
                                .collect(Collectors.joining("&"));
                    }
                    return e.getKey() + "=" + URLEncoder.encode(values.get(0), charset);
                })
                .collect(Collectors.joining("&"));
    }

    /**
     * 缓存requestBody和request
     *
     * @param exchange
     * @param cacheDecoratedRequest
     * @param function
     * @param <T>
     * @return
     */
    private <T> Mono<T> cacheRequestBodyAndRequest(ServerWebExchange exchange, boolean cacheDecoratedRequest,
                                                   Function<ServerHttpRequest, Mono<T>> function) {
        log.info(">>>>>缓存request body data");
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory factory = response.bufferFactory();
        // Join all the DataBuffers so we have a single DataBuffer for the body
        return DataBufferUtils.join(exchange.getRequest().getBody()).defaultIfEmpty(factory.wrap(EMPTY_BYTES))
                .map(dataBuffer -> decorate(exchange, dataBuffer, cacheDecoratedRequest))
                .switchIfEmpty(Mono.just(exchange.getRequest())).flatMap(function);
    }

    private ServerHttpRequest decorate(ServerWebExchange exchange, DataBuffer dataBuffer, boolean cacheDecoratedRequest) {
        if (dataBuffer.readableByteCount() > 0) {
            if (log.isTraceEnabled()) {
                log.trace("retaining body in exchange attribute");
            }

            Object cachedDataBuffer = exchange.getAttribute(CACHED_REQUEST_BODY_ATTR);
            // don't cache if body is already cached
            if (!(cachedDataBuffer instanceof DataBuffer)) {
                exchange.getAttributes().put(CACHED_REQUEST_BODY_ATTR, dataBuffer);
            }
        }

        ServerHttpRequest decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Mono.fromSupplier(() -> {
                    if (exchange.getAttribute(CACHED_REQUEST_BODY_ATTR) == null) {
                        // probably == downstream closed or no body
                        return null;
                    }
                    if (dataBuffer instanceof NettyDataBuffer pdb) {
                        return pdb.factory().wrap(pdb.getNativeBuffer().retainedSlice());
                    } else if (dataBuffer instanceof DefaultDataBuffer ddf) {
                        return ddf.factory().wrap(Unpooled.wrappedBuffer(ddf.getNativeBuffer()).nioBuffer());
                    } else {
                        throw new IllegalArgumentException(
                                "Unable to handle DataBuffer of type " + dataBuffer.getClass());
                    }
                }).flux();
            }
        };
        if (cacheDecoratedRequest) {
            exchange.getAttributes().put(CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR, decorator);
        }
        return decorator;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
