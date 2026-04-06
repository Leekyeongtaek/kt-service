package com.stockservice.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.stockservice.domain.Stock;
import com.stockservice.enums.Department;
import com.stockservice.enums.MarketType;
import com.stockservice.enums.StockType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

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
    private MarketType marketType;
    private String marketTypeName;
    private String securitiesType;
    private Department department;
    private String departmentName;
    private StockType stockType;
    private String stockTypeName;
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
        this.marketTypeName = stock.getMarketType().getMarketTypeName();
        this.securitiesType = stock.getSecuritiesType();
        this.department = stock.getDepartment();
        this.departmentName = Optional.ofNullable(stock.getDepartment())
                .map(Department::getDepartmentName)
                .orElse(null);
        this.stockType = stock.getStockType();
        this.stockTypeName = stock.getStockType().getStockTypeName();
        this.faceValue = stock.getFaceValue();
        this.listedShares = stock.getListedShares();
    }
}
