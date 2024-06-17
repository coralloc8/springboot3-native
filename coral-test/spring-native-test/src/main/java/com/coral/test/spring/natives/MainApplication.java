package com.coral.test.spring.natives;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主类
 *
 * @author huss
 * @date 2024/3/28 14:55
 * @packageName org.com.coral.test.spring.natives
 * @className ${NAME}
 */
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        System.setProperty("io.lettuce.core.jfr", "false");
        SpringApplication.run(MainApplication.class, args);
    }
}