package com.coral.test.spring.simple.enums;

import com.coral.test.spring.simple.core.enums.IEnum;
import lombok.Getter;

/**
 * @author huss
 * @version 1.0
 * @className EventStatus
 * @description 事件状态
 * @date 2022/7/20 18:27
 */
public enum EventStatus implements IEnum<EventStatus, Integer> {

    /**
     * 处理中
     */
    PROCESSING(1, "处理中"),

    /**
     * 处理失败
     */
    PROCESSING_FAILED(2, "处理失败"),

    /**
     * 失败次数太多导致的拒绝
     */
    REJECTED(3, "已拒绝"),

    /**
     * 处理成功
     */
    PROCESSING_SUCCEED(4, "处理成功"),


    ;

    EventStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Getter
    private final Integer code;

    @Getter
    private final String name;


}
