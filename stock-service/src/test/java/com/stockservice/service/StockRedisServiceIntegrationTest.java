package com.stockservice.service;

import com.stockservice.enums.LimitedOfferResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StockRedisServiceIntegrationTest {

    @Autowired
    private StockRedisService stockRedisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Long testStockId;
    private String stockKey;
    private String userKey;

    @BeforeEach
    void setUp() {
        testStockId = System.nanoTime();
        stockKey = "stock:limited-offer:" + testStockId;
        userKey = "stock:limited-offer:users:" + testStockId;

        redisTemplate.delete(List.of(stockKey, userKey));
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete(List.of(stockKey, userKey));
    }

    @Test
    @DisplayName("한정 상품 수량이 100개 존재하고, 1,000명이 동시에 구매 요청 시 정확하게 100 명만 매수에 성공한다.")
    void tryPurchaseLimitedOffer_Concurrency_Success() throws InterruptedException {
        //given
        int quantity = 100;
        int requestCount = 1000;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger duplicateCount = new AtomicInteger(0);

        //999 번 종목으로 100개 수량 설정
        stockRedisService.setUpLimitedOffer(testStockId, quantity);

        // 32개의 스레드로 1000개 요청 수행
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(requestCount);

        //when
        for (int i = 0; i < requestCount; i++) {
            long userId = i;

            executorService.submit(() -> {
                try {
                    LimitedOfferResult result = stockRedisService.tryPurchaseLimitedOffer(testStockId, userId);

                    switch (result) {
                        case SUCCESS -> successCount.incrementAndGet();
                        case SOLD_OUT -> failCount.incrementAndGet();
                        case DUPLICATE -> duplicateCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("에러: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        //then
        assertThat(successCount.get()).isEqualTo(quantity); //성공한 유저수는 설정한 재고 수와 같아야 한다
        assertThat(failCount.get()).isEqualTo(requestCount - quantity); //실패한 유저수는 총 요청 횟수 - 재고 수

        Object remainStock = redisTemplate.opsForValue().get(stockKey);
        assertThat(String.valueOf(remainStock)).isEqualTo("0"); //한정 상품 잔여 수량은 0개
    }

    @Test
    @DisplayName("동일한 유저가 동시에 여러 번 매수 신청을 해도, 한 번만 성공해야 한다.")
    void tryPurchaseLimitedOffer_Duplicate_Prevent() throws InterruptedException {
        //given
        Long userId = 77L;
        int duplicateRequestCount = 10;
        int quantity = 100;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger duplicateCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(duplicateRequestCount);
        CountDownLatch latch = new CountDownLatch(duplicateRequestCount);

        stockRedisService.setUpLimitedOffer(testStockId, quantity);

        //when
        for (int i = 0; i < duplicateRequestCount; i++) {
            executorService.submit(() -> {
                try {
                    LimitedOfferResult result = stockRedisService.tryPurchaseLimitedOffer(testStockId, userId);
                    switch (result) {
                        case SUCCESS -> successCount.incrementAndGet();
                        case DUPLICATE -> duplicateCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("예외: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        //then
        assertThat(successCount.get()).isEqualTo(1); // 1번은 반드시 성공 카운트
        assertThat(duplicateCount.get()).isEqualTo(duplicateRequestCount - 1); // 9번은 중복 요청 카운트
    }
}
