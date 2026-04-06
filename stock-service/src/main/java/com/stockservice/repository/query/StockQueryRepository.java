package com.stockservice.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stockservice.domain.Stock;
import com.stockservice.dto.QStockResponse;
import com.stockservice.dto.StockResponse;
import com.stockservice.repository.util.QuerydslUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.stockservice.domain.QStock.stock;

@Repository
public class StockQueryRepository {

    private final JPAQueryFactory queryFactory;

    public StockQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Page<StockResponse> searchStock(Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = QuerydslUtil.getOrderSpecifiers(pageable, Stock.class, "stock");

        List<StockResponse> stocks = queryFactory
                .select(new QStockResponse(stock))
                .from(stock)
                .orderBy(orderSpecifiers)
                .orderBy(stock.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(stock.count())
                .from(stock);

        return PageableExecutionUtils.getPage(stocks, pageable, countQuery::fetchOne);
    }
}
