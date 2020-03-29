package cn.stevekung.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/*
Spring 框架提供一种抽象的缓存机制，且 Spring 只提供接口，不提供缓存的具体实现。
所以在程序中使用时，需要有具体缓存的实现。
目前支持的常见的缓存比如 JDK ConcurrentMap-based Cache、Ehcache、Redis、
Caffeine Cache、Guava Cache 等

如果没有找到RedisCacheConfiguration Bean对象
则使用默认配置中的序列化器—JDK自带的序列化器，
如果有就使用自定义的RedisCacheConfiguration，
这就可以解释为什么我们想使用自定义的序列化器需要
声明一个RedisCacheConfiguration Bean对象了。

所谓声明式缓存，即使用 Spring 框架提供的注解来使用缓存功能。
spring-boot-starter-data-redis给我们提供了好几个Redis操作模板
其中RedisTemplate和StringRedisTemplate是最常用的模板。
甚至在SpringBoot中的spring-boot-autoconfigure模块儿下，
都有一个配置类为我们默认注入了RedisTemplate与StringRedisTemplate

默认的RedisTemplate<Object, Object>模板的序列化器会导致乱码，所以我们可以主动
注入一个自定义的RedisTemplate模板。

@ConditionalOnClass(RedisOperations.class)：当classpath下存在RedisOperations类时，
才有资格注入Spring容器； 否者无资格注入Spring容器。
@ConditionalOnMissin：当容器中不存在类的实例时，才有资格注入Spring容器；
 */
// @Configuration：表明这是一个配置类，并尝试注入Spring容器。
@Configuration
public class RedisConfig {

    // 第一种方式 不改动RedisTemplate模板，注解@Cache等可生效
    // 使用缓存注解时会使用StringRedisSerializer对Key进行序列化，
    // 使用GenericJackson2JsonRedisSerializer对Value进行反序列化。
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(){
        return RedisCacheConfiguration.defaultCacheConfig()
                // 键序列化方式
                .serializeKeysWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                // 值序列化方式
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 缓存时间
                .entryTtl(Duration.ofMinutes(1));

    }

    // 第二种方式 注入自定义的RedisTemplate模板
    // 使用示例见SpringbootRedisApplicationTests
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        // 这里的key采用String
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 设置key的 序列化 器
        // 注:RedisTemplate对应的反序列化器与序列化器 一致，设置了序列化器就相当于设置了反序列化器
        StringRedisSerializer keySerializer =  new StringRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        //设置value的 序列化 器
        //注:RedisTemplate对应的反序列化器与序列化器 一致，设置了序列化器就相当于设置了反序列化器
        Jackson2JsonRedisSerializer valueSerializer = new Jackson2JsonRedisSerializer(Object.class);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
