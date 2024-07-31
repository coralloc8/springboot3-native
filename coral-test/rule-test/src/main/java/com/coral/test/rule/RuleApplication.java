package com.coral.test.rule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 规则启动类
 *
 * @author huss
 * @date 2024/7/2 10:19
 * @packageName com.coral.test.rule
 * @className RuleApplication
 */
@SpringBootApplication
public class RuleApplication {
    public static void main(String[] args) {
        System.setProperty("io.lettuce.core.jfr", "false");
        SpringApplication.run(RuleApplication.class, args);
    }
}
