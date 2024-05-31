package com.coral.test.spring.natives.core.utils.reflection;


import lombok.NonNull;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huss
 * @version 1.0
 * @className LambdaFieldUtil
 * @description 列工具类
 * @date 2022/1/12 16:14
 */
public class LambdaFieldUtil {


    private static final String METHOD_IS_PREFIX = "is";
    private static final String METHOD_GET_PREFIX = "get";
    private static final String METHOD_SET_PREFIX = "set";
    private static final String LAMBDA_WRITE_REPLACE = "writeReplace";


    private static final Map<String, Field> FIELD_MAP = new ConcurrentHashMap<>(16);


    public static Class<?> findParameterizedType(@NonNull Field field) {
        Class<?> clazz = field.getType();
        if (Collection.class.isAssignableFrom(clazz)) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            return (Class<?>) findArg((ParameterizedType) parameterizedType);
        }
        return clazz;
    }


    /**
     * 根据lambda获取field
     *
     * @param lambda
     * @param <T>
     * @return
     */
    public static <T> Field getField(IGetter<T> lambda) {
        try {
            Method method = lambda.getClass().getDeclaredMethod(LAMBDA_WRITE_REPLACE);
            method.setAccessible(Boolean.TRUE);
            //调用writeReplace()方法，返回一个SerializedLambda对象
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);

            return FIELD_MAP.computeIfAbsent(createCacheKey(serializedLambda), key -> {
                try {
                    String fieldName = findFieldName(serializedLambda.getImplMethodName());
                    Field field = findField(serializedLambda, fieldName);
                    return field;
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据lambda获取field名称
     *
     * @param lambda
     * @param <T>
     * @return
     */
    public static <T> String getFieldName(IGetter<T> lambda) {
        if (Objects.isNull(lambda)) {
            return "";
        }
        Field field = getField(lambda);
        return field.getName();
    }

    /**
     * 根据lambda获取field名称
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static <T> Field findField(Class<?> clazz, String fieldName) {
        return FIELD_MAP.computeIfAbsent(createCacheKey(clazz, fieldName), key -> {
            String name = Introspector.decapitalize(fieldName);
            return FieldUtils.getField(clazz, fieldName, true);
        });
    }


    private static Field findField(SerializedLambda serializedLambda, String fieldName) throws ClassNotFoundException {
        // 第3步 获取的Class是字符串，并且包名是“/”分割，需要替换成“.”，才能获取到对应的Class对象
        String declaredClass = serializedLambda.getImplClass().replace("/", ".");
        Class<?> aClass = Class.forName(declaredClass, false, LambdaFieldUtil.class.getClassLoader());
        return FieldUtils.getField(aClass, fieldName, true);
    }

    private static String findFieldName(String name) {
        if (name.startsWith(METHOD_IS_PREFIX)) {
            name = Introspector.decapitalize(name.substring(2));
        } else {
            if (!name.startsWith(METHOD_GET_PREFIX) && !name.startsWith(METHOD_SET_PREFIX)) {
                throw new RuntimeException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }
            name = Introspector.decapitalize(name.substring(3));
        }
        return name;
    }

    private static String createCacheKey(SerializedLambda serializedLambda) {
        return serializedLambda.getImplClass() + ":" + serializedLambda.getImplMethodName();
    }

    private static String createCacheKey(Class<?> clazz, String fieldName) {
        return clazz.getName() + ":" + fieldName;
    }

    private static Type findArg(ParameterizedType parameterizedType) {
        Type[] types = parameterizedType.getActualTypeArguments();
        Type first = Objects.nonNull(types) ? types[0] : null;
        if (Objects.nonNull(first) && (ParameterizedType.class.isAssignableFrom(first.getClass()))) {
            return findArg((ParameterizedType) first);
        }
        return first;
    }

}
