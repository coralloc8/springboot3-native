package com.coral.test.rule.dto;

import cn.hutool.core.collection.CollUtil;
import com.coral.test.rule.util.RuleParseHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则报表首页信息
 *
 * @author huss
 * @date 2024/7/16 17:48
 * @packageName com.coral.test.rule.dto
 * @className RuleReportIndexInfoDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "规则报表首页信息")
@Data
@Slf4j
public class RuleReportIndexInfoDTO {
    private static final String MSG_NOT_TRIGGERED = "未触发";
    private static final String MSG_SUCCESS = "成功";
    private static final String MSG_FAILURE = "失败";
    private static final String RULE_RESULT_KEY = "rule";

    public static List<RuleReportIndexInfoDTO> create(Collection<RuleExecuteResponseInfoDTO> responses, Set<String> fileNames) {
        Map<String, List<RuleExecuteResponseInfoDTO>> ruleResponseMap = responses.stream()
                .filter(e -> StringUtils.isNotBlank(e.getRuleCode()))
                .collect(Collectors.groupingBy(RuleExecuteResponseInfoDTO::getRuleCode));

        return fileNames.stream()
                .collect(Collectors.groupingBy(fileName -> RuleParseHelper.getInstance().parseFileRuleCode(fileName)))
                .entrySet().stream()
                .map(en -> {
                    List<RuleExecuteResponseInfoDTO> curResponses = ruleResponseMap.get(en.getKey());
                    if (CollUtil.isEmpty(curResponses)) {
                        return empty(en.getKey(), empty(en.getValue()));
                    }
                    List<String> filterFileNames = new ArrayList<>(en.getValue());
                    filterFileNames.removeAll(
                            curResponses.stream().map(RuleExecuteResponseInfoDTO::getRuleFileName)
                                    .collect(Collectors.toSet())
                    );
                    RuleExecuteResponseInfoDTO first = curResponses.get(0);
                    List<RuleResult> results = buildRuleResults(curResponses);
                    results.addAll(empty(filterFileNames));

                    return RuleReportIndexInfoDTO.builder()
                            .ruleCode(first.getRuleCode())
                            .ruleName(first.getRuleName())
                            .results(results)
                            .build();
                }).collect(Collectors.toList());
    }


    private static List<RuleResult> buildRuleResults(Collection<RuleExecuteResponseInfoDTO> responses) {
        return responses.stream()
                .filter(ee -> StringUtils.isNotBlank(ee.getRuleFileName()))
                .collect(Collectors.groupingBy(RuleExecuteResponseInfoDTO::getRuleFileName))
                .entrySet().stream().map(ee -> {
                    RuleResult result = new RuleResult();
                    result.setRuleFileName(ee.getKey());
                    result.setRuleFileJsonPath(RuleParseHelper.getInstance().buildRuleJsonHtmlPath(ee.getKey()));
                    ee.getValue().forEach(eev -> {
                        String ruleFilePath = RuleParseHelper.getInstance().buildRuleResultHtmlPath(eev.getRuleFileName(), eev.getApiService());
                        if (RULE_RESULT_KEY.equalsIgnoreCase(eev.getApiService())) {
                            result.setRuleTestResult(eev.getTestResult() ? MSG_SUCCESS : MSG_FAILURE);
                            result.setRuleFilePath(ruleFilePath);
                        } else {
                            result.setBizTestResult(eev.getTestResult() ? MSG_SUCCESS : MSG_FAILURE);
                            result.setBizFilePath(ruleFilePath);
                        }
                    });
                    return result;
                }).collect(Collectors.toList());
    }

    private static RuleReportIndexInfoDTO empty(String ruleCode, List<RuleResult> results) {
        return RuleReportIndexInfoDTO.builder()
                .ruleCode(ruleCode)
                .results(results)
                .build();
    }

    private static List<RuleResult> empty(Collection<String> fileNames) {
        if (CollUtil.isEmpty(fileNames)) {
            return Collections.emptyList();
        }
        return fileNames.stream()
                .map(e -> RuleResult.builder()
                        .ruleFileName(e)
                        .ruleFileJsonPath(RuleParseHelper.getInstance().buildRuleJsonHtmlPath(e))
                        .bizTestResult(MSG_NOT_TRIGGERED)
                        .bizFilePath("")
                        .ruleTestResult(MSG_NOT_TRIGGERED)
                        .ruleFilePath("")
                        .build()
                ).collect(Collectors.toList());
    }

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     *
     */
    @Builder.Default
    private List<RuleResult> results = new ArrayList<>();

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RuleResult {
        /**
         * 规则文件名
         */
        private String ruleFileName;

        /**
         * 规则json文件路径
         */
        private String ruleFileJsonPath;

        /**
         * 业务测试结果
         */
        @Builder.Default
        private String bizTestResult = MSG_NOT_TRIGGERED;

        /**
         * 业务规则结果路径
         */
        private String bizFilePath;

        /**
         * 规则引擎测试结果
         */
        @Builder.Default
        private String ruleTestResult = MSG_NOT_TRIGGERED;

        /**
         * 规则引擎规则结果路径
         */
        private String ruleFilePath;
    }


}
