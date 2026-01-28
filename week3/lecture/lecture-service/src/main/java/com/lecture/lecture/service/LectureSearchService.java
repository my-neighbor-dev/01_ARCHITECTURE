package com.lecture.lecture.service;

import com.lecture.authorization.common.DomainFinder;
import com.lecture.lecture.domain.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 강의 조회를 위한 SearchService
 * 
 * DomainFinder를 구현하여 Aspect에서 사용됩니다.
 */
@Service
@RequiredArgsConstructor
public class LectureSearchService implements DomainFinder<Lecture> {
    
    private final LectureService lectureService;
    
    @Override
    public Lecture searchById(Long id) {
        return lectureService.findById(id);
    }
}
