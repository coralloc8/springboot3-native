package com.coral.test.spring.natives.core.utils.id;

import java.util.Objects;
import java.util.UUID;

/**
 * 工厂
 *
 * @author huss
 * @date 2024/4/3 17:07
 * @packageName com.coral.test.spring.natives.core.utils.id
 * @className IdCreatorFactory
 */
public class IdCreatorFactory {

    private static SnowFlakeCreator snowFlakeCreator;

    public IdCreatorFactory(long dataCenterId, long machineId) {
        snowFlakeCreator = new SnowFlakeCreator(dataCenterId, machineId);
    }

    public static String createRandomId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static Long createIncrementId() {
        return Objects.isNull(snowFlakeCreator) ? SnowFlakeCreator.createId() : snowFlakeCreator.nextId();
    }
}
