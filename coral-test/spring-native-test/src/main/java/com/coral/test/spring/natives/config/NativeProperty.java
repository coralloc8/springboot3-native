package com.coral.test.spring.natives.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * NativeProperty
 *
 * @author huss
 * @date 2024/4/18 15:51
 * @packageName com.coral.test.spring.natives.config
 * @className NativeProperty
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "coral.native")
public class NativeProperty {

    private String username;

    private String sex;


    public static String USERNAME;

    public static String SEX;

    @PostConstruct
    public void init() {
        USERNAME = this.username;
        SEX = this.sex;
    }

}
