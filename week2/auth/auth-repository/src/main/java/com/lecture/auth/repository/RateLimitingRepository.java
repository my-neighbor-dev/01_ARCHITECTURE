package com.lecture.auth.repository;

/**
 * RateLimitingRepository 인터페이스
 * 
 * Rate Limiting을 위한 Repository 인터페이스입니다.
 * Redis, 로컬 캐시 등 다양한 구현체로 교체 가능합니다.
 */
public interface RateLimitingRepository {
    
    /**
     * 키의 값을 증가시키고 반환합니다.
     * 키가 없으면 1로 시작합니다.
     */
    Long incrementAndGet(String key, long ttlSeconds);
    
    /**
     * 키를 삭제합니다.
     */
    void delete(String key);
}
