package com.lecture.lecture.controller;

import com.lecture.authorization.annotation.CheckLecturePermission;
import com.lecture.authorization.annotation.PermissionId;
import com.lecture.authorization.common.UserInfo;
import com.lecture.lecture.api.CreateLectureRequest;
import com.lecture.lecture.api.LectureApi;
import com.lecture.lecture.api.LectureResponse;
import com.lecture.lecture.orchestrator.LectureOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lecture API 구현체
 * 
 * @CheckLecturePermission Annotation이 붙은 메서드는
 * Aspect에 의해 자동으로 소유권 검증이 수행됩니다.
 */
@RestController
@RequiredArgsConstructor
public class LectureController implements LectureApi {
    
    private final LectureOrchestrator lectureOrchestrator;
    
    @Override
    @CheckLecturePermission
    public LectureResponse getLecture(@PermissionId Long lectureId) {
        return lectureOrchestrator.getLecture(lectureId);
    }
    
    @Override
    public LectureResponse createLecture(CreateLectureRequest request, UserInfo userInfo) {
        return lectureOrchestrator.createLecture(
            request.getTitle(),
            request.getDescription(),
            userInfo.getUserId()
        );
    }
}
