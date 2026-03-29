package com.stockservice.domain;

import com.stockservice.enums.Department;
import com.stockservice.enums.MarketType;
import com.stockservice.enums.StockType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "stock")
@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;
    private String standardCode;
    private String shortCode;
    private String korName;
    private String korAbbrName;
    private String engName;
    private LocalDate listedDate;
    @Enumerated(EnumType.STRING)
    private MarketType marketType;
    private String securitiesType;
    @Enumerated(EnumType.STRING)
    private Department department;
    @Enumerated(EnumType.STRING)
    private StockType stockType;
    private int faceValue;
    private long listedShares;

    @Builder
    public Stock(Long id, String standardCode, String shortCode, String korName, String korAbbrName, String engName, LocalDate listedDate, MarketType marketType, String securitiesType, Department department, StockType stockType, int faceValue, long listedShares) {
        this.id = id;
        this.standardCode = standardCode;
        this.shortCode = shortCode;
        this.korName = korName;
        this.korAbbrName = korAbbrName;
        this.engName = engName;
        this.listedDate = listedDate;
        this.marketType = marketType;
        this.securitiesType = securitiesType;
        this.department = department;
        this.stockType = stockType;
        this.faceValue = faceValue;
        this.listedShares = listedShares;
    }
}
