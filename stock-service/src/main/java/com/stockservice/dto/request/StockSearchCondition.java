package com.stockservice.dto.request;

import com.stockservice.enums.Department;
import com.stockservice.enums.MarketType;
import com.stockservice.enums.SecuritiesType;
import com.stockservice.enums.StockType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StockSearchCondition {

    private String keyword;
    private MarketType marketType; // 시장구분
    private StockType stockType; // 주식 종류
    private Department department; // 소속부
    private SecuritiesType securitiesType; // 증권구분

    @Builder
    public StockSearchCondition(String keyword, MarketType marketType, StockType stockType, Department department, SecuritiesType securitiesType) {
        this.keyword = keyword;
        this.marketType = marketType;
        this.stockType = stockType;
        this.department = department;
        this.securitiesType = securitiesType;
    }
}
