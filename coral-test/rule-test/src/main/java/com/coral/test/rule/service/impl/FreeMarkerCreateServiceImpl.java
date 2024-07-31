package com.coral.test.rule.service.impl;

import com.coral.test.rule.service.FreeMarkerCreateService;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import reactor.core.publisher.Mono;

/**
 * 模板创建服务
 *
 * @author huss
 * @date 2024/7/24 10:43
 * @packageName com.coral.test.rule.service.impl
 * @className FreeMarkerCreateServiceImpl
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FreeMarkerCreateServiceImpl implements FreeMarkerCreateService {

    private final FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public Mono<String> create(String templateName, Object model) {
        try {
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            return Mono.just(FreeMarkerTemplateUtils.processTemplateIntoString(template, model));
        } catch (Exception e) {
            log.error("创建失败:", e);
        }
        return Mono.empty();
    }
}
