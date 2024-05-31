package com.coral.test.spring.natives.core.web.convert;


import com.coral.test.spring.natives.core.utils.DatePattern;
import com.coral.test.spring.natives.core.utils.DateTimeUtil;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;

/**
 * 全局字符串转日期
 *
 * @author huss
 */
public class StringToLocalDate implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        if (source.isEmpty()) {
            // It's an empty enum identifier: reset the enum value to null.
            return null;
        }
        return LocalDate.from(DateTimeUtil.parse(source, DatePattern.YYYY_MM_DD_EN));
    }
}
