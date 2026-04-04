package com.stockservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MarketType {
    KOSPI("코스피"),
    KOSDAQ("코스닥"),
    KOSDAQ_GLOBAL("코스닥 글로벌"),
    KONEX("코넥스");

    private final String marketTypeName;
}
