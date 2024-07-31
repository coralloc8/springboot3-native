package com.coral.test.rule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 业务规则应用配置信息
 *
 * @author huss
 * @date 2024/7/16 9:36
 * @packageName com.coral.test.rule.dto
 * @className BizRuleApplyConfigInfoDTO
 */
@Builder
@Schema(description = "业务规则应用配置信息")
@Data
public class BizRuleApplyConfigInfoDTO {
    /**
     * 提示节点
     */
    @Schema(description = "提示节点")
    private String tipsNode;

    @Schema(description = "项目名")
    private String project;
    /**
     * 启用的规则列表
     */
    @Schema(description = "启用的规则列表")
    private List<String> ruleCodes;
}
