package com.stockservice.service;

import com.stockservice.dto.StockResponse;
import com.stockservice.repository.query.StockQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockService {

    private final StockQueryRepository stockQueryRepository;

    public Page<StockResponse> searchStock(Pageable pageable) {
        return stockQueryRepository.searchStock(pageable);
    }
}
