package com.stockservice.service;

import com.stockservice.domain.Stock;
import com.stockservice.dto.StockDetailResponse;
import com.stockservice.dto.StockRankingResponse;
import com.stockservice.dto.StockResponse;
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
    private final StockRankingService stockRankingService;

    public Page<StockResponse> searchStock(Pageable pageable) {
        return stockQueryRepository.searchStock(pageable);
    }

    public StockDetailResponse getStockDetail(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(IllegalArgumentException::new);

        stockRankingService.incrementScore(stockId);

        return new StockDetailResponse(stock);
    }

    public List<StockRankingResponse> getTop10Rankings() {
        List<Long> top10Ids = stockRankingService.getTop10Ids();

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
            stockRankingResponseList.add(new StockRankingResponse(rank++, stock));
        }

        return stockRankingResponseList;
    }
}
