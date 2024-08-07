package com.coral.test.rule.dto;

import com.coral.test.rule.core.json.JsonUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 规则配置信息
 *
 * @author huss
 * @date 2024/7/2 10:45
 * @packageName com.coral.test.rule.dto.req
 * @className RuleConfigInfoDTO
 */
@ToString(callSuper = true)
@Schema(description = "规则配置入参")
@Valid
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RuleConfigInfoDTO {

    @Schema(description = "文件名")
    private String fileName;

    @NotBlank(message = "规则编码不能为空")
    @Schema(description = "规则编码")
    private String ruleCode;

    @NotBlank(message = "规则名称不能为空")
    @Schema(description = "规则名称")
    private String ruleName;

    @NotBlank(message = "规则测试说明不能为空")
    @Schema(description = "规则测试说明")
    private String desc;

    @Schema(description = "唯一键（此处配置为全局的，下面的局部会覆盖此处全局的配置）")
    private String uniqueKey;

    @Schema(description = "查询键（此处配置为全局的，下面的局部会覆盖此处全局的配置）")
    private List<String> selectKeys;

//    @Schema(description = "删除键（此处配置为全局的，下面的局部会覆盖此处全局的配置）")
//    private List<String> deleteKeys;

    @NotNull(message = "规则测试必填参数不能为空")
    @Schema(description = "规则测试必填参数")
    private Map<String, Object> required;

    @Schema(description = "规则测试可选参数")
    private Map<String, Object> optional;

    @Schema(description = "规则测试字段映射")
    private List<Setting> settings;

    /**
     * 获取数据唯一标识
     *
     * @return
     */
    public String getDataUniqueKey() {
        if (Objects.isNull(getRequired())) {
            return "";
        }
        return DigestUtils.md5Hex(JsonUtil.toJson(new TreeMap<>(getRequired())));
    }


    @ToString(callSuper = true)
    @Schema(description = "规则测试字段映射")
    @Valid
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Setting {

        @NotBlank(message = "表名不能为空")
        @Schema(description = "表名")
        private String table;

        @Schema(description = "字段")
        private String field;

        @Schema(description = "字段值")
        private String fieldValue;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "唯一键（局部，会覆盖全局配置）")
        private String uniqueKey;

        @Schema(description = "查询键（局部，会覆盖全局配置）")
        private List<String> selectKeys;

        @Schema(description = "前置SQL语句")
        private List<String> preSqls;

        @Schema(description = "后置SQL语句")
        private List<String> postSqls;

//        @Schema(description = "删除键（局部，会覆盖全局配置）")
//        private List<String> deleteKeys;

        @Schema(description = "字段映射")
        private Map<String, Object> fieldMapping;

        @Schema(description = "字段映射集")
        private List<Map<String, Object>> fieldMappings;

    }
}
