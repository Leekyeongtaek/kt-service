package com.stockservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockservice.dto.request.StockLimitedOfferPurchaseRequest;
import com.stockservice.enums.LimitedOfferResult;
import com.stockservice.service.StockRedisService;
import com.stockservice.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StockService stockService;

    @MockitoBean
    private StockRedisService stockRedisService;

    @Test
    @DisplayName("특정 종목의 한정 상품 재고를 설정하면, 200 OK와 성공 메시지를 반환한다.")
    void createLimitedOffer_Success() throws Exception {
        //given
        Long stockId = 1L;
        int quantity = 100;

        //when & then
        mockMvc.perform(post("/api/v1/stocks/{stockId}/limited-offers", stockId)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andExpect(content().string("종목 ID " + stockId + "의 한정 상품 재고가 " + quantity + "개로 설정되었습니다."));

        verify(stockService).createLimitedOffer(stockId, quantity);
    }

    @Test
    @DisplayName("특정 종목의 한정 상품 매수를 신청하면, 200 OK와 성공 메시지를 반환한다.")
    void applyStockLimitedOffer_Success() throws Exception {
        // given
        Long stockId = 1L;
        Long stockLimitedOfferId = 1L;
        Long userId = 100L;
        StockLimitedOfferPurchaseRequest request = new StockLimitedOfferPurchaseRequest(stockLimitedOfferId, userId);

        given(stockService.applyLimitedOffer(eq(stockId), any(StockLimitedOfferPurchaseRequest.class)))
                .willReturn(LimitedOfferResult.SUCCESS);

        // when & then
        mockMvc.perform(post("/api/v1/stocks/{stockId}/limited-offers/purchases", stockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(LimitedOfferResult.SUCCESS.getMessage()));

        verify(stockService).applyLimitedOffer(eq(stockId), any(StockLimitedOfferPurchaseRequest.class));
    }
}