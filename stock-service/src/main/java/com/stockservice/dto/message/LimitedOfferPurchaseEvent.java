package com.stockservice.dto.message;

import com.stockservice.dto.request.StockLimitedOfferPurchaseRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class LimitedOfferPurchaseEvent {

    private Long stockId;
    private Long stockLimitedOfferId;
    private Long userId;
    private LocalDateTime purchasedAt;

    public LimitedOfferPurchaseEvent(Long stockId, StockLimitedOfferPurchaseRequest request) {
        this.stockId = stockId;
        this.stockLimitedOfferId = request.getStockLimitedOfferId();
        this.userId = request.getUserId();
        this.purchasedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "LimitedOfferPurchaseEvent{" +
                "StockLimitedOfferId=" + stockLimitedOfferId +
                ", userId=" + userId +
                ", stockId=" + stockId +
                '}';
    }
}
