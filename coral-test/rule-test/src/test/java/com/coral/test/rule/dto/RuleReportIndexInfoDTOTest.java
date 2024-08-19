package com.coral.test.rule.dto;

import com.coral.test.rule.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 规则报表首页信息
 *
 * @author huss
 * @date 2024/8/19 10:53
 * @packageName com.coral.test.rule.dto
 * @className RuleReportIndexInfoDTOTest
 */
@Slf4j
public class RuleReportIndexInfoDTOTest {

    @Test
    @DisplayName("解析规则首页数据")
    public void create() {
        Set<String> fileNames = Set.of(
                "R001_cs",
                "R001_bc",
                "R002_test1",
                "R002_test2",
                "R003_test1"
        );
        List<RuleExecuteResponseInfoDTO> list = new ArrayList<>();
        RuleExecuteResponseInfoDTO res = RuleExecuteResponseInfoDTO.builder()
                .ruleCode("R001")
                .ruleFileName("R001_cs")
                .apiService("insurance")
                .resultAdvice("成功")
                .build();
        list.add(res);
        res = RuleExecuteResponseInfoDTO.builder()
                .ruleCode("R001")
                .ruleFileName("R001_cs")
                .apiService("rule")
                .resultAdvice("成功")
                .expectedResult("失败")
                .build();
        list.add(res);

        res = RuleExecuteResponseInfoDTO.builder()
                .ruleCode("R001")
                .ruleFileName("R001_bc")
                .apiService("insurance")
                .resultAdvice("成功")
                .expectedResult("成功")
                .build();
        list.add(res);


        List<RuleReportIndexInfoDTO> indexInfos = RuleReportIndexInfoDTO.create(list,fileNames);


        System.out.println(JsonUtil.toJson(indexInfos));


    }
}
