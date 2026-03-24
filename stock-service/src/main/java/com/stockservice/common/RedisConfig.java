package com.stockservice.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    //Jedis (전통적인 강자), 동기(Blocking) 방식
    //스레드 안전하지 않음 (풀링 필요), 상대적으로 무거움 (멀티스레드 시)
    //application.yml에 설정한 값을 자동으로 읽어와 연결
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    //Lettuce (신흥 강자, 스프링 기본값), 비동기(Non-blocking) / 리액티브 방식
    //스레드 안전함 (커넥션 공유 가능), 효율적이고 빠름 (Netty 기반)
    //application.yml에 설정한 값을 자동으로 읽어와 연결
    @Primary
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key와 Value를 직렬화하여 사람이 읽을 수 있는 형태로 Redis에 저장
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 포맷으로 저장

        return redisTemplate;
    }
}
