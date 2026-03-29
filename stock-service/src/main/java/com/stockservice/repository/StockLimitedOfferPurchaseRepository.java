package com.stockservice.repository;

import com.stockservice.domain.StockLimitedOfferPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLimitedOfferPurchaseRepository extends JpaRepository<StockLimitedOfferPurchase, Long> {
}
