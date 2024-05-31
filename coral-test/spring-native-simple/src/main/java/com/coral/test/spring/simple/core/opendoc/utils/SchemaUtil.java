package com.coral.test.spring.simple.core.opendoc.utils;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author huss
 * @version 1.0
 * @className SchemaUtil
 * @description SchemaUtil
 * @date 2021/9/14 19:30
 */
public class SchemaUtil {

    public static Schema copySchema(Schema oldSchema, Schema newSchema) {
        String type = newSchema.getType();
        String format = newSchema.getFormat();
        newSchema = SchemaConvert.INSTANCE.convert(oldSchema);

        newSchema.type(type)
                .format(format)
        ;

        return newSchema;
    }

    public static Schema getSchema(Class className, String schemaName, String description) {
        ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(
                        new AnnotatedType(className).resolveAsRef(false));
        return resolvedSchema.schema.description(description).name(schemaName);
    }


    @Mapper
    interface SchemaConvert {
        SchemaConvert INSTANCE = Mappers.getMapper(SchemaConvert.class);

        Schema convert(Schema schema);
    }

}
