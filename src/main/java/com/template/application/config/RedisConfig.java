package com.template.application.config;

import cn.hutool.json.xml.ParseConfig;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.Map;

/**
 * ClassName: RedisConfig
 * Description:
 *
 * @Author 白给
 * @Create 2024/7/15 21:04
 * @Version 1.0
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<Object, Object> redisTemplate(final RedisConnectionFactory connectionFactory) {
        return createTemplate(connectionFactory);
    }


    private static RedisTemplate<Object, Object> createTemplate(final RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJson2JsonRedisSerializer<Object> serializer = new FastJson2JsonRedisSerializer<>(Object.class);

        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

//        template.setDefaultSerializer(serializer);
//        template.setStringSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }


    /**
     * fastJson2的序列化器
     *
     * @param <T>
     */
    private static class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
        private final Class<T> clazz;

        static {
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        }

        public FastJson2JsonRedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T value) throws SerializationException {
            if (value == null) {
                return new byte[0];
            }
            Map.Entry<String, T> entity = new AbstractMap.SimpleEntry<>(value.getClass().getName(), value);
            return JSON.toJSONString(entity, JSONWriter.Feature.WriteClassName).getBytes(Charset.defaultCharset());
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null) {
                return null;
            }

            String str = new String(bytes, Charset.defaultCharset());
            int index = str.indexOf(":");
            String cls = str.substring(2, index - 1);
            String obj = str.substring(index + 1, str.length() - 1);
            return JSON.parseObject(
                    obj,
                    clazz,
                    JSONReader.autoTypeFilter(
                            cls
                    ),
                    JSONReader.Feature.SupportClassForName);
        }
    }
}
