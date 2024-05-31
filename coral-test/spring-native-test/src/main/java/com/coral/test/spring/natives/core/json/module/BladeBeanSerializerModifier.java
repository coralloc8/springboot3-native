/**
 * Copyright (c) 2018-2028, DreamLu 卢春梦 (qq596392912@gmail.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0; you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.coral.test.spring.natives.core.json.module;

import com.coral.test.spring.natives.core.json.JsonIgnoreNull;
import com.coral.test.spring.natives.core.json.JsonNullSerializer;
import com.coral.test.spring.natives.core.utils.AnnotationUtil;
import com.coral.test.spring.natives.core.utils.StringPool;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @formatter:off
 * jackson 默认值为 null 时的处理
 *
 * 主要是为了避免 app 端出现null导致闪退
 *
 * 规则：
 * number -1
 * string ""
 * date ""
 * boolean false
 * array []
 * Object {}
 * @formatter:on
 */
@Slf4j
public class BladeBeanSerializerModifier extends BeanSerializerModifier {

    private static final int INDEX_NOT_FOUND = -1;

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        log.debug(">>>>>BladeBeanSerializerModifier start...");
        //还原
//        config.getDefaultPropertyInclusion().withValueInclusion(JsonInclude.Include.USE_DEFAULTS);

        Optional<JsonNullSerializer> nullSerializerOpt = AnnotationUtil.findAnnotation(beanDesc.getBeanClass(), JsonNullSerializer.class);
        Optional<JsonIgnoreNull> ignoreNullOpt = AnnotationUtil.findAnnotation(beanDesc.getBeanClass(), JsonIgnoreNull.class);
        // 循环所有的beanPropertyWriter
        beanProperties.forEach(writer -> {
            // 如果已经有 null 序列化处理如注解：@JsonSerialize(nullsUsing = xxx) 跳过
            if (writer.hasNullSerializer()) {
                return;
            }
            // 兼容之前的接口返参json序列化,以前的接口对null值直接返回给前端,后续新的接口可以配置null值处理或者忽略null值

            //空值需要序列化的
            boolean canSerializer = nullSerializerOpt.isEmpty() || nullSerializerOpt.get().value();
            if (canSerializer) {
                JavaType type = writer.getType();
                Class<?> clazz = type.getRawClass();
                if (type.isTypeOrSubTypeOf(Number.class)) {
                    writer.assignNullSerializer(NullJsonSerializers.NUMBER_JSON_SERIALIZER);
                } else if (type.isTypeOrSubTypeOf(Boolean.class)) {
                    writer.assignNullSerializer(NullJsonSerializers.BOOLEAN_JSON_SERIALIZER);
                } else if (type.isTypeOrSubTypeOf(Character.class)) {
                    writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
                } else if (type.isTypeOrSubTypeOf(String.class)) {
                    writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
                } else if (type.isArrayType() || clazz.isArray() || type.isTypeOrSubTypeOf(Collection.class)) {
                    writer.assignNullSerializer(NullJsonSerializers.ARRAY_JSON_SERIALIZER);
                } else if (type.isTypeOrSubTypeOf(OffsetDateTime.class)) {
                    writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
                } else if (type.isTypeOrSubTypeOf(Date.class) || type.isTypeOrSubTypeOf(TemporalAccessor.class)) {
                    writer.assignNullSerializer(NullJsonSerializers.STRING_JSON_SERIALIZER);
                } else {
                    writer.assignNullSerializer(NullJsonSerializers.OBJECT_JSON_SERIALIZER);
                }
            }
        });

        //空值处理
        if (ignoreNullOpt.isPresent()) {
//            config.getDefaultPropertyInclusion().withValueInclusion(JsonInclude.Include.NON_NULL);
//            newBeanProperties = newBeanProperties.stream().filter(writer -> {
//                try {
//                    log.info(">>>>>{},{}", writer, beanDesc);
//                    return !Objects.isNull(writer.get(beanDesc.getObjectIdInfo()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return true;
//                }
//            }).collect(Collectors.toList());
        }
        return super.changeProperties(config, beanDesc, beanProperties);
    }

    private interface NullJsonSerializers {

        JsonSerializer<Object> STRING_JSON_SERIALIZER = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(StringPool.EMPTY);
            }
        };

        JsonSerializer<Object> NUMBER_JSON_SERIALIZER = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeNumber(INDEX_NOT_FOUND);
            }
        };

        JsonSerializer<Object> BOOLEAN_JSON_SERIALIZER = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeObject(Boolean.FALSE);
            }
        };

        JsonSerializer<Object> ARRAY_JSON_SERIALIZER = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartArray();
                gen.writeEndArray();
            }
        };

        JsonSerializer<Object> OBJECT_JSON_SERIALIZER = new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartObject();
                gen.writeEndObject();
            }
        };

    }

}
