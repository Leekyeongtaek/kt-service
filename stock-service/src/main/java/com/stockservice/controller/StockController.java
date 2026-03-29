package com.stockservice.controller;

import com.stockservice.dto.StockDetailResponse;
import com.stockservice.dto.request.StockLimitedOfferPurchaseRequest;
import com.stockservice.dto.StockRankingResponse;
import com.stockservice.dto.StockResponse;
import com.stockservice.enums.LimitedOfferResult;
import com.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
@RestController
public class StockController {

    private final StockService stockService;

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

    //limited-offers: 한정 상품
    @PostMapping("/{stockId}/limited-offers")
    public ResponseEntity<String> createLimitedOffer(
            @PathVariable(value = "stockId") Long stockId,
            @RequestParam int quantity) {

        stockService.createLimitedOffer(stockId, quantity);
        return ResponseEntity.ok("종목 ID " + stockId + "의 한정 상품 재고가 " + quantity + "개로 설정되었습니다.");
    }

    @PostMapping("/{stockId}/limited-offers/purchases")
    public ResponseEntity<String> applyStockLimitedOffer(
            @PathVariable Long stockId,
            @RequestBody StockLimitedOfferPurchaseRequest request) {

        LimitedOfferResult limitedOfferResult = stockService.applyLimitedOffer(stockId, request);
        return ResponseEntity.ok(limitedOfferResult.getMessage());
    }

    @GetMapping("/version")
    public ResponseEntity<String> checkAppVersion() {
        final String appVersion = "현재 앱 버전: 1.0.1v";
        return new ResponseEntity<>(appVersion, HttpStatus.OK);
    }
}
