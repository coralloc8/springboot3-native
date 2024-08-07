package com.coral.test.rule;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;

import java.io.File;

/**
 * 规则启动类
 *
 * @author huss
 * @date 2024/7/2 10:19
 * @packageName com.coral.test.rule
 * @className RuleApplication
 */
@Slf4j
@SpringBootApplication
public class RuleApplication {

    public static void main(String[] args) {
        System.setProperty("io.lettuce.core.jfr", "false");
        SpringApplication.run(RuleApplication.class, args);

        log.info("##### 当前根目录为：{}", System.getProperty("user.dir"));
        log.info("##### 当前根目录为111111：{}", new File(FileUtil.getAbsolutePath("./config/rule-execute")).toString());
        log.info("##### 当前根目录为222222：{}", new File(FileUtil.getAbsolutePath("config/rule-execute")).toString());
    }
}
