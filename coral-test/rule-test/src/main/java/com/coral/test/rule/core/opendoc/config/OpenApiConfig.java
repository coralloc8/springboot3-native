package com.coral.test.rule.core.opendoc.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author huss
 * @version 1.0
 * @className SwaggerConfig
 * @description swagger 自定义配置
 * @date 2021/7/8 17:50
 */
@Slf4j
public class OpenApiConfig {

    @Getter
    private final OpenApiProperties openApiProperties;

    @Getter
    private final ApplicationContext context;

    @Getter
    private Map<String, OpenApiProperties.Group> groups;

    /**
     * 3.0注解变化 2.0的注解依然可以使用
     * 地址变为 swagger-ui/index.html
     *
     * @param openApiProperties
     * @ApiParam -> @Parameter
     * @ApiOperation -> @Operation
     * @Api -> @Tag
     * @ApiImplicitParams -> @Parameters
     * @ApiImplicitParam -> @Parameter
     * @ApiIgnore -> @Parameter(hidden = true) or @Operation(hidden = true) or @Hidden
     * @ApiModel -> @Schema
     * @Schema -> @Schema
     */
    public OpenApiConfig(OpenApiProperties openApiProperties, ApplicationContext context) {
        this.openApiProperties = openApiProperties;
        this.context = context;
        this.groups = openApiProperties.getGroups().stream()
                .collect(Collectors.toMap(OpenApiProperties.Group::getGroup, Function.identity()));
    }


    public void printVersion() {
        log.info("============================= openapi信息如下 =============================");
        String version = "Application Version: " + openApiProperties.getVersion() + ", " +
                "Spring Boot Version: " + SpringBootVersion.getVersion();
        log.info("#####项目版本号为:{}", version);
        log.info("#####项目访问地址为:{}", openApiProperties.getUrl());
        log.info("=========================================================================");
    }


    /**
     * 初始化tags
     *
     * @param group
     * @return
     */
    private List<Tag> initTags(OpenApiProperties.Group group) {
        return group.getTags().stream()
                .map(e -> new Tag().name(e.getName()).description(e.getDesc()))
                .collect(Collectors.toList());
    }


    private List<Tag> allTags() {
        List<Tag> tags = getGroups().entrySet().stream()
                .map(e -> initTags(e.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return tags;
    }

    /**
     * 创建组api
     *
     * @param groupName
     * @return
     */
    protected GroupedOpenApi createGroupedOpenApi(String groupName) {
        OpenApiProperties.Group group = getGroups().get(groupName);
        GroupedOpenApi.Builder builder = GroupedOpenApi.builder()
                .group(group.getGroup())
                .addOpenApiCustomizer(api -> api.tags(initTags(group)));

        if (StringUtils.isNotBlank(group.getPackagesToScan())) {
            builder.packagesToScan(group.getPackagesToScan());
        }

        if (StringUtils.isNotBlank(group.getPathsToMatch())) {
            builder.pathsToMatch(group.getPathsToMatch());
        }

        if (StringUtils.isNotBlank(group.getConsumesToMatch())) {
            builder.consumesToMatch(group.getConsumesToMatch());
        }

        if (StringUtils.isNotBlank(group.getHeadersToMatch())) {
            builder.headersToMatch(group.getHeadersToMatch());
        }

        if (StringUtils.isNotBlank(group.getProducesToMatch())) {
            builder.producesToMatch(group.getProducesToMatch());
        }

        if (StringUtils.isNotBlank(group.getPathsToExclude())) {
            builder.pathsToExclude(group.getPathsToExclude());
        }

        if (StringUtils.isNotBlank(group.getPackagesToExclude())) {
            builder.packagesToExclude(group.getPackagesToExclude());
        }


        return builder.build();
    }

    @ConditionalOnMissingBean(OpenAPI.class)
    @Bean
    public OpenAPI createRestApi() {
        return new OpenAPI()
                // 将api的元信息设置为包含在json ResourceListing响应中。
                .info(apiInfo())
                //授权
                .components(components())
                //所有tags
                .tags(allTags())
                //添加security
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .addSecurityItem(new SecurityRequirement().addList("LoginToken"))
                ;
    }


    private Components components() {
        return new Components()
                .addSecuritySchemes("LoginToken", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .name("token")
                        .in(In.HEADER)
                        .description("普通token")
                )
                .addSecuritySchemes("Authorization",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("jwt token")
                                .flows(
                                        new OAuthFlows()
                                )
                )
                ;
    }

    /**
     * API 页面上半部分展示信息
     */
    private Info apiInfo() {
        String contactName = StringUtils.isBlank(openApiProperties.getContactName()) ? "智慧医学" : openApiProperties.getContactName();

        String contactEmail = StringUtils.isBlank(openApiProperties.getContactEmail()) ? "452327322@qq.com" : openApiProperties.getContactEmail();

        return new Info()
                .title(openApiProperties.getName())
                .description(openApiProperties.getDescription())
                .contact(new Contact().name(contactName).email(contactEmail))
                .version("Application Version: " + openApiProperties.getVersion() + ", " +
                        "Spring Boot Version: " + SpringBootVersion.getVersion())
                ;
    }


}
