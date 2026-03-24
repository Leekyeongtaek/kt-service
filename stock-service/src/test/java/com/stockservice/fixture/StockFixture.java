package com.stockservice.fixture;

import com.stockservice.domain.Stock;
import com.stockservice.enums.Department;
import com.stockservice.enums.MarketType;
import com.stockservice.enums.StockType;

import java.time.LocalDate;

public class StockFixture {

    public static Stock createStandardStock() {
        return Stock.builder()
                .id(1L)
                .standardCode("KR7095570008")
                .shortCode("095570")
                .korName("AJ네트웍스보통주")
                .marketType(MarketType.KOSPI)
                .department(null) // 코스피는 소속부가 없는 경우가 많음
                .stockType(StockType.COMMON)
                .faceValue(1000)
                .listedShares(45252759L)
                .listedDate(LocalDate.of(2015, 8, 21))
                .build();
    }

    public static Stock createKosdaqVentureStock() {
        return Stock.builder()
                .id(2L)
                .standardCode("KR7263920001")
                .shortCode("263920")
                .korName("휴엠앤씨")
                .marketType(MarketType.KOSDAQ)
                .department(Department.BLUE_CHIP) // 우량기업부
                .stockType(StockType.COMMON)
                .faceValue(500)
                .listedShares(9809026L)
                .listedDate(LocalDate.of(2017, 9, 28))
                .build();
    }

    public static Stock createStockWithId(Long id, String korName) {
        return Stock.builder()
                .id(id)
                .shortCode("000000")
                .korName(korName)
                .marketType(MarketType.KOSPI)
                .stockType(StockType.COMMON)
                .build();
    }
}
