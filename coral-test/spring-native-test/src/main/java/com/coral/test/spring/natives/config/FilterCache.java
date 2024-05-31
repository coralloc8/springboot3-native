package com.coral.test.spring.natives.config;

/**
 * @author huss
 * @version 1.0
 * @className FilterCache
 * @description 过滤器缓存
 * @date 2022/10/11 8:44
 */
public interface FilterCache {

    String CACHE_FORM_DATA = "cacheFormData";
    String CACHED_REQUEST_BODY_ATTR = "cachedRequestBody";
    String CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR = "cachedServerHttpRequestDecorator";

    String TRACE_ID_HEADER = "X-Trace-Id";
    String TRACE_ID_MDC_KEY = "traceId";


}
