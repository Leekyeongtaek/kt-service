package com.stockservice.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LimitedOfferResult {

    SUCCESS(1L, "매수 신청이 성공했습니다."),
    SOLD_OUT(0L, "선착순 매수 수량이 모두 소진되었습니다."),
    DUPLICATE(-1L, "이미 선착순 매수 이벤트에 참여한 유저입니다.");

    private final Long code;
    private final String message;

    LimitedOfferResult(Long code, String message) {
        this.code = code;
        this.message = message;
    }

    public static LimitedOfferResult fromCode(Long code) {
        if (code == null) {
            throw new RuntimeException("레디스 스크립트 실행 오류: 결과가 null 입니다.");
        }
        return Arrays.stream(LimitedOfferResult.values())
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("정의되지 않은 레디스 응답 코드 입니다: " + code));
    }
}
