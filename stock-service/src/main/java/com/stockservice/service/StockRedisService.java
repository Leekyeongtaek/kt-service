package com.stockservice.service;

import com.stockservice.enums.LimitedOfferResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> purchaseLimitedStockScript;

    private static final String STOCK_LIMITED_OFFER_KEY_PREFIX = "stock:limited-offer:";// 이벤트 잔여 수량 키 (예: event:stock:{stockId})
    private static final String STOCK_LIMITED_OFFER_USERS_KEY_PREFIX = "stock:limited-offer:users:";// 이벤트 참여 유저 명부 키 (예: event:users:{stockId})
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

    public void setUpLimitedOffer(Long stockId, int quantity) {
        String key = STOCK_LIMITED_OFFER_KEY_PREFIX + stockId;
        redisTemplate.opsForValue().set(key, String.valueOf(quantity));
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    public LimitedOfferResult tryPurchaseLimitedOffer(Long stockId, Long userId) {
        String limitedOfferKey = STOCK_LIMITED_OFFER_KEY_PREFIX + stockId;
        String limitedOfferUserKey = STOCK_LIMITED_OFFER_USERS_KEY_PREFIX + stockId;

        Long resultCode = redisTemplate.execute(
                purchaseLimitedStockScript,
                List.of(limitedOfferKey, limitedOfferUserKey),
                String.valueOf(userId),
                "1"
        );

        return LimitedOfferResult.fromCode(resultCode);
    }
}
