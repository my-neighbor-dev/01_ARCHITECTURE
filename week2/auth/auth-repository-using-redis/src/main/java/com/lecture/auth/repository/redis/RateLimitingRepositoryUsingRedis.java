package com.lecture.auth.repository.redis;

import com.lecture.auth.repository.RateLimitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * RateLimitingRepositoryUsingRedis
 * 
 * Redis를 사용한 Rate Limiting Repository 구현체입니다.
 */
@Repository
@RequiredArgsConstructor
public class RateLimitingRepositoryUsingRedis implements RateLimitingRepository {
    
    private final StringRedisTemplate redisTemplate;
    
    @Override
    public Long incrementAndGet(String key, long ttlSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        
        // 첫 요청이면 TTL 설정
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        
        return count != null ? count : 0L;
    }
    
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
