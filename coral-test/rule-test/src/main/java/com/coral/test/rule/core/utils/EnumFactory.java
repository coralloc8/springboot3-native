package com.coral.test.rule.core.utils;

import com.coral.test.rule.core.enums.IEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huss
 * @version 1.0
 * @className EnumFactory
 * @description 枚举
 * @date 2021/3/22 14:49
 */
@Slf4j
public final class EnumFactory {

    private final static Map<Class<?>, Class<?>> ENUM_CLASS_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取enum描述
     *
     * @param clazz
     * @return
     */
    public static String description(Class<? extends IEnum> clazz) {
        List<String> remarks = Stream.of(clazz.getEnumConstants()).map(e -> {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotBlank(e.getName())) {
                sb.append(e.getName()).append(":");
            }
            if (Objects.nonNull(e.getCode())) {
                sb.append(e.getCode());
            }

            return sb.toString();
        }).collect(Collectors.toList());
        return String.join("    ", remarks);
    }

    /**
     * 获取枚举信息
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends IEnum<?, ?>> List<Map<String, Object>> infos(@NonNull Class<T> clazz) {
        boolean isEnum = clazz.isEnum() && IEnum.class.isAssignableFrom(clazz);
        if (!isEnum) {
            return Collections.emptyList();
        }
        return Arrays.stream(clazz.getEnumConstants()).map(e -> e.getParams()).collect(Collectors.toList());
    }

    /**
     * 根据 code/name/className 查询枚举值
     *
     * @param val
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends IEnum<?, ?>> Optional<T> findBy(String val, @NonNull Class<T> clazz) {
        return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.isEqual(val)).findFirst();
    }

    public static Class<?> getEnumType(Class<?> targetType) {
        return ENUM_CLASS_CACHE.computeIfAbsent(targetType, t -> {
            Class<?> enumType = t;
            while (enumType != null && !enumType.isEnum()) {
                enumType = enumType.getSuperclass();
            }
            log.debug("### getEnumType: {}", enumType);
            return enumType;
        });
    }
}
