package com.coral.test.rule.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * redis config
 *
 * @author huss
 * @date 2024/6/3 15:01
 * @packageName com.coral.test.spring.natives.config
 * @className RedisConfig
 */
@Import(CacheProperties.class)
@Configuration(proxyBeanMethods = false)
@EnableCaching
@Slf4j
public class RedisConfig implements CachingConfigurer {

    @Resource
    private ObjectMapper objectMapper;


    /**
     * @see RedisReactiveAutoConfiguration
     * reactiveRedisTemplate
     *
     * @param reactiveRedisConnectionFactory 连接工厂
     * @return ReactiveRedisTemplate
     */
    @Primary
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
                .newSerializationContext(RedisSerializer.string());
        RedisSerializationContext<String, Object> serializationContext = builder
                .value(valueSerializer())
                .hashValue(valueSerializer())
                .build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
    }

    /**
     * @formatter:off
     * 	methodName		root对象		当前被调用的方法名																        root.methodName
     *  method			root对象		当前被调用的方法																		root.method.name
     *  target			root对象		当前被调用的目标对象																	root.target
     *  targetClass		root对象		当前被调用的目标对象类																	root.targetClass
     *  args			root对象		当前被调用的方法的参数列表																root.args[0]
     *  caches			root对象		当前方法调用使用的缓存列表（如@Cacheable(value={“cache1”, “cache2”})），则有两个cache	    root.caches[0].name
     *  argument Name	执行上下文	当前被调用的方法的参数，如findById(Long id)，我们可以通过#id拿到参数user.id
     *  result			执行上下文	方法执行后的返回值（仅当方法执行之后的判断有效，如‘unless'，'com.coral.base.common.cache evict'的beforeInvocation=false）result
     *
     *	@CacheEvict(value = "user", key = "#user.id",
     *		condition = "#root.target.canCache() and #root.caches[0].get(#user.id).get().username ne #user.username", beforeInvocation = true)
     *  public void conditionUpdate(User user)
     *
     * @formatter:on
     */

    /**
     * 在没有指定缓存Key的情况下，key生成策略
     *
     * @return
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName()).append(".");
            sb.append(method.getName()).append(".");
            for (Object obj : params) {
                sb.append(obj.toString()).append(",");
            }
            String key = sb.substring(0, sb.length() - 1);
            log.info("Cache key: [{}]", key);
            return key;
        };
    }

    /**
     * @formatter:off
     * @@@@@@@@@@@@@@@@@@@@@
     * @Cacheable ，作用在方法上，触发缓存读取操作
     * @CacheEvict ，作用在方法上，触发缓存失效操作
     * @CachePut ，作用在方法上，触发缓存更新操作。
     * @Cache ，作用在方法上，综合上面的各种操作，在有些场景下 ，调用业务会触发多种缓存操作。
     * @CacheConfig ，在类上设置当前缓存 一些公共设置。
     * @@@@@@@@@@@@@@@@@@@@@
     *
     * @param redisConnectionFactory
     * @param cacheProperties
     * @return CacheManager
     * @formatter:on
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, CacheProperties cacheProperties) {
        // 关键点，spring cache的注解使用的序列化都从这来，没有这个配置的话使用的jdk自己的序列化，实际上不影响使用，只是打印出来不适合人眼识别
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(config)
                .transactionAware();

        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        if (cacheProperties.getRedis().isEnableStatistics()) {
            builder.enableStatistics();
        }
        return builder.build();
    }

    private RedisSerializer<Object> valueSerializer() {
        // 设置序列化Jackson2JsonRedisSerializer
        // GenericJackson2JsonRedisSerializer 序列化时会带上当前类的class
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }


}
