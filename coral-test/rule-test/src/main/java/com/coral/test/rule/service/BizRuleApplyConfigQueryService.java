package com.coral.test.rule.service;

import com.coral.test.rule.dto.BizRuleApplyConfigInfoDTO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 业务规则应用配置查询
 *
 * @author huss
 * @date 2024/7/16 9:34
 * @packageName com.coral.test.rule.service
 * @className BizRuleApplyConfigQueryService
 */
public interface BizRuleApplyConfigQueryService {

    /**
     * 查询启用的规则列表
     *
     * @return
     */
    Flux<BizRuleApplyConfigInfoDTO> findEnabledRules();


}
