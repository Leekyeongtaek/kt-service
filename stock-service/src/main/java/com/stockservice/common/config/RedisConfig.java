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

    // Jedis: Lettuce 이전에 쓰이던 클래스
    // Thread-sage 하지 않아서 별도의 쓰레드 풀을 만들어야 하고 느리고 무겁다.
//    @Bean
//    public JedisConnectionFactory jedisConnectionFactory() {
//        return new JedisConnectionFactory();
//    }

    // RedisConnectionFactory: 스프링과 레디스 서버 사이에 통신 다리
    // 스프링 부트 2.0 이후 기본 설정 클래스
    // 비동기 처리(Non-blocking), 성능 최적화
    // 스레드 안전함 (커넥션 공유 가능), 효율적이고 빠름 (Netty 기반)
    // application.yml에 설정한 값을 자동으로 읽어와 연결
    @Primary
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    // Redisson: 단순 캐싱이 아닌 분산 락 기능이 필요한 경우 사용

    // spring-boot-starter-data-redis 라이브러리는 스프링 부트의 자동 설정 대상: 포트, 호스트 정보 입력 안한 이유
    // 레디스 서버에 CRUD 기능을 모두 제공하는 만능 도구
    // Key, Value 모두 String만 사용한다면 StringRedisTemplate 사용 가능
    //todo 추후 직렬화 방식 변경 테스트 필요.
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

    // 선착순 구매 레디스 루아 스크립트를 빈으로 등록해 재사용
    @Bean
    public RedisScript<Long> purchaseLimitedOfferScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("scripts/purchase_limited_stock.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
