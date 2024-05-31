package com.coral.test.spring.natives.core.web.convert;


import com.coral.test.spring.natives.core.utils.DatePattern;
import com.coral.test.spring.natives.core.utils.DateTimeUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

/**
 * 全局字符串转时间
 *
 * @author huss
 */
public class StringToLocalTime implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(String source) {
        if (source.isEmpty()) {
            // It's an empty enum identifier: reset the enum value to null.
            return null;
        }
        return LocalTime.from(DateTimeUtil.parse(source, DatePattern.HH_MM_SS_EN));
    }
}
