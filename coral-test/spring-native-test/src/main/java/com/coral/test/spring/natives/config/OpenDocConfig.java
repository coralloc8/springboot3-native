package com.coral.test.spring.natives.config;

import com.coral.test.spring.natives.core.opendoc.config.OpenApiConfig;
import com.coral.test.spring.natives.core.opendoc.config.OpenApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huss
 * @version 1.0
 * @className OpenDocConfig
 * @description doc日志
 * @date 2021/9/18 10:41
 */
@Configuration
@Slf4j
public class OpenDocConfig extends OpenApiConfig {

    public OpenDocConfig(OpenApiProperties openApiProperties, ApplicationContext context) {
        super(openApiProperties, context);
    }

    @Bean
    public GroupedOpenApi adminGroup() {
        log.info(">>doc init");
        return this.createGroupedOpenApi("api");
    }

}
