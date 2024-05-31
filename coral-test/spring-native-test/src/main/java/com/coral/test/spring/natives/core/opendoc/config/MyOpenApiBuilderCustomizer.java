package com.coral.test.spring.natives.core.opendoc.config;

import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.service.OpenAPIService;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.springdoc.core.utils.SpringDocUtils.getConfig;


/**
 * @author huss
 * @version 1.0
 * @className OpenApiBuilderCustomizer
 * @description swagger api 自定义接口显示
 * @date 2021/9/6 17:00
 */
@Component
@Slf4j
public class MyOpenApiBuilderCustomizer implements OpenApiBuilderCustomizer {

    @Override
    public void customise(OpenAPIService openApiService) {
        // Set default mappings
        log.debug(">>>自定义api显示....");

        //没有tags或者tag注解的controller全部隐藏
        List<Class<?>> hiddenRestControllers = openApiService.getMappingsMap().entrySet().parallelStream()
                .filter(controller -> (AnnotationUtils.findAnnotation(
                                controller.getValue().getClass(), Tags.class) == null
                        ) && (AnnotationUtils.findAnnotation(
                                controller.getValue().getClass(),
                                io.swagger.v3.oas.annotations.tags.Tag.class) == null
                        )
                ).map(controller -> controller.getValue().getClass())
                .collect(Collectors.toList());


        if (!CollectionUtils.isEmpty(hiddenRestControllers)) {
            getConfig().addHiddenRestControllers(hiddenRestControllers.toArray(new Class<?>[hiddenRestControllers.size()]));
        }
    }
}
