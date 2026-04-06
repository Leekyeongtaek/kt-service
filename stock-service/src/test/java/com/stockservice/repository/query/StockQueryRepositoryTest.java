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
import org.springframework.data.domain.Sort;
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
    @DisplayName("조건 없는 목록 조회 시, ID 기준 내침차순으로 정렬되어 반환된다")
    void search_default() {
        // given
        int pageNumber = 0;
        int pageSize = 5;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        // when
        Page<StockResponse> result = stockQueryRepository.searchStock(pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(12);
        assertThat(result.getTotalPages()).isEqualTo(3);

        List<StockResponse> content = result.getContent();
        assertThat(content).hasSize(5);

        assertThat(content)
                .extracting(StockResponse::getId)
                .containsExactly(12L, 11L, 10L, 9L, 8L);
    }

    /*
    * 상장일 기준 내림차순 정렬 (최신순)
        ID	한글 약명	상장일
        2	덕양에너젠	2026-01-30
        6	테라뷰	2025-12-09
        12	줌인터넷	2021-07-28
        10	에브리봇	2021-07-28
        9	진코스텍	2019-11-29
        8	힘스	    2017-07-20
        7	3S	    2002-04-23
        11	포스코엠텍	1997-11-10
        3	CJ씨푸드1우 1990-01-13
        5	동화약품	1976-03-24
        4	삼성전자	1975-06-11
        1	경방	1956-03-03
    * */
    @Test
    @DisplayName("상장일 내림차순 조건으로 목록 조회 시, 최신 상장일 순으로 정렬되어 반환된다")
    void search_sortByListedDateDesc() {
        //given
        assertStockSingleSortOrder("listedDate", Sort.Direction.DESC,
                2L, 6L, 12L, 10L, 9L, 8L, 7L, 11L, 3L, 5L, 4L, 1L);
    }

    /*
    * 상장주식수 기준 내림차순 정렬 (많은순)
    *   ID	한글 약명	상장주식수
        4	삼성전자	5,919,637,922
        7	3S	53,059,040
        11	포스코엠텍	41,642,703
        6	테라뷰	35,517,731
        5	동화약품	27,931,470
        1	경방	27,415,270
        12	줌인터넷	27,361,812
        2	덕양에너젠	24,791,195
        10	에브리봇	12,690,583
        8	힘스	11,312,236
        9	진코스텍	2,589,337
        3	CJ씨푸드1우	200,000
    * */
    @Test
    @DisplayName("상장주식수 내림차순 조건으로 목록 조회 시, 상장주식수가 많은 순으로 정렬되어 반환된다")
    void search_sortByListedSharesDesc() {
        //given
        assertStockSingleSortOrder("listedShares", Sort.Direction.DESC,
                4L, 7L, 11L, 6L, 5L, 1L, 12L, 2L, 10L, 8L, 9L, 3L);
    }

    /*
    * 단축코드 기준 오름차순 정렬
    *   ID	한글 약명	단축코드
        5	동화약품	000020
        1	경방	    000050
        2	덕양에너젠	0001A0
        4	삼성전자	005930
        11	포스코엠텍	009520
        3	CJ씨푸드1우 011155
        7	3S	    060310
        8	힘스	    238490
        12	줌인터넷	239340
        9	진코스텍	250030
        10	에브리봇	270660
        6	테라뷰	950250
    * */
    @Test
    @DisplayName("단축코드 오름차순 조건으로 목록 조회 시, 단축코드 사전순으로 정렬되어 반환된다")
    void search_sortByShortCodeAsc() {
        //given
        assertStockSingleSortOrder("shortCode", Sort.Direction.ASC,
                5L, 1L, 2L, 4L, 11L, 3L, 7L, 8L, 12L, 9L, 10L, 6L);
    }

    /*
    * 한글 약명 기준 오름차순 정렬 (가나다순)
    *   ID	한글 약명
        7	3S
        3	CJ씨푸드1우
        1	경방
        2	덕양에너젠
        5	동화약품
        4	삼성전자
        10	에브리봇
        12	줌인터넷
        9	진코스텍
        6	테라뷰
        11	포스코엠텍
        8	힘스
    * */
    @Test
    @DisplayName("한글 종목약명 오름차순 조건으로 목록 조회 시, 숫자·영문·한글 순으로 정렬되어 반환된다")
    void search_sortByKorAbbrNameAsc() {
        //given
        assertStockSingleSortOrder("korAbbrName", Sort.Direction.ASC,
                7L, 3L, 1L, 2L, 5L, 4L, 10L, 12L, 9L, 6L, 11L, 8L);
    }

    private void assertStockSingleSortOrder(String property, Sort.Direction direction, Long... expectedIds) {
        //when
        Page<StockResponse> result = stockQueryRepository.searchStock(PageRequest.of(0, 12, Sort.by(direction, property)));

        //then
        assertThat(result.getContent())
                .extracting(StockResponse::getId)
                .containsExactly((expectedIds));
    }

    /*
    * 다중 정렬 (1순위: 상장일 내림차순 / 2순위: 상장주식수 내림차순)
    *   ID	상장일	    상장주식수	    한글 약명
        2	2026-01-30	24,791,195	덕양에너젠
        6	2025-12-09	35,517,731	테라뷰
        12	2021-07-28	27,361,812	줌인터넷
        10	2021-07-28	12,690,583	에브리봇
        9	2019-11-29	2,589,337	진코스텍
        8	2017-07-20	11,312,236	힘스
        7	2002-04-23	53,059,040	3S
        11	1997-11-10	41,642,703	포스코엠텍
        3	1990-01-13	200,000	    CJ씨푸드1우
        5	1976-03-24	27,931,470	동화약품
        4	1975-06-11	5,919,637,922 삼성전자
        1	1956-03-03	27,415,270	경방
    * */
    @Test
    @DisplayName("다중 정렬 조건으로 목록 조회 시, 상장일 최신순으로 우선 정렬되며 상장일이 같으면 주식수가 많은 순으로 정렬되어 반환된다")
    void search_multiSort_ListedDateAndListedShares() {
        //given
        Sort sort = Sort.by(Sort.Direction.DESC, "listedDate")
                .and(Sort.by(Sort.Direction.DESC, "listedShares"));

        //when
        Page<StockResponse> result = stockQueryRepository.searchStock(PageRequest.of(0, 12, sort));

        //then
        List<StockResponse> content = result.getContent();
        assertThat(content).hasSize(12);

        assertThat(content)
                .extracting(StockResponse::getId)
                .containsExactly(2L, 6L, 12L, 10L, 9L, 8L, 7L, 11L, 3L, 5L, 4L, 1L);
    }
}