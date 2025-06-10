package com.coral.test.rule.config;

import cn.hutool.core.util.IdUtil;
import com.coral.test.rule.core.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 规则执行key
 *
 * @author huss
 * @date 2024/7/10 18:31
 * @packageName com.coral.test.rule.config
 * @className RuleExecuteKey
 */
public interface RuleExecuteKey {

    String UUID = "uuid";
    String NOW = "now";
    String NOW_TIME = "now_time";
    String NOW_DATE = "now_date";
    String SNOW_FLAKE = "snow_flake";

    String TABLE_NAME = "table";

    /**
     * 获取目标key对应的值
     *
     * @param key
     * @return
     */
    static Object getTargetValue(String key, Object defVal) {
        if (StringUtils.isBlank(key)) {
            return defVal;
        }
        return switch (key) {
            case "uuid" -> IdUtil.fastSimpleUUID();
            case "now" -> DateTimeUtil.formatDateTime(LocalDateTime.now());
            case "now_time" -> DateTimeUtil.formatTime(LocalDateTime.now());
            case "now_date" -> DateTimeUtil.formatDate(LocalDate.now());
            case "snow_flake" -> IdUtil.getSnowflake(1, 1).nextIdStr();
            default -> defVal;
        };
    }
}
