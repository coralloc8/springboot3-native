package com.coral.test.rule.service;

import com.coral.test.rule.RuleApplicationTest;
import com.coral.test.rule.core.json.JsonUtil;
import com.coral.test.rule.dto.RuleExecuteResponseInfoDTO;
import com.coral.test.rule.dto.RuleReportIndexInfoDTO;
import com.coral.test.rule.util.FreeMarkerUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * 模板测试
 *
 * @author huss
 * @date 2024/7/24 10:49
 * @packageName com.coral.test.rule.service
 * @className FreeMarkerCreateServiceTest
 */
@Slf4j
public class FreeMarkerCreateServiceTest extends RuleApplicationTest {
    @Test
    @DisplayName("模板首页index生成测试")
    public void indexCreate() {
        String json = """
                [{
                    "ruleCode": "R002",
                    "ruleName": "",
                    "results": [{
                        "ruleFileName": "R002_test1",
                        "bizTestResult": "未触发",
                        "bizFilePath": "",
                        "ruleTestResult": "未触发",
                        "ruleFilePath": ""
                    }, {
                        "ruleFileName": "R002_test2",
                        "bizTestResult": "未触发",
                        "bizFilePath": "",
                        "ruleTestResult": "未触发",
                        "ruleFilePath": ""
                    }]
                }, {
                    "ruleCode": "R003",
                    "ruleName": "",
                    "results": [{
                        "ruleFileName": "R003_test1",
                        "bizTestResult": "未触发",
                        "bizFilePath": "",
                        "ruleTestResult": "未触发",
                        "ruleFilePath": ""
                    }]
                }, {
                    "ruleCode": "R001",
                    "ruleName": "",
                    "results": [{
                        "ruleFileName": "R001_bc",
                        "bizTestResult": "成功",
                        "bizFilePath": "R001_bc_insurance.html",
                        "ruleTestResult": "未触发",
                        "ruleFilePath": ""
                    }, {
                        "ruleFileName": "R001_cs",
                        "bizTestResult": "成功",
                        "bizFilePath": "R001_cs_insurance.html",
                        "ruleTestResult": "失败",
                        "ruleFilePath": "R001_cs_rule.html"
                    }]
                }]
                                
                """;

        Set<String> ruleCodes = Set.of(
                "R001",
                "R002",
                "R003"
        );

        List<RuleReportIndexInfoDTO> reportIndexs = JsonUtil.parseArray(json, RuleReportIndexInfoDTO.class);

        Map<String, Object> map = new HashMap<>();
        map.put("ruleCodes", ruleCodes);
        map.put("indexs", reportIndexs);

        String md = FreeMarkerUtils.create("rule_index.md.ftl", map);

        System.out.println(md);
    }


    @Test
    @DisplayName("模板测试")
    public void create() {
        RuleExecuteResponseInfoDTO response = RuleExecuteResponseInfoDTO.builder()
                .ruleCode("QD0071001")
                .ruleName("出院小结提示疾病")
                .ruleDescs(List.of("出院小结提示疾病，手动构造规则结果测试中。"))
                .resultAdvice("循证校验智能提示，本例中存在以下疾病诊断/手术/操作/治疗方案依据，供参考。")
                .resultDesc("")
                .specialDesc(true)
                .promptFields(Arrays.asList(
                        "diagList_124410-O69.208_diagCode",
                        "diagList_124409-O42.000x001_diagCode",
                        "wtDiagList_124410-O69.208_diagCode",
                        "wtDiagList_124409-O42.000x001_diagCode"
                ))
                .resultAdviceItems(Arrays.asList(
                        "肺癌",
                        "糖尿病",
                        "腹膜炎",
                        "心肌梗死",
                        "充血性心力衰竭",
                        "严重的心肌炎",
                        "原发性肝癌"
                ))
                .itemEvidenceInfos(Arrays.asList(
                        RuleExecuteResponseInfoDTO.ItemEvidenceInfo.builder()
                                .adviceItem("肺癌")
                                .evidenceDetailInfos(Arrays.asList(
                                        RuleExecuteResponseInfoDTO.EvidenceDetailInfo.builder()
                                                .moduleName("检验报告结果记录")
                                                .indexName("B2微球蛋白  3.8")
                                                .tipTitle("临床意义")
                                                .tipContent("1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。")
                                                .build(),
                                        RuleExecuteResponseInfoDTO.EvidenceDetailInfo.builder()
                                                .moduleName("检验报告结果记录")
                                                .indexName("B2微球蛋白  3.8")
                                                .tipTitle("临床意义")
                                                .tipContent("1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。")
                                                .build()
                                ))
                                .build(),
                        RuleExecuteResponseInfoDTO.ItemEvidenceInfo.builder()
                                .adviceItem("糖尿病")
                                .evidenceDetailInfos(Collections.singletonList(
                                        RuleExecuteResponseInfoDTO.EvidenceDetailInfo.builder()
                                                .moduleName("检验报告结果记录")
                                                .indexName("B2微球蛋白  3.8")
                                                .tipTitle("临床意义")
                                                .tipContent("1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。")
                                                .build()
                                ))
                                .build(),
                        RuleExecuteResponseInfoDTO.ItemEvidenceInfo.builder()
                                .adviceItem("腹膜炎")
                                .evidenceDetailInfos(Collections.singletonList(
                                        RuleExecuteResponseInfoDTO.EvidenceDetailInfo.builder()
                                                .moduleName("检验报告结果记录")
                                                .indexName("肌酸酶同功酶  32.4")
                                                .tipTitle("临床意义")
                                                .tipContent("增高：主要见于心肌梗死、严重的心肌炎和充血性心力衰竭等。")
                                                .build()
                                ))
                                .build(),
                        RuleExecuteResponseInfoDTO.ItemEvidenceInfo.builder()
                                .adviceItem("心肌梗死")
                                .evidenceDetailInfos(Collections.singletonList(
                                        RuleExecuteResponseInfoDTO.EvidenceDetailInfo.builder()
                                                .moduleName("检验报告结果记录")
                                                .indexName("肌酸酶同功酶  32.4")
                                                .tipTitle("临床意义")
                                                .tipContent("增高：主要见于心肌梗死、严重的心肌炎和充血性心力衰竭等。")
                                                .build()
                                ))
                                .build(),
                        RuleExecuteResponseInfoDTO.ItemEvidenceInfo.builder()
                                .adviceItem("充血性心力衰竭")
                                .evidenceDetailInfos(Collections.singletonList(
                                        RuleExecuteResponseInfoDTO.EvidenceDetailInfo.builder()
                                                .moduleName("检验报告结果记录")
                                                .indexName("肌酸酶同功酶  32.4")
                                                .tipTitle("临床意义")
                                                .tipContent("增高：主要见于心肌梗死、严重的心肌炎和充血性心力衰竭等。")
                                                .build()
                                ))
                                .build(),
                        RuleExecuteResponseInfoDTO.ItemEvidenceInfo.builder()
                                .adviceItem("原发性肝癌")
                                .evidenceDetailInfos(Collections.singletonList(
                                        RuleExecuteResponseInfoDTO.EvidenceDetailInfo.builder()
                                                .moduleName("检验报告结果记录")
                                                .indexName("B2微球蛋白  3.8")
                                                .tipTitle("临床意义")
                                                .tipContent("1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。")
                                                .build()
                                ))
                                .build()
                ))
                .build();
//        Optional<String> res = freeMarkerCreateService.create("rule_report.md.ftl", response).blockOptional();
//        System.out.println(res.get());
    }
}
