package com.stockservice.domain;

import com.stockservice.enums.Department;
import com.stockservice.enums.MarketType;
import com.stockservice.enums.StockType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
}
