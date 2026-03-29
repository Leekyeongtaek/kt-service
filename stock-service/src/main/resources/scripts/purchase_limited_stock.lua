--- [한정 종목 선착순 구매 스크립트]
--- KEYS[1]: 한정 종목 잔여 수량 레디스 키
--- KEYS[2]: 구매한 유저 명부 레디스 키
--- ARGV[1]: 구매 요청 유저 ID
--- ARGV[2]: 차감 수량
---
--- @return 1 (성공), 0 (재고 부족), -1 (중복 구매)

-- 1. 중복 구매 검증
if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
    return -1
end

-- 2. 잔여 수량 검증
local quantity = tonumber(redis.call('GET', KEYS[1]))
if quantity == nil or quantity < tonumber(ARGV[2]) then
    return 0
end

-- 3. 수량 차감 후 구매한 유저 명부 등록
redis.call('DECRBY', KEYS[1], ARGV[2])
redis.call('SADD', KEYS[2], ARGV[1])

return 1