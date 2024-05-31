package com.coral.test.spring.simple.core.opendoc.config;

import com.coral.test.spring.simple.core.enums.IEnum;
import com.coral.test.spring.simple.core.opendoc.utils.SchemaUtil;
import com.coral.test.spring.simple.core.utils.EnumFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huss
 * @version 1.0
 * @className MyParameterCustomizer
 * @description 参数自定义
 * @date 2021/9/10 15:01
 */
@Slf4j
@Component
public class MyParameterCustomizer implements ParameterCustomizer {
    @Override
    public Parameter customize(Parameter parameterModel, MethodParameter methodParameter) {
        //所有参数的定义优先使用schema中定义的数据
        Schema schema = null;
        if (methodParameter.hasParameterAnnotation(io.swagger.v3.oas.annotations.Parameter.class)) {
            return parameterModel;
        }
        if (methodParameter.hasParameterAnnotation(Schema.class)) {
            schema = methodParameter.getParameterAnnotation(Schema.class);
        }
        if (Objects.isNull(schema)) {
            return parameterModel;
        }

        this.buildSchemaIfEnum(methodParameter, parameterModel, schema);

        parameterModel.setRequired(schema.required());
        parameterModel.setDescription(parameterModel.getSchema().getDescription());
        parameterModel.setDeprecated(schema.deprecated());
        parameterModel.setExample(schema.example());


        log.info(">>>>>重新设置parameterModel成功. name:{},required:{},description:{}",
                parameterModel.getName(), schema.required(), schema.description());
        return parameterModel;
    }

    private void buildSchemaIfEnum(MethodParameter methodParameter, Parameter parameterModel, Schema schema) {
        if (!IEnum.class.isAssignableFrom(methodParameter.getParameterType())) {
            return;
        }

        String desc = schema.description() + "   " + EnumFactory.description((Class<? extends IEnum>) methodParameter.getParameterType());

        IEnum[] enums = (IEnum[]) methodParameter.getParameterType().getEnumConstants();

        Class<?> clazz = enums[0].getCode().getClass();

        io.swagger.v3.oas.models.media.Schema oldSchema = parameterModel.getSchema();

        oldSchema.setDescription(desc);
        List codes = Stream.of(enums).map(e -> e.getCode()).collect(Collectors.toList());

        if (String.class.isAssignableFrom(clazz)) {
            oldSchema.setEnum(codes);
            return;
        }

        io.swagger.v3.oas.models.media.Schema newSchema = new IntegerSchema();
        SchemaUtil.copySchema(oldSchema, newSchema);

        newSchema.setEnum(codes);
        //重写enum的值
        parameterModel.setSchema(newSchema);
    }

}
