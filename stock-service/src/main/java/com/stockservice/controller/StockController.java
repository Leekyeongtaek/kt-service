package com.stockservice.controller;

import com.stockservice.domain.Stock;
import com.stockservice.dto.StockDetailResponse;
import com.stockservice.dto.StockRankingResponse;
import com.stockservice.dto.StockResponse;
import com.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/stocks")
@RestController
public class StockController {

    private final StockService stockService;

    //todo 반환 데이터 축약
    @GetMapping("/search")
    public ResponseEntity<Page<StockResponse>> getStocks(Pageable pageable) {
        Page<StockResponse> stockResponses = stockService.searchStock(pageable);
        return new ResponseEntity<>(stockResponses, HttpStatus.OK);
    }

    @GetMapping("/{stockId}")
    public ResponseEntity<StockDetailResponse> getStock(@PathVariable Long stockId) {
        return ResponseEntity.ok(stockService.getStockDetail(stockId));
    }

    @GetMapping("/rankings")
    public ResponseEntity<List<StockRankingResponse>> getTop10Rankings() {
        return ResponseEntity.ok(stockService.getTop10Rankings());
    }

    @GetMapping("/version")
    public ResponseEntity<String> checkAppVersion() {
        final String appVersion = "현재 앱 버전: 1.0.1v";
        return new ResponseEntity<>(appVersion, HttpStatus.OK);
    }
}
