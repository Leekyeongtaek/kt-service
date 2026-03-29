package com.stockservice.messaging;

import com.stockservice.dto.message.LimitedOfferPurchaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String STOCK_EVENT_TOPIC = "stock.limited-offer.purchased";

    public void send(LimitedOfferPurchaseEvent message) {
        log.info("카프카 메시지 발행: {}", message);
        kafkaTemplate.send(STOCK_EVENT_TOPIC, message);
    }
}
