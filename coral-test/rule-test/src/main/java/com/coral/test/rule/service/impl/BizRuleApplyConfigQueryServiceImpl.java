package com.coral.test.rule.service.impl;

import com.coral.test.rule.dto.BizRuleApplyConfigInfoDTO;
import com.coral.test.rule.service.BizRuleApplyConfigQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 业务规则应用配置查询
 *
 * @author huss
 * @date 2024/7/16 9:52
 * @packageName com.coral.test.rule.service.impl
 * @className BizRuleApplyConfigQueryServiceImpl
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BizRuleApplyConfigQueryServiceImpl implements BizRuleApplyConfigQueryService {
    private final static String ENABLED_RULE_SQL = "select rule_code,one_category as project,tips_node from biz_rule.rule_apply_config where status = 1;";

    private final static AtomicReference<Flux<BizRuleApplyConfigInfoDTO>> CACHE = new AtomicReference<>(Flux.empty());

    private final R2dbcEntityTemplate template;


    @Override
    public Flux<BizRuleApplyConfigInfoDTO> findEnabledRules() {
        return CACHE.updateAndGet(current -> current.switchIfEmpty(template.getDatabaseClient().sql(ENABLED_RULE_SQL)
                .fetch().all().cache()
                .filter(map -> Objects.nonNull(map.get("tips_node")) &&
                        Objects.nonNull(map.get("project")) &&
                        Objects.nonNull(map.get("rule_code"))
                ).groupBy(map -> {
                    String tipsNode = (String) map.get("tips_node");
                    String project = (String) map.get("project");
                    return String.join("@", tipsNode, project);
                }).flatMap(group -> {
                    String[] keys = group.key().split("@");
                    String tipsNode = keys.length > 0 ? keys[0] : "";
                    String project = keys.length > 1 ? keys[1] : "";
                    BizRuleApplyConfigInfoDTO configInfo = BizRuleApplyConfigInfoDTO.builder()
                            .tipsNode(tipsNode)
                            .project(project)
                            .ruleCodes(Collections.emptyList())
                            .build();
                    return group.map(map -> (String) map.get("rule_code"))
                            .filter(StringUtils::isNotBlank)
                            .collectList()
                            .map(ruleCodes -> {
                                configInfo.setRuleCodes(ruleCodes);
                                return configInfo;
                            }).defaultIfEmpty(configInfo);
                }).onErrorResume(error -> {
                    log.error(">>>>> 【SQL执行异常】. 查询启用的规则列表,ERROR: \n", error);
                    return Flux.empty();
                })
        ));
    }

}
