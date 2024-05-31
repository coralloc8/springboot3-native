package com.coral.test.spring.simple.core.opendoc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author huss
 * @version 1.0
 * @className SwaggerProperties
 * @description 自定义swagger配置
 * @date 2021/7/8 17:49
 */
@Component
@ConfigurationProperties("springdoc")
@Data
public class OpenApiProperties {

    /**
     * 项目应用名
     */
    private String name;

    /**
     * 项目版本信息
     */
    private String version;

    /**
     * 项目描述信息
     */
    private String description;

    /**
     * 接口调试地址
     */
    private String url;


    private List<Group> groups;

    /**
     * 联系人
     */
    private String contactName;

    private String contactEmail;


    @Data
    public static class Group {

        private String group;

        private String packagesToScan;

        private String pathsToMatch;

        private String pathsToExclude;

        private String consumesToMatch;

        private String producesToMatch;

        private String headersToMatch;

        private String packagesToExclude;

        private List<Version> tags;
    }

    @Data
    public static class Version {
        /**
         * 版本名称
         */
        private String name;
        /**
         * 注释
         */
        private String desc;
    }


}
