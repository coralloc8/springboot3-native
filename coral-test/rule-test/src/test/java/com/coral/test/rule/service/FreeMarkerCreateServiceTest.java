package com.coral.test.rule.service;

import com.coral.test.rule.RuleApplicationTest;
import com.coral.test.rule.core.json.JsonUtil;
import com.coral.test.rule.dto.RuleExecuteResponseInfoDTO;
import com.coral.test.rule.dto.RuleReportIndexInfoDTO;
import com.coral.test.rule.util.FreeMarkerUtils;
import com.coral.test.rule.util.MarkdownUtils;
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
    @DisplayName("模板首页json生成测试")
    public void jsonCreate() {
        String json = """
                {
                      "rule_code": "QD0019601",
                      "rule_name": "手术记录中手术或操作疑似漏编",
                      "descs": [
                        "手术记录中手术或操作疑似漏编。以【520000 522600 2024年】版本查DIP分组应用知识库，病组编码包含【C50】为前提条件查询。ICD9复合手术编码知识库对应的组为【A01】。",
                        "【查询】 查询【DIP分组应用知识库】SQL：SELECT * FROM \\"meta_knowledge\\".\\"knowledge_group_dip_application\\" WHERE \\"province_code\\" = '520000' AND \\"western_diagnose_code\\" LIKE '%C50%' AND \\"version\\" = '2024年' LIMIT 1000 OFFSET 0;",
                        "【查询】 查询【ICD9符合手术编码知识库】SQL：SELECT * FROM \\"meta_knowledge\\".\\"knowledge_code_operation_merge\\" WHERE \\"group_code\\" = 'A01' LIMIT 1000 OFFSET 0;",
                        "【逻辑】 复合手术同组：【28.3x03-扁桃体伴腺样体等离子切除术】、【28.2x00-扁桃体切除术不伴腺样增殖体切除术】。【28.2x00-扁桃体切除术不伴腺样增殖体切除术】排除掉不提示。",
                        "【逻辑】 DIP分组：【85.4303-单侧单纯乳房切除术伴区域性淋巴结切除术】、【45.2302-电子结肠镜检查】不属于该西医主诊病组范围手术。都会排除掉。",
                        "【逻辑】 DIP分组：【单侧根治性乳房切除术】手术标准化后出现了多项对照，其中只有【85.4500-单侧根治性乳房切除术】属于该西医主诊病组范围手术，而另一个不满足。故该手术也会排除掉。",
                        "【逻辑】 DIP分组：【85.4301-单侧乳腺改良根治术】属于该西医主诊病组范围手术，而清单手术记录中也没有该手术，符合提示条件，会提示漏编",
                        "【结果】 最终的预期结果应该为【单侧乳腺改良根治术】"
                      ],
                      "expected_result": "",
                      "expected_result_keywords": [
                      ],
                      "expected_result_items": [
                        "单侧乳腺改良根治术"
                      ],
                      "unique_key": "primary_id",
                      "select_keys": [
                        "hosp_code",
                        "stlm_sn"
                      ],
                      "required": {
                        "org_code": "H52262300006",
                        "org_name": "施秉县人民医院",
                        "hosp_code": "SBXRMYY",
                        "patient_id": "2024080919601",
                        "patient_name": "吴*松",
                        "ipt_sn": "2024080919601101",
                        "stlm_sn": "2024080919601301"
                      },
                      "optional": {
                        "ipt_no": "",
                        "mi_record_num": "",
                        "sn": "2024080919601201"
                      },
                      "settings": [
                        {
                          "table": "dwd.dwd_misl_main",
                          "unique_key": "primary_id",
                          "field_mapping": {
                            "sex_code": "1",
                            "sex_name": "男",
                            "sex_code_std": "1",
                            "sex_name_std": "男性",
                            "ipt_dept_code": "A03.04",
                            "ipt_dept_name": "心血管内科专业",
                            "ipt_dept_code_std": "0304",
                            "ipt_dept_name_std": "心血管内科专业",
                            "leave_dept_code": "A03.04",
                            "leave_dept_name": "心血管内科专业",
                            "leave_dept_code_std": "0304",
                            "leave_dept_name_std": "心血管内科专业",
                            "age_year": "20岁",
                            "age_year_clean": 20.00000
                          }
                        },
                        {
                          "table": "dwd.dwd_misl_diag",
                          "unique_key": "primary_id",
                          "select_keys": [
                            "hosp_code",
                            "stlm_sn",
                            "primary_id"
                          ],
                          "pre_sqls": [
                            "delete from ${table} where hosp_code=${hosp_code} and stlm_sn=${stlm_sn};"
                          ],
                          "post_sqls": [],
                          "field_mappings": [
                            {
                              "diag_type_code_std_derive": "020101",
                              "diag_type_name_std_derive": "西医出院主要诊断",
                              "diag_code": "C50.400",
                              "diag_name": "乳房上外象限恶性肿瘤",
                              "diag_code_std": "$C50.400$",
                              "diag_name_std": "$乳房上外象限恶性肿瘤$",
                              "diag_lv1_code_std": "$C50-C50$",
                              "diag_lv1_name_std": "$乳房恶性肿瘤$",
                              "diag_lv2_code_std": "$C50$",
                              "diag_lv2_name_std": "$乳房恶性肿瘤$",
                              "diag_lv3_code_std": "$C50.4$",
                              "diag_lv3_name_std": "$乳房上外象限恶性肿瘤$"
                            }
                          ]
                        },
                        {
                          "table": "dwd.dwd_misl_opn",
                          "unique_key": "primary_id",
                          "select_keys": [
                            "hosp_code",
                            "stlm_sn",
                            "primary_id"
                          ],
                          "pre_sqls": [
                            "delete from ${table} where hosp_code=${hosp_code} and stlm_sn=${stlm_sn};"
                          ],
                          "field_mapping": {
                            "opn_code": "28.3x03",
                            "opn_name": "扁桃体伴腺样体等离子切除术",
                            "opn_code_std": "$28.3x03$",
                            "opn_name_std": "$扁桃体伴腺样体等离子切除术$"
                          }
                        },
                        {
                          "table": "dwd.dwd_emr_opn",
                          "unique_key": "primary_id",
                          "select_keys": [
                            "hosp_code",
                            "ipt_sn",
                            "primary_id"
                          ],
                          "pre_sqls": [
                            "delete from ${table} where hosp_code=${hosp_code} and ipt_sn=${ipt_sn};"
                          ],
                          "field_mappings": [
                            {
                              "hosp_opn_code": "",
                              "hosp_opn_name": "扁桃体切除术不伴腺样增殖体切除术",
                              "hosp_opn_code_std": "$28.2x00$",
                              "hosp_opn_name_std": "$扁桃体切除术不伴腺样增殖体切除术$"
                            },
                            {
                              "hosp_opn_code": "85.4301",
                              "hosp_opn_name": "单侧乳腺改良根治术",
                              "hosp_opn_code_std": "$85.4301$",
                              "hosp_opn_name_std": "$单侧乳腺改良根治术$"
                            },
                            {
                              "hosp_opn_code": "85.4303",
                              "hosp_opn_name": "单侧单纯乳房切除术伴区域性淋巴结切除术",
                              "hosp_opn_code_std": "$85.4303$",
                              "hosp_opn_name_std": "$单侧单纯乳房切除术伴区域性淋巴结切除术$"
                            },
                            {
                              "hosp_opn_code": "45.2302",
                              "hosp_opn_name": "电子结肠镜检查",
                              "hosp_opn_code_std": "$45.2302$",
                              "hosp_opn_name_std": "$电子结肠镜检查$"
                            },
                            {
                              "hosp_opn_code": "",
                              "hosp_opn_name": "单侧根治性乳房切除术",
                              "hosp_opn_code_std": "$85.4500$85.4700$",
                              "hosp_opn_name_std": "$单侧根治性乳房切除术$单侧扩大根治性乳房切除术$"
                            }
                          ]
                        }
                      ]
                    }
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("json", json);

        String md = FreeMarkerUtils.create("rule_json.md.ftl", map);

        System.out.println(md);

        String html = MarkdownUtils.markdownToHtml(md);

        System.out.println(html);
    }

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
