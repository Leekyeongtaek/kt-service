package com.stockservice.service;

import com.stockservice.domain.Stock;
import com.stockservice.dto.StockDetailResponse;
import com.stockservice.dto.StockRankingResponse;
import com.stockservice.dto.message.LimitedOfferPurchaseEvent;
import com.stockservice.dto.request.StockLimitedOfferPurchaseRequest;
import com.stockservice.enums.LimitedOfferResult;
import com.stockservice.fixture.StockFixture;
import com.stockservice.messaging.StockProducer;
import com.stockservice.repository.StockLimitedOfferPurchaseRepository;
import com.stockservice.repository.StockRepository;
import com.stockservice.repository.query.StockQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;
    @Mock
    private StockRedisService stockRedisService;
    @Mock
    private StockProducer stockProducer;
    @Mock
    private StockQueryRepository stockQueryRepository;
    @Mock
    private StockLimitedOfferPurchaseRepository stockLimitedOfferPurchaseRepository;

    @Test
    @DisplayName("유효한 종목 ID로 상세 조회 시, 종목 정보를 정상적으로 반환하고 조회수를 1 증가시킨다.")
    void getStockDetail_Success() {
        //given
        Long stockId = 1L;
        Stock standardStock = StockFixture.createStandardStock();

        given(stockRepository.findById(stockId)).willReturn(Optional.of(standardStock));

        //when
        StockDetailResponse stockDetail = stockService.getStockDetail(stockId);

        //then
        assertThat(stockDetail).usingRecursiveComparison()
                .ignoringFields("marketTypeName", "departmentName", "stockTypeName")
                .isEqualTo(standardStock);

        assertThat(stockDetail.getMarketTypeName()).isEqualTo("코스피");
        assertThat(stockDetail.getStockTypeName()).isEqualTo("보통주");
        assertThat(stockDetail.getDepartmentName()).isNull();

        verify(stockRedisService, times(1)).incrementScore(stockId);
    }

    @Test
    @DisplayName("존재하지 않는 종목 ID로 조회 시, 예외가 발생한다.")
    void getStockDetail_Fail_NotFound() {
        //given
        Long invalidStockId = 999L;

        given(stockRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.getStockDetail(invalidStockId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Top10 종목 조회 시, 레디스에서 조회한 ID 순서대로 DTO 리스트가 정렬되어 반환된다")
    void getTop10Rankings_Order_Guaranteed() {
        //given
        List<Long> top10Ids = List.of(3L, 1L, 2L);
        given(stockRedisService.getTop10Ids()).willReturn(top10Ids);

        Stock stock1 = StockFixture.createStockWithId(1L, "종목1");
        Stock stock2 = StockFixture.createStockWithId(2L, "종목2");
        Stock stock3 = StockFixture.createStockWithId(3L, "종목3");

        given(stockRepository.findAllById(top10Ids)).willReturn(List.of(stock1, stock2, stock3));

        //when
        List<StockRankingResponse> result = stockService.getTop10Rankings();

        //then
        assertThat(result)
                .hasSize(3)
                .extracting("stockId", "rank")
                .containsExactly(
                        tuple(3L, 1),
                        tuple(1L, 2),
                        tuple(2L, 3)
                );
    }

    @Test
    @DisplayName("Top10 종목 조회 시, 레디스에 종목 랭킹 데이터가 없으면 빈 리스트를 반환한다")
    void getTop10Rankings_Empty() {
        //given
        given(stockRedisService.getTop10Ids()).willReturn(Collections.emptyList());

        //when
        List<StockRankingResponse> result = stockService.getTop10Rankings();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("주식 한정 상품 매수에 성공하면, 카프카 메시지를 정상 발행한다.")
    void applyLimitedOffer_Success() {
        //given
        Long stockId = 1L;
        Long stockLimitedOfferId = 2L;
        Long userId = 10L;
        StockLimitedOfferPurchaseRequest request = new StockLimitedOfferPurchaseRequest(stockLimitedOfferId, userId);

        given(stockRedisService.tryPurchaseLimitedOffer(stockId, userId))
                .willReturn(LimitedOfferResult.SUCCESS);

        //when
        LimitedOfferResult result = stockService.applyLimitedOffer(stockId, request);

        //then
        assertThat(result).isEqualTo(LimitedOfferResult.SUCCESS);

        verify(stockProducer).send(any(LimitedOfferPurchaseEvent.class));
    }

    @Test
    @DisplayName("주식 한정 상품 매수에 실패하면, 카프카 메시지를 발행하지 않는다.")
    void applyLimitedOffer_Fail_DoesNotPublishEvent() {
        //given
        Long stockId = 1L;
        Long stockLimitedOfferId = 2L;
        Long userId = 10L;
        StockLimitedOfferPurchaseRequest request = new StockLimitedOfferPurchaseRequest(stockLimitedOfferId, userId);

        given(stockRedisService.tryPurchaseLimitedOffer(stockId, userId))
                .willReturn(LimitedOfferResult.SOLD_OUT);

        //when
        LimitedOfferResult result = stockService.applyLimitedOffer(stockId, request);

        //then
        assertThat(result).isEqualTo(LimitedOfferResult.SOLD_OUT);

        verify(stockProducer, never()).send(any(LimitedOfferPurchaseEvent.class));
    }
}