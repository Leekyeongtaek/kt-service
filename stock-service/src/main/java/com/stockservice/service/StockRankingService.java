package com.stockservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StockRankingService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RANKING_KEY_PREFIX = "stock:ranking:daily:";

    @Async
    public void incrementScore(Long stockId) {
        String key = getDailyKey();
        try {
            redisTemplate.opsForZSet().incrementScore(key, String.valueOf(stockId), 1);
            redisTemplate.expire(key, 2, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("레디스 랭킹 점수 증가 실패. stockId: {}", stockId, e);
        }
    }

    public List<Long> getTop10Ids() {
        String key = getDailyKey();
        Set<Object> rangeSet = redisTemplate.opsForZSet().reverseRange(key, 0, 9);

        return Optional.ofNullable(rangeSet)
                .orElseGet(Collections::emptySet)
                .stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .toList(); // java 16
    }

    private String getDailyKey() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return RANKING_KEY_PREFIX + today;
    }
}
