package com.coral.test.spring.natives.core.web.convert;

import com.coral.test.spring.natives.core.utils.DatePattern;
import com.coral.test.spring.natives.core.utils.DateTimeUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;

/**
 * 全局字符串转日期时间
 *
 * @author huss
 */
public class StringToLocalDateTime implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String source) {
        if (source.isEmpty()) {
            // It's an empty enum identifier: reset the enum value to null.
            return null;
        }
        return LocalDateTime.from(DateTimeUtil.parse(source, DatePattern.YYYY_MM_DD_HH_MM_SS_EN));
    }
}
