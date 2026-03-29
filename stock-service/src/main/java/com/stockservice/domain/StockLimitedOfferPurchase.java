package com.stockservice.domain;

import com.stockservice.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock_limited_offer_purchase")
@Entity
public class StockLimitedOfferPurchase extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_limited_offer_purchase_id")
    private Long id;

    @Column(name = "stock_limited_offer_id", nullable = false, updatable = false)
    private Long StockLimitedOfferId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "stock_id", nullable = false, updatable = false)
    private Long stockId;

    @Column(name = "purchased_at", nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    @Builder
    public StockLimitedOfferPurchase(Long StockLimitedOfferId, Long userId, Long stockId,  LocalDateTime purchasedAt) {
        this.StockLimitedOfferId = StockLimitedOfferId;
        this.userId = userId;
        this.stockId = stockId;
        this.purchasedAt = purchasedAt;
    }
}
