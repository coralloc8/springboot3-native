package com.coral.test.rule.core.enums;

import lombok.Getter;

/**
 * @author huss
 * @version 1.0
 * @className GlobalOrder
 * @description 通用排序
 * @date 2021/9/14 16:12
 */
public enum GlobalOrder implements IEnum<GlobalOrder, String> {
    /**
     * asc
     */
    ASC("asc", "升序"),

    /**
     * desc
     */
    DESC("desc", "倒序"),

    ;


    GlobalOrder(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Getter
    private String code;

    @Getter
    private String name;
}
