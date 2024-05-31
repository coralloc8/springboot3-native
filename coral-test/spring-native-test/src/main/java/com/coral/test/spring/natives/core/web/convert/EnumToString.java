package com.coral.test.spring.natives.core.web.convert;

import com.coral.test.spring.natives.core.enums.IEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Objects;

/**
 * 枚举转字符串
 *
 * @author huss
 * @date 2024/4/3 16:49
 * @packageName com.coral.test.spring.natives.core.web.convert
 * @className EnumToString
 */
@WritingConverter
public class EnumToString implements Converter<IEnum<?, String>, String> {
    @Override
    public String convert(IEnum<?, String> source) {
        if (Objects.isNull(source)) {
            // It's an empty enum identifier: reset the enum value to null.
            return null;
        }
        return source.getCode();
    }
}