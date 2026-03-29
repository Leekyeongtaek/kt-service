package com.stockservice.repository;

import com.stockservice.domain.StockLimitedOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLimitedOfferRepository extends JpaRepository<StockLimitedOffer, Long> {
}
