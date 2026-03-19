package com.stockservice.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.stockservice.domain.Stock;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StockResponse {

    private Long id;
    private String standardCode;
    private String shortCode;
    private String korName;
    private String korAbbrName;
    private String engName;
    private LocalDate listedDate;
    private String marketType;
    private String securitiesType;
    private String department;
    private String stockType;
    private int faceValue;
    private long listedShares;

    @QueryProjection
    public StockResponse(Stock stock) {
        this.id = stock.getId();
        this.standardCode = stock.getStandardCode();
        this.shortCode = stock.getShortCode();
        this.korName = stock.getKorName();
        this.korAbbrName = stock.getKorAbbrName();
        this.engName = stock.getEngName();
        this.listedDate = stock.getListedDate();
        this.marketType = stock.getMarketType();
        this.securitiesType = stock.getSecuritiesType();
        this.department = stock.getDepartment();
        this.stockType = stock.getStockType();
        this.faceValue = stock.getFaceValue();
        this.listedShares = stock.getListedShares();
    }
}
