package com.coral.test.rule.core.web;

import com.coral.test.rule.core.enums.ErrorMessageEnum;
import com.coral.test.rule.core.exception.BaseServiceException;
import com.coral.test.rule.core.exception.ValidateException;
import com.coral.test.rule.core.response.DefResult;
import com.coral.test.rule.core.web.filter.ReactiveHttpContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

/**
 * webflux 全局异常处理 可支持函数式编程方式
 *
 * @author huss
 * @date 2024/4/1 8:47
 * @packageName com.coral.test.rule.core.web
 * @className GlobalErrorWebExceptionHandler
 */
@Slf4j
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        log.error("服务请求异常。", error);
        if (error instanceof WebExchangeBindException exec) {
            String msg;
            if (exec.hasErrors()) {
                FieldError fieldError = exec.getFieldErrors().get(0);
                String field = fieldError.getField();
                String message = fieldError.getDefaultMessage();
                msg = "【" + field + "】校验失败！";
                if (message != null) {
                    msg = msg + "msg: " + message;
                }
            } else {
                msg = exec.getMessage();
            }
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(DefResult.error(ErrorMessageEnum.ILLEGAL_PARAMETER.getCode(), msg));
        } else if (error instanceof BaseServiceException exc) {
            // 自定义服务异常
            String errorMessage = exc.getErrMessage();
            Integer errorCode = Integer.valueOf(exc.getErrCode());
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(DefResult.error(errorCode, errorMessage));
        } else if (error instanceof ValidateException exec) {
            // 自定义校验异常
            return ServerResponse.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(DefResult.error(ErrorMessageEnum.ILLEGAL_PARAMETER, exec.getDetails()));
        }
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(DefResult.error());
    }
}
