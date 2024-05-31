package com.coral.test.spring.simple.core.opendoc.config;


import com.coral.test.spring.simple.core.enums.IEnum;
import com.coral.test.spring.simple.core.opendoc.constants.DefConstant;
import com.coral.test.spring.simple.core.opendoc.utils.SchemaUtil;
import com.coral.test.spring.simple.core.utils.EnumFactory;
import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huss
 * @version 1.0
 * @className MyPropertyCustomizer
 * @description 自定义schema
 * @date 2021/9/13 10:58
 */
@Component
@Slf4j
public class MyPropertyCustomizer implements PropertyCustomizer {

    /**
     * 参考 {@link org.springdoc.core.converters.ResponseSupportConverter}
     *
     * @param property
     * @param type
     * @return
     */
    @Override
    public Schema customize(Schema property, AnnotatedType type) {
        JavaType javaType = Json.mapper().constructType(type.getType());
//        property = buildTimeSchema(property, type, javaType);
        property = buildEnumSchema(property, type, javaType);
        return property;
    }

//    /**
//     * 时间转化
//     *
//     * @param property
//     * @param type
//     * @return
//     */
//    private Schema buildTimeSchema(Schema property, AnnotatedType type, JavaType javaType) {
//        boolean isTemporalAccessor = TemporalAccessor.class.isAssignableFrom(javaType.getRawClass());
//
//        if (!isTemporalAccessor) {
//            return property;
//        }
//        log.info(">>>>>time重写...type:{}", type.getType().getTypeName());
//        Schema newSchema = new DateTimeSchema();
//        SchemaUtil.copySchema(property, newSchema);
//        return newSchema;
//
//    }

    /**
     * 创建枚举schema
     *
     * @param property
     * @param type
     * @return
     */
    private Schema buildEnumSchema(Schema property, AnnotatedType type, JavaType javaType) {
        boolean isIEnum = IEnum.class.isAssignableFrom(javaType.getRawClass());
        if (!isIEnum) {
            return property;
        }
        Class<? extends IEnum> clazz = (Class<IEnum>) javaType.getRawClass();
        String desc = EnumFactory.description(clazz);

        if (DefConstant.DOC_FORMAT_ENUM.equals(property.getFormat())) {
            log.debug(">>>>>enum重写...type:{}", type.getType().getTypeName());
            //返参碰到枚举类型的重写数据结构
            property = SchemaUtil.getSchema(EnumSchema.class, javaType.getRawClass().getSimpleName(),
                    property.getDescription() + "  " + desc);
            return property;
        }
        log.debug(">>>>>enum重新设置description和enums...type:{}", type.getType().getTypeName());
        desc = property.getDescription() + "   " + desc;
        property.description(desc);

        //设置枚举值
        IEnum[] enums = clazz.getEnumConstants();
        List enumArray = Stream.of(enums)
                .map(e -> e.getCode())
                .collect(Collectors.toList());

        Class<?> codeClazz = enums[0].getCode().getClass();
        if (!String.class.isAssignableFrom(codeClazz)) {
            return this.buildIntSchema(property, enumArray);
        }

        property.setEnum(enumArray);
        return property;
    }

    private Schema buildIntSchema(Schema property, List enumArray) {
        Schema newSchema = new IntegerSchema();
        SchemaUtil.copySchema(property, newSchema);
        newSchema.setEnum(enumArray);
        return newSchema;
    }

}
