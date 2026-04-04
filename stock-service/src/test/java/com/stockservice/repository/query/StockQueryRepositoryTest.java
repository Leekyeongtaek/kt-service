package com.stockservice.repository.query;

import com.stockservice.config.TestQuerydslConfig;
import com.stockservice.dto.StockResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({StockQueryRepository.class, TestQuerydslConfig.class})
@Sql(scripts = "/stock-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StockQueryRepositoryTest {

    @Autowired
    private StockQueryRepository stockQueryRepository;

    @Test
    @DisplayName("조건 없는 목록 조회 시, 오름차순으로 페이징되어 반환된다")
    void default_test() {
        // given
        int pageNumber = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        // when
        Page<StockResponse> result = stockQueryRepository.searchStock(pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(2);

        List<StockResponse> content = result.getContent();
        assertThat(content).hasSize(5);

        assertThat(content)
                .extracting(StockResponse::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L);
    }
}