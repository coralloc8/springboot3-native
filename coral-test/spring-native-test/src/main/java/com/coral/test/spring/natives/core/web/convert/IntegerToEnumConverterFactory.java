package com.coral.test.spring.natives.core.web.convert;

import com.coral.test.spring.natives.core.enums.IEnum;
import com.coral.test.spring.natives.core.utils.EnumFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 替换spring默认的字符串转enum
 *
 * @author huss
 */
@ReadingConverter
@Slf4j
public class IntegerToEnumConverterFactory implements ConverterFactory<Integer, IEnum<?, ?>> {

    @Override
    public <T extends IEnum<?, ?>> Converter<Integer, T> getConverter(Class<T> targetType) {
        return new StringToEnum(EnumFactory.getEnumType(targetType));
    }

    private static class StringToEnum<T extends IEnum<?, ?>> implements Converter<Integer, T> {
        private final Class<T> enumType;

        public StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(Integer source) {
            if (Objects.isNull(source)) {
                // It's an empty enum identifier: reset the enum value to null.
                return null;
            }

            if (IEnum.class.isAssignableFrom(this.enumType)) {
                Class clazz = this.enumType;
                try {
                    T t = (T) EnumFactory.findBy(source.toString(), clazz).get();
                    return t;
                } catch (Exception e) {
                    log.error(">>>>>convert enum error:", e);
                }
            }
            return null;
        }
    }



}
