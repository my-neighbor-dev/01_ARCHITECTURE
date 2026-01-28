package com.lecture.group.service;

import com.lecture.authorization.common.DomainFinder;
import com.lecture.group.domain.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 그룹 조회를 위한 SearchService
 * 
 * DomainFinder를 구현하여 Aspect에서 사용됩니다.
 */
@Service
@RequiredArgsConstructor
public class GroupSearchService implements DomainFinder<Group> {
    
    private final GroupService groupService;
    
    @Override
    public Group searchById(Long id) {
        return groupService.findById(id);
    }
}
