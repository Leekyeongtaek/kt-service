package com.stockservice.service;

import com.stockservice.domain.Stock;
import com.stockservice.dto.StockDetailResponse;
import com.stockservice.dto.StockRankingResponse;
import com.stockservice.fixture.StockFixture;
import com.stockservice.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    // 가짜 객체 생성
    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockRankingService stockRankingService;

    // 가짜 객체들을 주입받을 진짜 테스트 대상 생성
    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("유효한 종목 ID로 상세 조회 시, 종목 정보를 정상적으로 반환한다.")
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
        given(stockRankingService.getTop10Ids()).willReturn(top10Ids);

        Stock stock1 = StockFixture.createStockWithId(1L, "종목1");
        Stock stock2 = StockFixture.createStockWithId(2L, "종목2");
        Stock stock3 = StockFixture.createStockWithId(3L, "종목3");

        given(stockRepository.findAllById(top10Ids)).willReturn(List.of(stock1, stock2, stock3));

        //when
        List<StockRankingResponse> result = stockService.getTop10Rankings();

        //then
        assertThat(result.get(0).getStockId()).isEqualTo(3L);
        assertThat(result.get(0).getRank()).isEqualTo(1);

        assertThat(result.get(1).getStockId()).isEqualTo(1L);
        assertThat(result.get(1).getRank()).isEqualTo(2);

        assertThat(result.get(2).getStockId()).isEqualTo(2L);
        assertThat(result.get(2).getRank()).isEqualTo(3);
    }

    @Test
    @DisplayName("Top10 종목 조회 시, 레디스에 종목 랭킹 데이터가 없으면 빈 리스트를 반환한다")
    void getTop10Rankings_Empty() {
        //given
        given(stockRankingService.getTop10Ids()).willReturn(Collections.emptyList());

        //when
        List<StockRankingResponse> result = stockService.getTop10Rankings();

        //then
        assertThat(result).isEmpty();
    }
}