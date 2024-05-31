package com.coral.test.spring.natives.core.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认枚举接口
 *
 * @author huss
 * @date 2024/3/29 16:36
 * @packageName com.coral.test.spring.natives.enums
 * @className IEnum
 */
public interface IEnum<K extends Enum<?>, T> {
    String CODE_KEY = "code";
    String NAME_KEY = "name";
    String DESCRIPTION_KEY = "description";

    T getCode();

    String getName();

    default Map<String, Object> getParams() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", this.getCode());
        map.put("name", this.getName());
        return map;
    }

    default boolean isEqual(String val) {
        return StringUtils.isNotBlank(val) && (val.equals(getCode().toString()) || val.equals(getName()) || val.equals(this.toString())
        );
    }
}