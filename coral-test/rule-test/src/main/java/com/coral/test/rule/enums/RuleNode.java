package com.coral.test.rule.enums;

import com.coral.test.rule.core.enums.IEnum;
import com.coral.test.rule.core.r2dbc.IMapping;
import lombok.Getter;

/**
 * @author huss
 * @version 1.0
 * @className EventStatus
 * @description 事件状态
 * @date 2022/7/20 18:27
 */
public enum RuleNode implements IEnum<RuleNode, String>, IMapping<String> {

    /**
     * 基础校验
     */
    VERIFY_BASIC("verify_basic", "基础校验"),

    /**
     * 编码校验
     */
    VERIFY_CODE("verify_code", "编码校验"),

    /**
     * 循证校验
     */
    VERIFY_EVIDENCE_BASED("verify_evidence_based", "循证校验"),

    ;

    RuleNode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Getter
    private final String code;

    @Getter
    private final String name;


    @Override
    public String getValue() {
        return getCode();
    }
}
