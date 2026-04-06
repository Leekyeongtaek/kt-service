package com.stockservice.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stockservice.domain.Stock;
import com.stockservice.dto.request.StockSearchCondition;
import com.stockservice.dto.response.QStockResponse;
import com.stockservice.dto.response.StockResponse;
import com.stockservice.enums.Department;
import com.stockservice.enums.MarketType;
import com.stockservice.enums.SecuritiesType;
import com.stockservice.enums.StockType;
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

    public Page<StockResponse> searchStock(Pageable pageable, StockSearchCondition condition) {
        OrderSpecifier<?>[] orderSpecifiers = QuerydslUtil.getOrderSpecifiers(pageable, Stock.class, "stock");

        List<StockResponse> stocks = queryFactory
                .select(new QStockResponse(stock))
                .from(stock)
                .where(
                        eqMarketType(condition.getMarketType()),
                        eqStockType(condition.getStockType()),
                        eqSecuritiesType(condition.getSecuritiesType()),
                        eqDepartment(condition.getDepartment())
                )
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

    private BooleanExpression eqMarketType(MarketType marketType) {
        return marketType != null ? stock.marketType.eq(marketType) : null;
    }

    private BooleanExpression eqDepartment(Department department) {
        return department != null ? stock.department.eq(department) : null;
    }

    private BooleanExpression eqStockType(StockType stockType) {
        return stockType != null ? stock.stockType.eq(stockType) : null;
    }

    private BooleanExpression eqSecuritiesType(SecuritiesType securitiesType) {
        return securitiesType != null ? stock.securitiesType.eq(securitiesType) : null;
    }
}
