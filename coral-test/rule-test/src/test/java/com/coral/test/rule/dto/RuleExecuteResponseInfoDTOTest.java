package com.coral.test.rule.dto;

import com.coral.test.rule.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 规则执行响应信息测试
 *
 * @author huss
 * @date 2024/7/24 17:12
 * @packageName com.coral.test.rule.dto
 * @className RuleExecuteResponseInfoDTOTest
 */
@Slf4j
public class RuleExecuteResponseInfoDTOTest {
    @Test
    @DisplayName("解析规则数据")
    public void parseRule() {
        var response = """
                  {
                    	"data": {
                    		"ruleTipsNodeResult": [{
                    			"tipsNode": "循证校验",
                    			"ruleDetailList": [{
                    				"twoCategory": "内容填写规范质控",
                    				"threeCategory": "诊断编码循证分析",
                    				"ruleCode": "QD0071001",
                    				"ruleDesignCode": "R00710",
                    				"ruleName": "出院小结提示疾病",
                    				"score": null,
                    				"peremptory": false,
                    				"ruleNature": null,
                    				"ruleSource": "1.2016版《国家卫生计生委办公厅关于印发住院病案首页数据填写质量规范（暂行）\\n2.疾病和有关健康问题的国际统计分类\\n3.病案信息学（第二版）\\n4.国家疾病分类与代码（ICD-10）应用指导手册",
                    				"resultAdvice": "循证校验智能提示，本例中存在以下疾病诊断/手术/操作/治疗方案依据，供参考。",
                    				"resultAdviceItemPrefix": "",
                    				"resultAdviceItems": [{
                    					"category": null,
                    					"code": "",
                    					"name": "急性上呼吸道感染"
                    				}],
                    				"resultDesc": "",
                    				"specialDesc": true,
                    				"specialDescText": "诊断",
                    				"specialDescList": [{
                    					"diagnosisCode": "",
                    					"diagnosisName": "急性上呼吸道感染",
                    					"diagnosisStandard": "诊断标准",
                    					"treatmentPlan": null,
                    					"treatmentPlans": ["我说及治疗放肆"],
                    					"basisName": "诊断",
                    					"basisList": [{
                    						"moduleName": "出院小结",
                    						"indexCode": "",
                    						"indexName": "急性上呼吸道感染,,,,",
                    						"tipTitle": '治疗方案',
                    						"tipContent": '1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。'
                    					},
                    					{
                    						"moduleName": "出院小结",
                    						"indexCode": "",
                    						"indexName": "急性上呼吸道感染222222,,,,",
                    						"tipTitle": null,
                    						"tipContent": null
                    					}]
                    				}],
                    				"treadType": null
                    			}],
                    			"qualityFieldList": [{
                    				"keys": ["companyPostcode"],
                    				"fieldPrompts": [{
                    					"ruleCode": "QD0099503",
                    					"ruleName": "工作单位邮编与职业冲突",
                    					"ruleNature": null
                    				}],
                    				"fieldQCResultList": null
                    			}]
                    		}],
                    		"diagnosis": null,
                    		"nodeSize": 1
                    	},
                    	"success": true,
                    	"errorCode": "0",
                    	"errorMsg": "成功"
                    }
                """;
        RuleConfigInfoDTO ruleConfigInfo = RuleConfigInfoDTO.builder()
                .ruleCode("QD0071001")
                .desc("出院小结提示疾病自动化测试")
                .build();
        RuleExecuteResponseInfoDTO ruleExecuteResponseInfo = RuleExecuteResponseInfoDTO.parse(response, ruleConfigInfo, "循证校验").get();

        System.out.println(JsonUtil.toJson(ruleExecuteResponseInfo));
    }

}
