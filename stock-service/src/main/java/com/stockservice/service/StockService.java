package com.stockservice.service;

import com.stockservice.domain.Stock;
import com.stockservice.domain.StockLimitedOffer;
import com.stockservice.dto.message.LimitedOfferPurchaseEvent;
import com.stockservice.dto.request.StockLimitedOfferPurchaseRequest;
import com.stockservice.dto.response.StockDetailResponse;
import com.stockservice.dto.response.StockRankingResponse;
import com.stockservice.dto.response.StockResponse;
import com.stockservice.enums.LimitedOfferResult;
import com.stockservice.messaging.StockProducer;
import com.stockservice.repository.StockLimitedOfferRepository;
import com.stockservice.repository.StockRepository;
import com.stockservice.repository.query.StockQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockQueryRepository stockQueryRepository;
    private final StockRedisService stockRedisService;
    private final StockLimitedOfferRepository stockLimitedOfferRepository;
    private final StockProducer stockProducer;

    public Page<StockResponse> searchStock(Pageable pageable) {
        return stockQueryRepository.searchStock(pageable);
    }

    public StockDetailResponse getStockDetail(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(IllegalArgumentException::new);

        stockRedisService.incrementScore(stockId);

        return new StockDetailResponse(stock);
    }

    public List<StockRankingResponse> getTop10Rankings() {
        List<Long> top10Ids = stockRedisService.getTop10Ids();

        if (top10Ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Stock> stocks = stockRepository.findAllById(top10Ids);

        Map<Long, Stock> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getId, stock -> stock));

        List<StockRankingResponse> stockRankingResponseList = new ArrayList<>();
        int rank = 1;

        for (Long top10Id : top10Ids) {
            Stock stock = stockMap.get(top10Id);
            if (stock != null) {
                stockRankingResponseList.add(new StockRankingResponse(rank++, stock));
            }
        }

        return stockRankingResponseList;
    }

    public void createLimitedOffer(Long stockId, int quantity) {
        stockRedisService.setUpLimitedOffer(stockId, quantity);
        stockLimitedOfferRepository.save(new StockLimitedOffer(stockId, quantity));
    }

    public LimitedOfferResult applyLimitedOffer(Long stockId, StockLimitedOfferPurchaseRequest request) {
        LimitedOfferResult limitedOfferResult = stockRedisService.tryPurchaseLimitedOffer(stockId, request.getUserId());
        if (LimitedOfferResult.SUCCESS.equals(limitedOfferResult)) {
            stockProducer.send(new LimitedOfferPurchaseEvent(stockId, request));
        }
        return limitedOfferResult;
    }
}
