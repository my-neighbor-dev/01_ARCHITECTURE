package com.lecture.auth.repository.local;

import com.lecture.auth.repository.RateLimitingRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimitingRepositoryUsingLocalCache
 * 
 * 로컬 캐시를 사용한 Rate Limiting Repository 구현체입니다.
 * Redis 대신 사용할 수 있는 간단한 구현체입니다.
 */
@Repository
public class RateLimitingRepositoryUsingLocalCache implements RateLimitingRepository {
    
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    @Override
    public Long incrementAndGet(String key, long ttlSeconds) {
        CacheEntry entry = cache.compute(key, (k, v) -> {
            if (v == null || v.isExpired()) {
                return new CacheEntry(1L, System.currentTimeMillis() + (ttlSeconds * 1000));
            }
            return new CacheEntry(v.count + 1, v.expiresAt);
        });
        
        return entry.count;
    }
    
    @Override
    public void delete(String key) {
        cache.remove(key);
    }

    /**
     * 캐시 엔트리
     */
    private static class CacheEntry {
        final Long count;
        final long expiresAt;
        
        CacheEntry(Long count, long expiresAt) {
            this.count = count;
            this.expiresAt = expiresAt;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
