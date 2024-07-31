package com.coral.test.rule.core.utils;//package com.coral.test.spring.natives.core.utils;
//
//
//import lombok.extern.slf4j.Slf4j;
//import net.sf.cglib.beans.BeanCopier;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @description: 对象复制
// * @author: huss
// * @time: 2020/7/15 17:16
// */
//@Slf4j
//public class BeanCopierUtil {
//
//    private static final Map<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();
//
//    /**
//     * BeanCopier的copy
//     *
//     * @param source 源文件的
//     * @param target 目标文件
//     */
//    public static <T, R> R copy(T source, R target) {
//        final String key = genKey(source, target);
//        BeanCopier beanCopier =
//                BEAN_COPIER_CACHE.computeIfAbsent(key, k -> BeanCopier.create(source.getClass(), target.getClass(), false));
//        beanCopier.copy(source, target, null);
//        return target;
//    }
//
//    public static <T, R> R copy(T source, Class<R> targetClazz) {
//        try {
//            R r = targetClazz.newInstance();
//            final String key = genKey(source, r);
//            BeanCopier beanCopier =
//                    BEAN_COPIER_CACHE.computeIfAbsent(key, k -> BeanCopier.create(source.getClass(), targetClazz, false));
//            beanCopier.copy(source, r, null);
//            return r;
//        } catch (InstantiationException | IllegalAccessException e) {
//            log.error("Error", e);
//        }
//
//        return null;
//    }
//
//    private static String genKey(Object source, Object target) {
//        return source.getClass() + "_" + target.getClass();
//    }
//
//}
