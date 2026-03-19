package com.stockservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Department {
    MID_TIER("중견기업부"),
    BLUE_CHIP("우량기업부");

    private final String DepartmentName;
}
