package com.coral.test.rule.core.json;


import com.coral.test.rule.core.exception.Exceptions;
import com.coral.test.rule.core.json.module.*;
import com.coral.test.rule.core.utils.DatePattern;
import com.coral.test.rule.core.utils.StringPool;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Jackson工具类
 *
 * @author huss
 */
@Slf4j
public class JsonUtil {

    /**
     * 将对象序列化成json字符串
     *
     * @param value javaBean
     * @param <T>   T 泛型标记
     * @return jsonString json字符串
     */
    public static <T> String toJson(T value) {
        try {
            return getInstance().writeValueAsString(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将对象序列化成 json byte 数组
     *
     * @param object javaBean
     * @return jsonString json字符串
     */
    public static byte[] toJsonAsBytes(Object object) {
        try {
            return getInstance().writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     *
     * @param content   content
     * @param valueType class
     * @param <T>       T 泛型标记
     * @return Bean
     */
    public static <T> T parse(String content, Class<T> valueType) {
        try {
            return getInstance().readValue(content, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     *
     * @param content       content
     * @param typeReference 泛型类型
     * @param <T>           T 泛型标记
     * @return Bean
     */
    public static <T> T parse(String content, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(content, typeReference);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json byte 数组反序列化成对象
     *
     * @param bytes     json bytes
     * @param valueType class
     * @param <T>       T 泛型标记
     * @return Bean
     */
    public static <T> T parse(byte[] bytes, Class<T> valueType) {
        try {
            return getInstance().readValue(bytes, valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     *
     * @param bytes         bytes
     * @param typeReference 泛型类型
     * @param <T>           T 泛型标记
     * @return Bean
     */
    public static <T> T parse(byte[] bytes, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(bytes, typeReference);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     *
     * @param in        InputStream
     * @param valueType class
     * @param <T>       T 泛型标记
     * @return Bean
     */
    public static <T> T parse(InputStream in, Class<T> valueType) {
        try {
            return getInstance().readValue(in, valueType);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json反序列化成对象
     *
     * @param in            InputStream
     * @param typeReference 泛型类型
     * @param <T>           T 泛型标记
     * @return Bean
     */
    public static <T> T parse(InputStream in, TypeReference<T> typeReference) {
        try {
            return getInstance().readValue(in, typeReference);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json反序列化成List对象
     *
     * @param content      content
     * @param valueTypeRef class
     * @param <T>          T 泛型标记
     * @return List
     */
    public static <T> List<T> parseArray(String content, Class<T> valueTypeRef) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        try {
            if (!StringUtils.startsWithIgnoreCase(content, StringPool.LEFT_SQ_BRACKET)) {
                content = StringPool.LEFT_SQ_BRACKET + content + StringPool.RIGHT_SQ_BRACKET;
            }
            List<T> result;
            if (isBaseWrapType(valueTypeRef)) {
                result = getInstance().readValue(content, new TypeReference<List<T>>() {
                });

            } else {
                List<Map<String, Object>> list = getInstance().readValue(content,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                result = list.stream().map(e -> toPojo(e, valueTypeRef)).collect(Collectors.toList());
            }
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw Exceptions.unchecked(e);
        }
    }


    public static Map<String, Object> toMap(String content) {
        try {
            return getInstance().readValue(content, Map.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw Exceptions.unchecked(e);
        }
    }

    public static <T> Map<String, T> toMap(String content, Class<T> valueTypeRef) {
        try {
            Map<String, Map<String, Object>> map =
                    getInstance().readValue(content, new TypeReference<Map<String, Map<String, Object>>>() {
                    });
            Map<String, T> result = new HashMap<>(16);
            for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
                result.put(entry.getKey(), toPojo(entry.getValue(), valueTypeRef));
            }
            return result;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw Exceptions.unchecked(e);
        }
    }

    public static <T> T toPojo(Map fromValue, Class<T> toValueType) {
        return getInstance().convertValue(fromValue, toValueType);
    }

    /**
     * 将json字符串转成 JsonNode
     *
     * @param jsonString jsonString
     * @return jsonString json字符串
     */
    public static JsonNode readTree(String jsonString) {
        try {
            return getInstance().readTree(jsonString);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json字符串转成 JsonNode
     *
     * @param in InputStream
     * @return jsonString json字符串
     */
    public static JsonNode readTree(InputStream in) {
        try {
            return getInstance().readTree(in);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json字符串转成 JsonNode
     *
     * @param content content
     * @return jsonString json字符串
     */
    public static JsonNode readTree(byte[] content) {
        try {
            return getInstance().readTree(content);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 将json字符串转成 JsonNode
     *
     * @param jsonParser JsonParser
     * @return jsonString json字符串
     */
    public static JsonNode readTree(JsonParser jsonParser) {
        try {
            return getInstance().readTree(jsonParser);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 是否是基本数据类型 即 int,double,long等类似格式
     *
     * @param clazz
     * @return
     */
    public static boolean isBaseType(Class clazz) {
        return Objects.nonNull(clazz) && clazz.isPrimitive();
    }

    /**
     * 是否是基本数据类型的包装类型
     *
     * @param clz
     * @return
     */
    public static boolean isBaseWrapType(Class clz) {
        try {
            if (Objects.isNull(clz) || isBaseType(clz)) {
                return false;
            }
            if (String.class.isAssignableFrom(clz)) {
                return true;
            }
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static ObjectMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    private static class JacksonHolder {
        private static final Locale CHINA = Locale.CHINA;
        private static ObjectMapper INSTANCE;

        static {
            INSTANCE = new ObjectMapper();
            // 设置地点为中国
            INSTANCE.setLocale(CHINA);
            // 去掉默认的时间戳格式
            INSTANCE.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // 设置为中国上海时区
            INSTANCE.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            // 序列化时，日期的统一格式
            INSTANCE.setDateFormat(new SimpleDateFormat(DatePattern.PATTERN_DATETIME, Locale.CHINA));

            // 失败处理
            INSTANCE.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            INSTANCE.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // 单引号处理
            INSTANCE.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

            // 禁用科学计数法
            INSTANCE.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
            INSTANCE.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

            // 日期格式化
            INSTANCE.registerModules(new ParameterNamesModule(), new BladeJavaTimeModule(), new EnumModule(),
                    new String2EscapeHtml4Module());
            // 字段属性自定义转换处理
            INSTANCE.setSerializerFactory(INSTANCE.getSerializerFactory().withSerializerModifier(new BladeBeanSerializerModifier()));
            // 属性可视化范围
            // super.visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        }
    }

}
