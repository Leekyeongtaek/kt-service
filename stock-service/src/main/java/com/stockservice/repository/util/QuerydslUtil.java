package com.stockservice.repository.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

//todo 잘못된 정렬 조건(예외)에 대한 방어 테스트
@Slf4j
public class QuerydslUtil {

    /**
     * Spring Data의 Pageable 객체에서 정렬 정보를 추출하여, Querydsl의 OrderSpecifier 배열로 변환한다
     *
     * @param pageable 클라이언트가 요청한 페이징 및 정렬 정보
     * @param type     정렬을 적용할 엔티티의 클래스 파일
     * @param variable Querydsl 쿼리에서 사용하는 엔티티의 변수명 (예: "stock")
     * @return Querydsl 쿼리의 orderby()에 들어갈 OrderSpecifier 배열
     */
    public static OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable, Class<?> type, String variable) {

        // [시나리오 데이터 입력 상태]
        // pageable
        //  - 1. property="listedDate, direction=DESC"
        //  - 2. property="listedShares, direction=DESC"
        // type: Stock.class
        // variable: "stock"

        // 변환된 OrderSpecifier 객체들을 담을 빈 리스트
        // 현재 상태: orderSpecifiers = []
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (!pageable.getSort().isEmpty()) {

            // PathBuilder: 동적으로 Querydsl의 경로를 생성해 객체 초기화
            // 내부적으로 "stock"이라는 이름을 가진 Stock.class 타입의 엔티티 경로를 탐색할 준비 세팅
            PathBuilder<?> pathBuilder = new PathBuilder<>(type, variable);

            for (Sort.Order order : pageable.getSort()) {

                // Spring Data의 정렬 방향(ASC/DESC)을 Querydsl의 정렬 방향(Order.ASC/Order.DESC)으로 변환
                // 1회차 direction: Order.DESC
                // 2회차 direction: Order.DESC
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                try {
                    // pathBuilder.get(order.getProperty()): 클라이언트가 넘긴 필드명을 엔티티의 실제 경로로 변환
                    // new OrderSpecifier(...): 방향과 경로를 조합하여 하나의 정렬 조건 객체를 생성 후 리스트에 추가

                    // [1회차 실행]
                    // order.getProperty() -> "listedDate" 반환
                    // pathBuilder.get("listedDate") -> QStock.stock.listedDate 경로 객체로 동적 변환
                    // new OrderSpecifier(Order.DESC, QStock.stock.listedDate) 객체 생성 후 리스트에 추가
                    // 현재 리스트 상태: [orderSpecifiers(Order.DESC, stock.listedDate)]

                    // [2회차 실행]
                    // 같은 과정 반복
                    orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
                } catch (IllegalArgumentException e) {
                    log.error("getOrderSpecifiers error message : {}", e.getMessage());
                }
            }
        }

        // [최종 반환]
        // new OrderSpecifier[0] 의미
        // 자바에서 List를 배열로 변환할 때 관례적으로 사용하는 크기 0의 빈 배열 타입 지정자
        // toArray() 메서드는 전달받은 배열의 크기가 리스트의 크기보다 작으면, 리스트의 크기에 맞는 새로운 배열을 내부적으로 자동 생성하여 데이터를 채워 반환
        // 최종 반환 데이터: OrderSpecifier[]{ OrderSpecifier(DESC, stock.listedDate), OrderSpecifier(DESC, stock.listedShares) }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    /**
     * [비교용 샘플 메서드]
     * QuerydslUtil(PathBuilder)을 사용하지 않고, 수동으로 정렬을 구현할 경우 예시
     * 특징
     * - 클라이언트가 넘긴 문자열을 각각 case 문으로 분기 처리해야 함.
     * - 엔티티에 새로운 필드가 추가되거나 정렬 조건이 늘어날 때마다 코드 수정 필수 (OCP 위반)
     */
    private OrderSpecifier<?>[] whySample(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                // 대참사의 시작
                switch (order.getProperty()) {
                    case "id":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, null));
                        break;
                    case "listedDate":
                        break;
                    case "shortCode":
                        break;
                    default:
                        throw new IllegalArgumentException(order.getProperty());
                }
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
