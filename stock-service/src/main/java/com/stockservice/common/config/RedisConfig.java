package com.stockservice.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    //Jedis (전통적인 강자), 동기(Blocking) 방식
    //스레드 안전하지 않음 (풀링 필요), 상대적으로 무거움 (멀티스레드 시)
    //application.yml에 설정한 값을 자동으로 읽어와 연결
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        return new JedisConnectionFactory();
//    }

    //Lettuce (신흥 강자, 스프링 기본값), 비동기(Non-blocking) / 리액티브 방식
    //스레드 안전함 (커넥션 공유 가능), 효율적이고 빠름 (Netty 기반)
    //application.yml에 설정한 값을 자동으로 읽어와 연결
    @Primary
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    //spring-boot-starter-data-redis 라이브러리는 스프링 부트의 자동 설정 대상: 포트, 호스트 정보 입력 안한 이유
    //데이터를 저장하고 꺼내오는 캐싱/랭킹 용도(Lettuce 기반)로 사용
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key와 Value를 직렬화하여 사람이 읽을 수 있는 형태로 Redis에 저장
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 포맷으로 저장

        return redisTemplate;
    }

    @Bean
    public RedisScript<Long> purchaseLimitedOfferScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("scripts/purchase_limited_stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
