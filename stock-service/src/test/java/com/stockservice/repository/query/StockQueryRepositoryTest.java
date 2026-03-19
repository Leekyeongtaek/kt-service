package com.stockservice.repository.query;

import com.stockservice.dto.StockResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/stock-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StockQueryRepositoryTest {

    @Autowired
    private StockQueryRepository stockQueryRepository;

    @Test
    @DisplayName("조건 없는 목록 조회 시, ID 내림차순으로 페이징되어 반환된다")
    void default_test() {
        // given
        int pageNumber = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        // when
        Page<StockResponse> result = stockQueryRepository.searchStock(pageRequest);

        // then
        // 1. 전체 데이터 및 페이지 수 검증
        assertThat(result.getTotalElements()).isEqualTo(20);
        assertThat(result.getTotalPages()).isEqualTo(4);
        assertThat(result.hasNext()).isTrue();

        // 2. 현재 페이지 데이터 검증
        List<StockResponse> content = result.getContent();
        assertThat(content).hasSize(5);

        // 3. 정렬 검증
        // 가장 큰 ID(20번)인 'DB하이텍'이 첫 번째로 나와야 한다.
        assertThat(content.get(0).getId()).isEqualTo(20L);
        assertThat(content.get(0).getKorName()).isEqualTo("DB하이텍보통주");

        // 1페이지의 마지막(5번째) 데이터는 ID가 16인 'CS홀딩스'여야 한다
        assertThat(content.get(4).getId()).isEqualTo(16L);
        assertThat(content.get(4).getKorName()).isEqualTo("CS홀딩스보통주");
    }
}