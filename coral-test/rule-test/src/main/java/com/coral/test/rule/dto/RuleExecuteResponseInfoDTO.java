package com.coral.test.rule.dto;

import cn.hutool.core.collection.CollUtil;
import com.coral.test.rule.core.json.JsonUtil;
import com.coral.test.rule.util.RuleParseHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 规则执行响应信息
 *
 * @author huss
 * @date 2024/7/16 17:48
 * @packageName com.coral.test.rule.dto
 * @className RuleExecuteResponseInfoDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "规则执行响应信息")
@Data
@Slf4j
public class RuleExecuteResponseInfoDTO {

    /**
     * 解析规则数据
     *
     * @param response
     * @param ruleConfigInfo
     * @param ruleExecute
     * @return
     */
    public static Optional<RuleExecuteResponseInfoDTO> parse(String response, RuleConfigInfoDTO ruleConfigInfo, RuleExecuteInfoDTO ruleExecute) {
        if (StringUtils.isBlank(response)) {
            return Optional.empty();
        }
        try {
            final String errorCodeKey = "errorCode";
            final String errorMsgKey = "errorMsg";
            final String dataKey = "data";
            final String ruleTipsNodeResultKey = "ruleTipsNodeResult";
            final String tipsNodeKey = "tipsNode";
            final String ruleDetailListKey = "ruleDetailList";
            final String ruleCodeKey = "ruleCode";
            final String qualityFieldListKey = "qualityFieldList";
            final String fieldPromptsKey = "fieldPrompts";
            final String keysKey = "keys";
            //
            final String ruleNameKey = "ruleName";
            final String resultAdviceKey = "resultAdvice";
            final String specialDescKey = "specialDesc";
            final String resultDescKey = "resultDesc";
            //
            final String resultAdviceItemsKey = "resultAdviceItems";
            final String nameKey = "name";
            //
            final String specialDescListKey = "specialDescList";
            final String diagnosisNameKey = "diagnosisName";
            final String diagnosisStandardKey = "diagnosisStandard";
            final String treatmentPlansKey = "treatmentPlans";
            //
            final String basisListKey = "basisList";
            final String moduleNameKey = "moduleName";
            final String indexNameKey = "indexName";
            final String tipTitleKey = "tipTitle";
            final String tipContentKey = "tipContent";


            Map<String, Object> map = JsonUtil.toMap(response);
            if (!"0".equals(map.getOrDefault(errorCodeKey, ""))) {
                log.info("【规则数据解析】失败。规则：[{}]。返参提示：{}。", ruleConfigInfo.getRuleCode(), map.getOrDefault(errorMsgKey, ""));
                return Optional.empty();
            }
            map = (Map<String, Object>) map.get(dataKey);
            List<Map<String, Object>> ruleTipsNodeResult = (List<Map<String, Object>>) map.get(ruleTipsNodeResultKey);

            Map<String, Object> oneRuleResult = !ruleTipsNodeResult.isEmpty() ? ruleTipsNodeResult.get(0) : new HashMap<>();
            if (StringUtils.isNotBlank(ruleExecute.getApiKey())) {
                oneRuleResult = ruleTipsNodeResult.stream()
                        .filter(res -> toString(res.getOrDefault(tipsNodeKey, "")).equals(ruleExecute.getApiKey()))
                        .findFirst()
                        .orElse(Collections.emptyMap());
            }
            if (CollUtil.isEmpty(oneRuleResult)) {
                log.info("【规则数据解析】失败。规则：[{}]。没有质控结果。", ruleConfigInfo.getRuleCode());
                return Optional.empty();
            }
            // 具体提示的规则
            ruleTipsNodeResult = (List<Map<String, Object>>) oneRuleResult.get(ruleDetailListKey);

            oneRuleResult = ruleTipsNodeResult.stream()
                    .filter(res -> toString(res.getOrDefault(ruleCodeKey, "")).equals(ruleConfigInfo.getRuleCode()))
                    .findFirst().orElse(Collections.emptyMap());
            if (CollUtil.isEmpty(oneRuleResult)) {
                log.info("【规则数据解析】失败。规则：[{}] 没有质控结果。", ruleConfigInfo.getRuleCode());
                return Optional.empty();
            }
            // 规则数据解析
            List<String> resultAdviceItems = new ArrayList<>();
            List<ItemEvidenceInfo> itemEvidenceInfos = new ArrayList<>();
            List<Map<String, Object>> resultAdviceItemInfos = ((List<Map<String, Object>>) oneRuleResult.get(resultAdviceItemsKey));
            if (CollUtil.isNotEmpty(resultAdviceItemInfos)) {
                resultAdviceItems = resultAdviceItemInfos.stream()
                        .map(item -> toString(item.getOrDefault(nameKey, "")))
                        .filter(StringUtils::isNotBlank)
                        .distinct()
                        .collect(Collectors.toList());
                Map<String, Map<String, Object>> specialDescListMap = ((List<Map<String, Object>>) oneRuleResult.getOrDefault(specialDescListKey, Collections.emptyList())).stream()
                        .collect(Collectors.toMap(spec -> toString(spec.getOrDefault(diagnosisNameKey, "")), Function.identity(), (t1, t2) -> t2));

                itemEvidenceInfos = resultAdviceItems.stream()
                        .filter(specialDescListMap::containsKey)
                        .map(item -> {
                            Map<String, Object> specialDesc = specialDescListMap.get(item);
                            List<Map<String, Object>> basisList = ((List<Map<String, Object>>) specialDesc.get(basisListKey));
                            List<EvidenceDetailInfo> evidenceDetailInfos = new ArrayList<>();

                            if (CollUtil.isNotEmpty(basisList)) {
                                evidenceDetailInfos = basisList.stream()
                                        .map(basis -> EvidenceDetailInfo.builder()
                                                .moduleName(toString(basis.getOrDefault(moduleNameKey, "")))
                                                .indexName(toString(basis.getOrDefault(indexNameKey, "")))
                                                .tipTitle(toString(basis.getOrDefault(tipTitleKey, "")))
                                                .tipContent(toString(basis.getOrDefault(tipContentKey, "")))
                                                .build())
                                        .collect(Collectors.toList());
                            }

                            return ItemEvidenceInfo.builder()
                                    .adviceItem(item)
                                    .diagnosisStandard(toString(specialDesc.getOrDefault(diagnosisStandardKey, "")))
                                    .treatmentPlans((List<String>) specialDesc.getOrDefault(treatmentPlansKey, Collections.emptyList()))
                                    .evidenceDetailInfos(evidenceDetailInfos)
                                    .build();
                        }).collect(Collectors.toList());
            }
            // 提示字段解析
            ruleTipsNodeResult = (List<Map<String, Object>>) oneRuleResult.get(qualityFieldListKey);
            List<String> fieldPromptKeys = new ArrayList<>();
            if (CollUtil.isNotEmpty(ruleTipsNodeResult)) {
                //
                fieldPromptKeys = ruleTipsNodeResult.stream()
                        .filter(res -> ((List<Map<String, Object>>) res.getOrDefault(fieldPromptsKey, Collections.emptyList())).stream()
                                .anyMatch(prompt -> toString(prompt.getOrDefault(ruleCodeKey, "")).equals(ruleConfigInfo.getRuleCode()))
                        ).flatMap(res -> ((List<String>) res.getOrDefault(keysKey, new ArrayList<>())).stream())
                        .distinct()
                        .collect(Collectors.toList());
            }
            String fileNameWithoutType = RuleParseHelper.getInstance().parseFilePrefixName(ruleConfigInfo.getFileName());
            // 数据组装
            RuleExecuteResponseInfoDTO info = RuleExecuteResponseInfoDTO.builder()
                    .ruleCode(toString(oneRuleResult.getOrDefault(ruleCodeKey, "")))
                    .ruleName(toString(oneRuleResult.getOrDefault(ruleNameKey, "")))
                    .ruleFileName(fileNameWithoutType)
                    .apiService(ruleExecute.getApiService())
                    .ruleDescs(ruleConfigInfo.getDescs())
                    .expectedResult(ruleConfigInfo.getExpectedResult())
                    .expectedResultKeywords(ruleConfigInfo.getExpectedResultKeywords())
                    .expectedResultItems(ruleConfigInfo.getExpectedResultItems())
                    .resultAdvice(toString(oneRuleResult.getOrDefault(resultAdviceKey, "")))
                    .resultDesc(toString(oneRuleResult.getOrDefault(resultDescKey, "")))
                    .specialDesc(Boolean.valueOf(toString(oneRuleResult.getOrDefault(specialDescKey, false))))
                    .promptFields(fieldPromptKeys)
                    .resultAdviceItems(resultAdviceItems)
                    .itemEvidenceInfos(itemEvidenceInfos)
                    .build();
            log.info("【规则数据解析】成功。规则：[{}]。", ruleConfigInfo.getRuleCode());
            return Optional.of(info);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("规则数据解析失败。", e);
        }
        return Optional.empty();

    }

    private static String toString(Object obj) {
        boolean notBlank = Objects.nonNull(obj) && StringUtils.isNotBlank(obj.toString());
        if (notBlank) {
            return obj.toString().replaceAll(" ", "").replaceAll("[\t\n\s]", ";");
        }
        return "";
    }

    public static void main(String[] args) {
        String str = """
                升主动脉内径增宽                  \s
                        主动脉瓣钙化并反流(少量)
                        左心室舒张功能减低
                """;
        System.out.println(str.replaceAll(" ", "").replaceAll("[\t\n\s]", ";"));
    }


    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 规则说明
     */
    private List<String> ruleDescs;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则文件名
     */
    private String ruleFileName;

    /**
     * 服务名
     */
    private String apiService;

    /**
     * 预期结果
     */
    private String expectedResult;

    /**
     * 预期结果关键字列表
     */
    private List<String> expectedResultKeywords;

    /**
     * 预期结果细项
     */
    private List<String> expectedResultItems;

    /**
     * 规则建议
     */
    private String resultAdvice;

    /**
     * 规则结论说明
     */
    private String resultDesc;

    /**
     * 是否包含特殊说明（即循证依据）
     */
    private Boolean specialDesc;

    /**
     * 提示字段
     */
    private List<String> promptFields;

    /**
     * 建议细项列表
     */
    private List<String> resultAdviceItems;

    /**
     * 循证依据
     */
    private List<ItemEvidenceInfo> itemEvidenceInfos;


    /**
     * 建议细项 拼接文本（空格分隔）
     *
     * @return
     */
    public String getResultAdviceItemText() {
        return CollUtil.isEmpty(getResultAdviceItems()) ? "" :
                String.join("  ", getResultAdviceItems());
    }

    /**
     * 是否和预期结果一致
     *
     * @return
     */
    public Boolean getTestResult() {
        boolean adviceMatch = false;
        boolean adviceKeywordMatch = false;
        boolean adviceItemMatch = true;

        if (StringUtils.isBlank(getExpectedResult()) && CollUtil.isEmpty(getExpectedResultKeywords())) {
            adviceMatch = true;
            adviceKeywordMatch = true;
        }

        if (StringUtils.isNotBlank(getExpectedResult()) && StringUtils.isNotBlank(getResultAdvice())) {
            adviceMatch = getResultAdvice().contains(getExpectedResult());
        }
        if (CollUtil.isNotEmpty(getExpectedResultKeywords()) && StringUtils.isNotBlank(getResultAdvice())) {
            adviceKeywordMatch = getExpectedResultKeywords().stream().allMatch(getResultAdvice()::contains);
        }
        if (CollUtil.isNotEmpty(getExpectedResultKeywords()) && CollUtil.isNotEmpty(getResultAdviceItems())) {
            adviceKeywordMatch = adviceKeywordMatch || getExpectedResultKeywords().stream()
                    .allMatch(key -> getResultAdviceItems().stream().anyMatch(item -> item.contains(key)));
        }

        if (CollUtil.isNotEmpty(getExpectedResultItems()) && CollUtil.isNotEmpty(getResultAdviceItems())) {
            adviceItemMatch = getExpectedResultItems().stream()
                    .allMatch(key -> getResultAdviceItems().stream().anyMatch(item -> item.contains(key)));
        }

        return (adviceMatch || adviceKeywordMatch) && adviceItemMatch;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ItemEvidenceInfo {

        /**
         * 建议细项
         */
        private String adviceItem;

        /**
         * 诊断标准
         */
        private String diagnosisStandard;

        /**
         * 治疗方案集合
         */
        private List<String> treatmentPlans;

        /**
         * 依据列表
         */
        private List<EvidenceDetailInfo> evidenceDetailInfos;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class EvidenceDetailInfo {
        /**
         * 模块
         */
        private String moduleName;

        /**
         * 指标名称
         */
        private String indexName;

        /**
         * 提示标题
         */
        private String tipTitle;

        /**
         * 提示内容
         */
        private String tipContent;
    }
}
