package com.stockservice.controller;

import com.stockservice.domain.Stock;
import com.stockservice.dto.StockResponse;
import com.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/stocks")
@RestController
public class StockController {

    private final StockService stockService;

    @GetMapping("/search")
    public ResponseEntity<Page<StockResponse>> getStocks(Pageable pageable) {
        Page<StockResponse> stockResponses = stockService.searchStock(pageable);
        return new ResponseEntity<>(stockResponses, HttpStatus.OK);
    }

    @GetMapping("/version")
    public ResponseEntity<String> checkAppVersion() {
        final String appVersion = "현재 앱 버전: 1.0.0v";
        return new ResponseEntity<>(appVersion, HttpStatus.OK);
    }
}
