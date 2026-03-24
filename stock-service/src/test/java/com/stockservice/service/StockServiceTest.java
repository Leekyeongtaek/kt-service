package com.stockservice.service;

import com.stockservice.domain.Stock;
import com.stockservice.dto.StockDetailResponse;
import com.stockservice.fixture.StockFixture;
import com.stockservice.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}