package com.coral.test.rule.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.coral.test.rule.dto.BizRuleApplyConfigInfoDTO;
import com.coral.test.rule.service.BizRuleApplyConfigQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final R2dbcEntityTemplate template;


    private static final List<BizRuleApplyConfigInfoDTO> BIZ_RULE_CONFIGS_CACHE = new CopyOnWriteArrayList<>();

    @Override
    public Flux<BizRuleApplyConfigInfoDTO> findEnabledRules() {
        if (CollUtil.isNotEmpty(BIZ_RULE_CONFIGS_CACHE)) {
            log.info(">>>>> 【业务规则应用配置查询】 【触发缓存】");
            return Flux.fromIterable(BIZ_RULE_CONFIGS_CACHE);
        }
        log.info(">>>>> 【业务规则应用配置查询】 【实际查询数据库】");
        // 加入缓存
        return template.getDatabaseClient().sql(ENABLED_RULE_SQL).fetch().all()
                .log("findEnabledRules")
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
                }).doOnNext(BIZ_RULE_CONFIGS_CACHE::add)
                .onErrorResume(error -> {
                    log.error(">>>>> 【SQL执行异常】. 查询启用的规则列表,ERROR: \n", error);
                    return Flux.empty();
                });
    }
}
