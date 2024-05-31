package com.coral.test.spring.simple.core.feature;

import cn.hutool.core.util.ClassUtil;
import com.coral.test.spring.simple.core.utils.reflection.IGetter;
import com.coral.test.spring.simple.repository.IRepository;
import com.coral.test.spring.simple.service.IService;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

import java.lang.invoke.SerializedLambda;
import java.util.HashSet;
import java.util.Set;

/**
 * 运行时注册
 *
 * @author huss
 * @date 2024/4/12 10:34
 * @packageName com.coral.test.spring.simple.core.feature
 * @className RuntimeRegistrationFeature
 */
public class RuntimeRegistrationFeature implements Feature {

    private static final String PACKAGE_NAME = "com.coral.test.spring.simple";

    @Override
    public void duringSetup(DuringSetupAccess access) {
        //		扫描指定包下IService IRepository的字类（实现类），然后全部注册到graalvm Lambda 序列化中
        RuntimeRegistrationFeature.findClasses(PACKAGE_NAME, Set.of(IService.class, IRepository.class))
                .forEach(RuntimeSerialization::registerLambdaCapturingClass);
        RuntimeSerialization.register(SerializedLambda.class, IGetter.class);
    }

    /**
     * 找到某个包下面指定的父类的所有子类
     *
     * @param packageName  包名
     * @param superClasses 父类
     * @return 子类集合
     */
    private static Set<Class<?>> findClasses(String packageName, Set<Class<?>> superClasses) {
        Set<Class<?>> classes = new HashSet<>();
//        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
//        TypeFilter filter = new AssignableTypeFilter(superClass);
//
//        scanner.addIncludeFilter(filter);
//
//        String basePackage = ClassUtils.convertClassNameToResourcePath(packageName);
//        for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
//            try {
//                Class<?> clazz = Class.forName(candidate.getBeanClassName());
//                classes.add(clazz);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//                // 处理异常
//            }
//        }
        for (Class<?> superClass : superClasses) {
            classes.addAll(ClassUtil.scanPackageBySuper(packageName, superClass));
        }
        return classes;
    }

}
