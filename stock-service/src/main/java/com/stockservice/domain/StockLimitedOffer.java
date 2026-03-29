package com.stockservice.domain;

import com.stockservice.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock_limited_offer")
@Entity
public class StockLimitedOffer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_limited_offer_id")
    private Long id;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public StockLimitedOffer(Long stockId, Integer quantity) {
        this.stockId = stockId;
        this.quantity = quantity;
    }
}
