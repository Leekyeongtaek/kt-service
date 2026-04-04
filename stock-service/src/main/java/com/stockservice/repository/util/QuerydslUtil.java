package com.stockservice.repository.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

//소스 근거
//주석 설명
@Slf4j
public class QuerydslUtil {

    public static OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable, Class<?> type, String variable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (!pageable.getSort().isEmpty()) {
            PathBuilder<?> pathBuilder = new PathBuilder<>(type, variable);

            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                try {
                    orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
                } catch (IllegalArgumentException e) {
                    log.error("getOrderSpecifiers error message : {}", e.getMessage());
                }
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
