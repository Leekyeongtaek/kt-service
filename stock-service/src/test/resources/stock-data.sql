DELETE FROM stock;

INSERT INTO stock (stock_id, standard_code, short_code, kor_name, kor_abbr_name, eng_name, listed_date, market_type, securities_type, department, stock_type, face_value, listed_shares) VALUES
-- [상장일] 최소(1956년): 경방 / 최대(2026년): 덕양에너젠
(1, 'KR7000050005', '000050', '경방보통주', '경방', 'Kyungbang', '1956-03-03', 'KOSPI', 'STOCK', NULL, 'COMMON', 500, 27415270),
(2, 'KR70001A0001', '0001A0', '덕양에너젠', '덕양에너젠', 'DEOKYANG ENERGEN', '2026-01-30', 'KOSDAQ', 'STOCK', 'MID_TIER', 'COMMON', 500, 24791195),

-- [상장주식수] 최소(20만): CJ씨푸드1우 / 최대(59억): 삼성전자
(3, 'KR7011151008', '011155', 'CJ씨푸드1우선주', 'CJ씨푸드1우', 'CJSEAFOOD(1P)', '1990-01-13', 'KOSPI', 'STOCK', NULL, 'NEW_PREFERRED', 500, 200000),
(4, 'KR7005930003', '005930', '삼성전자보통주', '삼성전자', 'SamsungElectronics', '1975-06-11', 'KOSPI', 'STOCK', NULL, 'COMMON', 100, 5919637922),

-- [단축코드] 최소(000020): 동화약품 / 최대(950250): 테라뷰
(5, 'KR7000020008', '000020', '동화약품보통주', '동화약품', 'DongwhaPharm', '1976-03-24', 'KOSPI', 'STOCK', NULL, 'COMMON', 1000, 27931470),
(6, 'KR8826050005', '950250', '테라뷰홀딩스', '테라뷰', 'TERAVIEW HOLDINGS', '2025-12-09', 'KOSDAQ', 'INVESTMENT_COMPANY', 'BLUE_CHIP', 'COMMON', 0, 35517731),

-- [한글 종목약명] 최소(숫자 시작): 3S / 최대(ㅎ 시작): 힘스
(7, 'KR7060310000', '060310', '삼에스코리아', '3S', '3S KOREA', '2002-04-23', 'KOSDAQ', 'DEPOSITORY_RECEIPT', 'MID_TIER', 'COMMON', 500, 53059040),
(8, 'KR7238490007', '238490', '힘스', '힘스', 'HIMS CO., LTD.', '2017-07-20', 'KOSDAQ', 'STOCK', 'BLUE_CHIP', 'COMMON', 500, 11312236),

-- 데이터 다양성을 위한 중간값 샘플: 진코스텍 / 에브리봇
(9, 'KR7250030004', '250030', '진코스텍', '진코스텍', 'JINCOSTECH', '2019-11-29', 'KONEX', 'STOCK', 'MID_TIER', 'COMMON', 500, 2589337),
(10, 'KR7270660004', '270660', '에브리봇 주식회사', '에브리봇', 'EVERYBOT Inc.', '2021-07-28', 'KOSDAQ', 'REIT', 'MID_TIER', 'COMMON', 500, 12690583),

-- [마켓 타입] KOSDAQ_GLOBAL: 포그코엠텍
(11, 'KR7009520008', '009520', '(주)포스코엠텍', '포스코엠텍', 'POSCO M-TECH CO.,LTD.', '1997-11-10', 'KOSDAQ_GLOBAL', 'FOREIGN_STOCK', 'BLUE_CHIP', 'COMMON', 500, 41642703),

-- [다중 정렬] 에브리봇과 상장일 동일하나 주식수는 다름
(12, 'KR7239340003', '239340', '줌인터넷 주식회사', '줌인터넷', 'ZUMinternet', '2021-07-28', 'KOSDAQ', 'INFRASTRUCTURE_INVESTMENT', 'MID_TIER', 'COMMON', 500, 27361812);