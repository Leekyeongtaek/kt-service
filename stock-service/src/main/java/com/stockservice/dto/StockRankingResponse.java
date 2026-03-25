package com.stockservice.dto;

import com.stockservice.domain.Stock;
import com.stockservice.enums.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StockRankingResponse {

    private int rank;
    private Long stockId;
    private String korName;
    private String shortCode;
    private String marketTypeName;

    public StockRankingResponse(int rank, Stock stock) {
        this.rank = rank;
        this.stockId = stock.getId();
        this.korName = stock.getKorName();
        this.shortCode = stock.getShortCode();
        this.marketTypeName = stock.getMarketType().getMarketTypeName();
    }
}
