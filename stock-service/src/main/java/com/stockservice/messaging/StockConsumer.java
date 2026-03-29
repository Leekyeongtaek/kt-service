package com.stockservice.messaging;

import com.stockservice.domain.StockLimitedOfferPurchase;
import com.stockservice.dto.message.LimitedOfferPurchaseEvent;
import com.stockservice.repository.StockLimitedOfferPurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StockConsumer {

    private final StockLimitedOfferPurchaseRepository stockLimitedOfferPurchaseRepository;

    @KafkaListener(
            topics = "stock.limited-offer.purchased",
            groupId = "stock-service.limited-offer.purchased-group")
    public void listener(LimitedOfferPurchaseEvent message) {
        log.info("카프카 메시지 수신 : {}", message);

        StockLimitedOfferPurchase stockLimitedOfferPurchase = StockLimitedOfferPurchase.builder()
                .StockLimitedOfferId(message.getStockLimitedOfferId())
                .userId(message.getUserId())
                .stockId(message.getStockId())
                .purchasedAt(message.getPurchasedAt())
                .build();

        stockLimitedOfferPurchaseRepository.save(stockLimitedOfferPurchase);

        log.info("DB 저장 완료");
    }
}
