package com.coral.test.rule.core.r2dbc;

/**
 * 值映射
 *
 * @author huss
 * @date 2024/4/19 9:31
 * @packageName com.coral.test.spring.natives.core.r2dbc
 * @className IMapping
 */
public interface IMapping<T> {
    T getValue();
}
