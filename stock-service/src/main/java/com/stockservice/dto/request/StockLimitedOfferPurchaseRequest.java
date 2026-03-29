package com.stockservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockLimitedOfferPurchaseRequest {

    private Long stockLimitedOfferId;
    private Long userId;

    public StockLimitedOfferPurchaseRequest(Long stockLimitedOfferId, Long userId) {
        this.stockLimitedOfferId = stockLimitedOfferId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "StockLimitedOfferPurchaseRequest{" +
                "stockLimitedOfferId=" + stockLimitedOfferId +
                ", userId=" + userId +
                '}';
    }
}
