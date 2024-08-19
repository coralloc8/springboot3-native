package com.coral.test.rule.util;

import com.coral.test.rule.core.web.SpringContextHolder;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;

import java.util.Optional;

/**
 * 模板创建
 *
 * @author huss
 * @date 2024/7/24 10:43
 * @packageName com.coral.test.rule.service
 * @className FreeMarkerUtils
 */
@Slf4j
public class FreeMarkerUtils {


    // 默认模板名称
    private static final String RULE_REPORT_TEMPLATE_NAME = "rule_report.md.ftl";

    private static final String RULE_INDEX_TEMPLATE_NAME = "rule_index.md.ftl";

    /**
     * 获取默认的规则报表模板
     *
     * @return
     */
    public static Optional<Template> getDefRuleIndex() {
        return getTemplate(RULE_INDEX_TEMPLATE_NAME);
    }

    /**
     * 获取默认的规则报表模板
     *
     * @return
     */
    public static Optional<Template> getDefRuleReport() {
        return getTemplate(RULE_REPORT_TEMPLATE_NAME);
    }

    /**
     * 创建默认的规则报表
     *
     * @param model
     * @return
     */
    public static String createDefRuleReport(Template template, Object model) {
        return create(template, model);
    }

    /**
     * 创建默认的规则报表
     *
     * @param model
     * @return
     */
    public static String createDefRuleReport(Object model) {
        return create(RULE_REPORT_TEMPLATE_NAME, model);
    }


    /**
     * 创建模板
     *
     * @param templateName
     * @param model
     * @return
     */
    public static String create(Template template, Object model) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            log.error("创建失败:", e);
        }
        return "";
    }

    /**
     * 创建模板
     *
     * @param templateName
     * @param model
     * @return
     */
    public static String create(String templateName, Object model) {
        try {
            FreeMarkerConfigurer freeMarkerConfigurer = SpringContextHolder.getBean(FreeMarkerConfigurer.class);
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            log.error("创建失败:", e);
        }
        return "";
    }

    /**
     * 创建模板
     *
     * @param templateName
     * @param model
     * @return
     */
    public static Optional<Template> getTemplate(String templateName) {
        try {
            FreeMarkerConfigurer freeMarkerConfigurer = SpringContextHolder.getBean(FreeMarkerConfigurer.class);
            return Optional.ofNullable(freeMarkerConfigurer.getConfiguration().getTemplate(templateName));
        } catch (Exception e) {
            log.error("创建失败:", e);
        }
        return Optional.empty();
    }
}
