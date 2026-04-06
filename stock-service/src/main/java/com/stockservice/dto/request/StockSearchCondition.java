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

    //검색어 필드
    private MarketType marketType; // 시장구분
    private StockType stockType; // 주식 종류
    private Department department; // 소속부
    private SecuritiesType securitiesType; // 증권구분

    @Builder
    public StockSearchCondition(MarketType marketType, StockType stockType, Department department, SecuritiesType securitiesType) {
        this.marketType = marketType;
        this.stockType = stockType;
        this.department = department;
        this.securitiesType = securitiesType;
    }
}
