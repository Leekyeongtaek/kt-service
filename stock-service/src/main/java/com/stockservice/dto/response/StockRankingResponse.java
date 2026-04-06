package com.stockservice.dto.response;

import com.stockservice.domain.Stock;
import lombok.Getter;

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
