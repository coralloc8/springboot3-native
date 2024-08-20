package com.coral.test.rule.config;

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
@ConfigurationProperties(prefix = "coral.rule")
public class RuleProperty {

    /**
     * 所有规则执行超时时间 默认 秒
     */
    private Long executeTimout;


    /**
     * 规则配置路径
     */
    private String ruleConfigPath;

    /**
     * 规则配置路径
     */
    private String reportTemplatePath;


    /**
     * 规则路径（不会执行，只是放着。真正执行的时候可以将规则复制到执行路径）
     */
    private String rulePath;

    /**
     * 规则执行路径
     */
    private String ruleExecutePath;

    /**
     * 规则报告路径
     */
    private String ruleReportPath;

    /**
     * 执行的配置文件名称
     */
    private String executeConfigName;


    public static Long EXECUTE_TIMOUT = 2 * 60L;
    public static String RULE_PATH = "rule";

    public static String RULE_CONFIG_PATH = "rule-config";

    public static String RULE_EXECUTE_PATH = "rule-execute";

    public static String RULE_REPORT_PATH = "rule-report";

    public static String REPORT_TEMPLATE_PATH = "templates";

    public static String EXECUTE_CONFIG_NAME = "rule_execute.json";

    @PostConstruct
    public void init() {
        RULE_PATH = this.rulePath;
        RULE_CONFIG_PATH = this.ruleConfigPath;
        RULE_EXECUTE_PATH = this.ruleExecutePath;
        RULE_REPORT_PATH = this.ruleReportPath;
        EXECUTE_CONFIG_NAME = this.executeConfigName;
        REPORT_TEMPLATE_PATH = this.reportTemplatePath;
        EXECUTE_TIMOUT = this.executeTimout;
    }

}
