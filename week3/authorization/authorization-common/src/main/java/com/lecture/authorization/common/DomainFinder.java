package com.lecture.authorization.common;

/**
 * 도메인을 ID로 조회할 수 있는 인터페이스
 * 
 * 각 SearchService는 이 인터페이스를 구현하여 도메인을 조회합니다.
 * Aspect에서 이 인터페이스를 통해 도메인을 조회하여 소유권을 검증합니다.
 */
public interface DomainFinder<T extends ResourceOwnership> {
    /**
     * ID로 도메인을 조회합니다.
     * 
     * @param id 도메인 ID
     * @return 도메인 객체
     * @throws com.lecture.common.exception.NotFoundException 도메인이 존재하지 않을 경우
     */
    T searchById(Long id);
}
