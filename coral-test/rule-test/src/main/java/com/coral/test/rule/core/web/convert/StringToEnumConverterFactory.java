package com.coral.test.rule.core.web.convert;

import com.coral.test.rule.core.enums.IEnum;
import com.coral.test.rule.core.utils.EnumFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;

/**
 * 替换spring默认的字符串转enum
 *
 * @author huss
 */
@ReadingConverter
@Slf4j
public class StringToEnumConverterFactory implements ConverterFactory<String, IEnum<?, ?>> {

    @Override
    public <T extends IEnum<?, ?>> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnum(EnumFactory.getEnumType(targetType));
    }

    private static class StringToEnum<T extends IEnum<?, ?>> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isBlank(source)) {
                // It's an empty enum identifier: reset the enum value to null.
                return null;
            }
            if (IEnum.class.isAssignableFrom(this.enumType)) {
                Class clazz = this.enumType;
                try {
                    T t = (T) EnumFactory.findBy(source, clazz).get();
                    return t;
                } catch (Exception e) {
                    log.error(">>>>>convert enum error:", e);
                }
            }
            return null;
        }
    }

}
