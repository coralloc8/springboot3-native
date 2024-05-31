package com.coral.test.spring.natives.core.web.aop;


import com.coral.test.spring.natives.core.json.JsonUtil;
import com.coral.test.spring.natives.core.utils.AnnotationUtil;
import com.coral.test.spring.natives.core.utils.StrFormatter;
import com.coral.test.spring.natives.core.web.filter.ReactiveHttpContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huss
 */
@Component
@Aspect
@Slf4j
public class LogAop {

    private static final List<String> URLS = Arrays.asList("login", "logout");

    @Pointcut("execution(public * com..*.*Controller..*(..)) ||"
            + " execution(public * com..*.*Control..*(..)) ||"
            + " execution(public * org.springframework.security.oauth2.provider.endpoint.TokenEndpoint..*(..))")
    public void webLog() {

    }

    @Around("webLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String uuid = "";
        log.info(">>>>>[日志记录开始]....");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Optional<Log> logAnnotationOpt = AnnotationUtil.findAnnotation(methodSignature.getMethod(), Log.class);
        boolean needPrint = logAnnotationOpt.isEmpty() || logAnnotationOpt.get().recordRequestParam();
        String logVal = logAnnotationOpt.map(value -> value.value() + ":").orElse("");
        String module = logVal + methodSignature.getDeclaringTypeName() + "/" + methodSignature.getName();

        return printRequestLog(joinPoint, needPrint, module).flatMap(request -> {
            // 调用
            try {
                Object result = joinPoint.proceed();
                Mono<?> resultMono;
                if (result instanceof Mono<?> resMono) {
                    resultMono = resMono;
                } else if (result instanceof Flux<?> resFlux) {
                    resultMono = resFlux.collectList();
                } else if (result instanceof Collection<?> resCon) {
                    resultMono = Flux.fromIterable(resCon).collectList();
                } else {
                    resultMono = Mono.justOrEmpty(result);
                }
                return resultMono.map(res -> {
                    printResultLog(res, module);
                    return res;
                });
            } catch (Throwable e) {
                return Mono.error(e);
            }
        }).doFinally(type -> log.info(">>>>>[日志记录结束]..."));

    }

    private Mono<?> printRequestLog(ProceedingJoinPoint joinPoint, boolean needPrint, String module) {
        if (!needPrint) {
            return ReactiveHttpContextHolder.getRequest();
        }
        return ReactiveHttpContextHolder.getRequest().map(request -> {
//            String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
//            if (StringUtils.isNotBlank(traceId)) {
//                MDC.put(TRACE_ID_MDC_KEY, traceId);
//            }
            try {
                // 参数值
                Object[] args = joinPoint.getArgs();
                // 获取方法参数
                //文件上传时file会序列化失败，暂时过滤掉
                List<Object> httpReqArgs = streamOf(args)
                        .filter(arg -> (!(arg instanceof HttpServerRequest) &&
                                !(arg instanceof HttpServerResponse)) &&
                                !(arg instanceof MultipartFile))
                        .collect(Collectors.toList());
                boolean canToJson = true;
                String ip = getRemoteIP(request);
                String httpMethod = request.getMethod().name();
                String url = request.getURI().toString();
                MediaType mediaType = request.getHeaders().getContentType();

                String contentType = Objects.nonNull(mediaType) ? mediaType.getType() : MediaType.APPLICATION_FORM_URLENCODED_VALUE;

                boolean isFormData = StringUtils.isNotBlank(contentType) &&
                        contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE);

                if (URLS.stream().anyMatch(url::contains)) {
                    canToJson = false;
                }
                // 打印请求参数参数
                log.info(">>>>>[url]:{},[method]:{},[ip]:{}", url, httpMethod, ip);
                log.info(">>>>>[headers]：{}", this.printHeaders(request));

                if (isFormData) {
                    log.info(">>>>>[module]:{},contentType:form-data,do not output input parameters", module);
                } else {
                    //json序列化时在spring auth中的数据时会StackOverflowError
                    if (canToJson) {
                        log.info(">>>>>[module]:{},[params]:{}", module, JsonUtil.toJson(httpReqArgs));
                    } else {
                        log.info(">>>>>[module]:{},[params]:{}", module, httpReqArgs);
                    }
                }

            } catch (Exception e) {
                log.error(StrFormatter.format(">>>>>[printRequestLog]打印日志出错,Error:"), e);
            }
            return request;
        });
    }

    /**
     * 执行方法
     *
     * @param result
     * @param module
     * @return
     */
    private void printResultLog(Object result, String module) {
        try {
            // 调用原来的方法
            String json = JsonUtil.toJson(result);
            if (log.isDebugEnabled()) {
                log.debug(">>>>>调用结束[{}],完整版返参:{}", module, json);
            } else {
                if (json.length() > 5000) {
                    log.info(">>>>>调用结束[{}],返参数据量太大,截取后打印为:{}", module, json.substring(0, 5000) + "...");
                } else {
                    log.info(">>>>>调用结束[{}],返参:{}", module, json);
                }
            }

        } catch (Exception e) {
            log.error(StrFormatter.format(">>>>>[printResultLog]打印日志出错,Error:"), e);
        }
    }


    /**
     * stream 转换
     *
     * @param array
     * @param <T>
     * @return
     */
    private static <T> Stream<T> streamOf(T[] array) {
        return ArrayUtils.isEmpty(array) ? Stream.empty() : Arrays.stream(array);
    }

    private static String getRemoteIP(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("x-forwarded-for");
        try {
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeaders().getFirst("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
            }
            if (ip != null && ip.indexOf(",") > 0) {
                String[] parts = ip.split(",");
                for (String part : parts) {
                    if (!part.isEmpty() && !"unknown".equalsIgnoreCase(part)) {
                        ip = part.trim();
                        break;
                    }
                }
            }
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }
        } catch (Exception e) {
            log.error(">>>>>获取IP地址失败", e);
        }

        return ip;
    }

    private Set<String> printHeaders(ServerHttpRequest request) {
        /**
         * 获取所有请求头信息
         */
        return request.getHeaders().keySet();

    }

}
