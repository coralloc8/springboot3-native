package com.coral.test.spring.simple.core.utils.reflection;

import java.io.Serializable;

/**
 * @author huss
 * @version 1.0
 * @className IGetter
 * @description 实体类的 getter
 * @date 2022/1/12 16:50
 */
@FunctionalInterface
public interface IGetter<T> extends Serializable {
    /**
     * apply
     *
     * @param source
     * @return
     */
    Object apply(T source);
}