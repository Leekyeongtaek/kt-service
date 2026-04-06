package com.stockservice.enums;

import lombok.Getter;

/**
 * 증권구분
 */
@Getter
public enum SecuritiesType {

    STOCK("주권"),
    REIT("부동산투자회사"),
    DEPOSITORY_RECEIPT("주식예탁증권"),
    FOREIGN_STOCK("외국주권"),
    INFRASTRUCTURE_INVESTMENT("사회간접자본투융자회사"),
    INVESTMENT_COMPANY("투자회사");

    private final String description;

    SecuritiesType(String description) {
        this.description = description;
    }
}
