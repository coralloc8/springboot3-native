package com.coral.test.rule.service;

import com.coral.test.rule.RuleApplicationTest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 业务规则查询测试
 *
 * @author huss
 * @date 2024/7/25 9:51
 * @packageName com.coral.test.rule.service
 * @className BizRuleApplyConfigQueryServiceTest
 */
@Slf4j
public class BizRuleApplyConfigQueryServiceTest extends RuleApplicationTest {

    @Resource
    private BizRuleApplyConfigQueryService bizRuleApplyConfigQueryService;


    @Test
    @DisplayName("查询启用的规则列表")
    public void findEnabledRules() {
        System.out.println("结果：" + bizRuleApplyConfigQueryService.findEnabledRules().blockLast());
//        bizRuleApplyConfigQueryService.findEnabledRules().subscribe(
//                list -> System.out.println("Collected List: " + list), // 成功时的回调
//                error -> System.err.println("Error: " + error), // 错误时的回调
//                () -> System.out.println("Completed.") // 完成时的回调
//        );
    }

}
