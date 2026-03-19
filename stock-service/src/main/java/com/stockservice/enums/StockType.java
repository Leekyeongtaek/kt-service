package com.stockservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockType {
    COMMON("보통주"),
    OLD_PREFERRED("구형우선주"),
    NEW_PREFERRED("신형우선주");

    private final String StockTypeName;
}
