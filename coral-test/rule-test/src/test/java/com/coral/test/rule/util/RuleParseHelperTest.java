package com.coral.test.rule.util;

import cn.hutool.core.io.FileUtil;
import com.coral.test.rule.config.RuleExecuteKey;
import com.coral.test.rule.config.RuleProperty;
import com.coral.test.rule.core.json.JsonUtil;
import com.coral.test.rule.core.utils.StrFormatter;
import com.coral.test.rule.dto.RuleConfigInfoDTO;
import com.coral.test.rule.dto.RuleExecuteInfoDTO;
import com.coral.test.rule.dto.SqlInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.util.Asserts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * 规则解析测试
 *
 * @author huss
 * @date 2024/7/12 8:42
 * @packageName com.coral.test.rule.util
 * @className RuleParseHelperTest
 */
@Slf4j
public class RuleParseHelperTest {
    private final static String QD0014401_1 = "QD0014401_1.json";

    @Test
    @DisplayName("返回数据为空时打印日志")
    public void logWhenEmpty() {
        Mono.empty()
                .doOnSuccess(s -> {
                    System.out.println("Mono is empty：" + s);
                })
                .subscribe();


        Mono.fromFuture(CompletableFuture.runAsync(() -> {
            IntStream.range(1, 9).forEach(e -> {
                System.out.println("?????测试能不能执行");
                Flux.just("1111", "222", "333")
                        .filter(data -> {
                            System.out.println("过滤器执行了吗");
                            return true;
                        })
                        .subscribe(list -> System.out.println("Collected List: " + list), // 成功时的回调
                                error -> System.err.println("Error: " + error), // 错误时的回调
                                () -> System.out.println("Completed.") // 完成时的回调
                        );
            });
        })).blockOptional();

    }

    @Test
    @DisplayName("写入文件")
    public void writeFile() {
        String path = "rule-report";
        String fileName = "QD0014401_1.html";
        String fullPath = Path.of(path, fileName).toString();
        System.out.println(fullPath);
//        if (!FileUtil.exist(fullPath)) {
//            FileUtil.createRandomAccessFile()
//        }
        String html = doCreateRuleReportHtml();
        FileUtil.writeString(html, fullPath, StandardCharsets.UTF_8);
        System.out.println("文件写入完成");
    }

    @Test
    @DisplayName("创建规则报表(html)")
    public void createRuleReportHtml() {
        System.out.println(doCreateRuleReportHtml());
    }

    private String doCreateRuleReportHtml() {
        String markdown = """
                [引言]
                <!-- TOC -->
                * [【规则名】](#规则名)
                * [【说明】](#规则说明)
                * [【类型】](#类型)
                * [【结果集】](#结果集)
                    * [【建议】](#建议)
                    * [【建议项】](#建议项)
                    * [【说明】](#说明)
                * [【提示字段】](#提示字段)
                * [【循证依据】](#循证依据)
                    * [【肺癌】](#肺癌)
                    * [【糖尿病】](#糖尿病)
                    * [【腹膜炎】](#腹膜炎)
                    * [【心肌梗死】](#心肌梗死)
                    * [【充血性心力衰竭】](#充血性心力衰竭)
                    * [【原发性肝癌】](#原发性肝癌)
                                 
                <!-- TOC -->
                                 
                                 
                                 
                # 【规则名】
                QD0071001
                # 【规则说明】
                出院小结提示疾病，手动构造规则结果测试中。
                # 【规则类型】
                出院小结提示疾病
                                 
                -------------------
                # 【结果集】
                ## 【建议】
                循证校验智能提示，本例中存在以下疾病诊断/手术/操作/治疗方案依据，供参考。
                ## 【建议项】
                肺癌  糖尿病  腹膜炎  心肌梗死  充血性心力衰竭  严重的心肌炎  原发性肝癌
                ## 【说明】
                                 
                                 
                -------------------
                # 【提示字段】
                * diagList_124410-O69.208_diagCode
                * diagList_124409-O42.000x001_diagCode
                * wtDiagList_124410-O69.208_diagCode
                * wtDiagList_124409-O42.000x001_diagCode
                                 
                -------------------
                # 【循证依据】
                ### 【肺癌】
                | 模块名称 | 指标名称 | 提示标题 | 提示内容 |
                |------|------|------|------|
                | 检验报告结果记录   | B2微球蛋白  3.8   |  临床意义   |  1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。  |
                | 检验报告结果记录   | B2微球蛋白  3.8   |  临床意义   |  1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。  |
                ### 【糖尿病】
                | 模块名称 | 指标名称 | 提示标题 | 提示内容 |
                |------|------|------|------|
                | 检验报告结果记录   | B2微球蛋白  3.8   |  临床意义   |  1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。  |
                ### 【腹膜炎】
                | 模块名称 | 指标名称 | 提示标题 | 提示内容 |
                |------|------|------|------|
                | 检验报告结果记录   | 肌酸酶同功酶  32.4   |  临床意义   |  增高：主要见于心肌梗死、严重的心肌炎和充血性心力衰竭等。  |
                ### 【心肌梗死】
                | 模块名称 | 指标名称 | 提示标题 | 提示内容 |
                |------|------|------|------|
                | 检验报告结果记录   | 肌酸酶同功酶  32.4   |  临床意义   |  增高：主要见于心肌梗死、严重的心肌炎和充血性心力衰竭等。  |
                ### 【充血性心力衰竭】
                | 模块名称 | 指标名称 | 提示标题 | 提示内容 |
                |------|------|------|------|
                | 检验报告结果记录   | 肌酸酶同功酶  32.4   |  临床意义   |  增高：主要见于心肌梗死、严重的心肌炎和充血性心力衰竭等。  |
                ### 【原发性肝癌】
                | 模块名称 | 指标名称 | 提示标题 | 提示内容 |
                |------|------|------|------|
                | 检验报告结果记录   | B2微球蛋白  3.8   |  临床意义   |  1、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG正常：见于急慢性肾炎、肾衰竭等。\\n2、尿β<sub>2</sub>-MG增高，血β<sub>2</sub>-MG正常：见于先天性近曲小管功能缺陷、慢性镉中毒、威尔逊病(Wilson)、肾移植排异反应等。\\n3、血β<sub>2</sub>-MG增高，尿β<sub>2</sub>-MG增高：见于恶性肿瘤（原发性肝癌，肺癌等)、自身免疫性疾病、糖尿病等。  |
                 
                 """;

        return RuleParseHelper.getInstance().createRuleReportHtml(markdown);
    }


    @Test
    @DisplayName("http异步请求")
    public void httpSendAsync() throws ExecutionException, InterruptedException {
        String json1 = """
                  {
                    "desc": "循证校验规则测试",
                    "url": "http://192.168.29.119:9000/api/rule/v1/execute/v2",
                    "method": "POST",
                    "content_type": "application/json",
                    "data": {
                      "appScenario": "QUALITY",
                      "dataType": "1",
                      "hospitalCode": "${hosp_code}",
                      "iptSn": "${ipt_sn}",
                      "module": "VERIFY_EVIDENCE_BASED",
                      "patientId": "${patient_id}",
                      "patientName": "测试XXX",
                      "processingLogicType": "REAL_TIME",
                      "project": "INSURANCE",
                      "qualitySerialNum": "${uuid}",
                      "ruleOrderType": "4",
                      "sn": "${stlm_sn}",
                      "tipsNodes": [
                        "循证校验"
                      ]
                    }
                  }
                """;
        String json2 = """
                {
                  "rule_code": "QD0014401",
                  "rule_name": "检查诊断疑似漏编",
                  "desc": "检查诊断全部都是程序实时提取出来的，并且都不在【组间合并症诊断编码知识库中】",
                  "unique_key": "primary_id",
                  "select_keys": [
                    "hosp_code",
                    "stlm_sn"
                  ],
                  "required": {
                    "org_code": "H41088200229",
                    "org_name": "沁阳市人民医院",
                    "hosp_code": "QYSRMYY",
                    "patient_id": "41088220000000001000736528",
                    "ipt_sn": "202415757",
                    "stlm_sn": "202415757stlm_sn"
                  },
                  "optional": {
                    "ipt_no": "",
                    "mi_record_num": "",
                    "sn": ""
                  },
                  "settings": [
                    {
                      "table": "dwd.dwd_misl_main",
                      "unique_key": "primary_id",
                      "field_mapping": {
                        "sex_code": "2",
                        "sex_name": "女",
                        "sex_code_std": "2",
                        "sex_name_std": "女性",
                        "ipt_dept_code": "A03.04",
                        "ipt_dept_name": "心血管内科专业",
                        "ipt_dept_code_std": "0304",
                        "ipt_dept_name_std": "心血管内科专业",
                        "leave_dept_code": "A03.04",
                        "leave_dept_name": "心血管内科专业",
                        "leave_dept_code_std": "0304",
                        "leave_dept_name_std": "心血管内科专业"
                      }
                    },
                    {
                      "table": "dwd.dwd_misl_diag",
                      "unique_key": "primary_id",
                      "select_keys": [
                        "hosp_code",
                        "ipt_sn",
                        "primary_id"
                      ],
                      "field_mappings": [
                        {
                          "primary_id": "2024150271",
                          "diag_type_code_std_derive": "020102",
                          "diag_type_name_std_derive": "西医出院其他诊断",
                          "diag_code": "A01.000x017",
                          "diag_name": "伤寒轻型",
                          "diag_code_std": "$A01.000x017$",
                          "diag_name_std": "$伤寒轻型$",
                          "diag_lv1_code_std": "$A00-A09$",
                          "diag_lv1_name_std": "$肠道传染病肠道传染病$",
                          "diag_lv2_code_std": "$A01$",
                          "diag_lv2_name_std": "$伤寒和副伤寒$",
                          "diag_lv3_code_std": "$A01.0$",
                          "diag_lv3_name_std": "$伤寒$"
                        },
                        {
                          "diag_type_code_std_derive": "020101",
                          "diag_type_name_std_derive": "西医出院主要诊断",
                          "diag_code": "A02.900",
                          "diag_name": "沙门菌感染",
                          "diag_code_std": "$A02.900$",
                          "diag_name_std": "$沙门菌感染$",
                          "diag_lv1_code_std": "$A00-A09$",
                          "diag_lv1_name_std": "$肠道传染病$",
                          "diag_lv2_code_std": "$A02$",
                          "diag_lv2_name_std": "$其他沙门菌感染$",
                          "diag_lv3_code_std": "$A02.9$",
                          "diag_lv3_name_std": "$未特指的沙门菌感染$"
                        }
                      ]
                    }
                  ]
                }
                """;
        RuleConfigInfoDTO ruleConfigReq = JsonUtil.parse(json2, RuleConfigInfoDTO.class);
        RuleExecuteInfoDTO ruleExecute = JsonUtil.parse(json1, RuleExecuteInfoDTO.class);
        CompletableFuture<String> future = RuleParseHelper.getInstance().httpSendAsync(ruleExecute, ruleConfigReq);

        System.out.println(future.get());
    }

    @Test
    @DisplayName("MD5加密测试")
    public void md5() {
        String res = DigestUtils.md5Hex("coral");
        Assertions.assertEquals(res, "d2ebed4eaf58509dcc358e1782c38fea");
    }

    @Test
    @DisplayName("查找文件")
    public void findFiles() {
        List<String> rules = RuleParseHelper.getInstance().findFiles(RuleProperty.RULE_EXECUTE_PATH, null);
        System.out.println(rules);
    }

    @Test
    @DisplayName("读取规则配置文件")
    public void readJsonFromFile() {
        String ruleJson = RuleParseHelper.getInstance().readFileContent(RuleProperty.RULE_EXECUTE_PATH, QD0014401_1);
        Asserts.notBlank(ruleJson, "规则配置");
        //
        RuleConfigInfoDTO ruleConfigInfoDTO = JsonUtil.parse(ruleJson, RuleConfigInfoDTO.class);
        Asserts.notNull(ruleConfigInfoDTO, "规则配置DTO");
        Asserts.notNull(ruleConfigInfoDTO.getSettings(), "规则测试字段映射");
    }

    @Test
    @DisplayName("读取规则配置文件并解析成sql")
    public void buildSql() {
        String ruleJson = RuleParseHelper.getInstance().readFileContent(RuleProperty.RULE_EXECUTE_PATH, QD0014401_1);
        RuleConfigInfoDTO ruleConfigInfoDTO = JsonUtil.parse(ruleJson, RuleConfigInfoDTO.class);
        List<SqlInfoDTO> sqlInfoDTOS = RuleParseHelper.getInstance().buildSql(ruleConfigInfoDTO);

        System.out.println(JsonUtil.toJson(sqlInfoDTOS));
    }


    @Test
    @DisplayName("解析sql中的占位符并全部替换")
    public void parseSql() {
        String sql = """
                delete from ${table} where hosp_code=${hosp_code} and ipt_sn=${ipt_sn};
                """;

        Map<String, Object> required = new HashMap<>();
        required.put("table", "dwd.dwd_misl_diag");
        required.put("hosp_code", "QYSRMYY");
        required.put("ipt_sn", "202413796");

        String pattern = "\\$\\{.*?}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(sql);
        while (m.find()) {
            String fieldKey = m.group().replaceAll("\\$\\{|}", "");
            Object value = required.getOrDefault(fieldKey, "");
            String formatSql = RuleExecuteKey.TABLE_NAME.equalsIgnoreCase(fieldKey) ? "{}" : "'{}'";
            sql = sql.replaceAll(StrFormatter.format("\\$\\{{}}", fieldKey), StrFormatter.format(formatSql, value.toString()));
        }
        System.out.println(sql);
    }
}
