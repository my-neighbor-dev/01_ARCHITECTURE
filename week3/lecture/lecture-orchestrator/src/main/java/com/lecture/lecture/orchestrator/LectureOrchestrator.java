package com.lecture.lecture.orchestrator;

import com.lecture.lecture.api.LectureResponse;
import com.lecture.lecture.domain.Lecture;
import com.lecture.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureOrchestrator {
    
    private final LectureService lectureService;
    
    public LectureResponse getLecture(Long lectureId) {
        Lecture lecture = lectureService.findById(lectureId);
        return new LectureResponse(
            lecture.getId(),
            lecture.getTitle(),
            lecture.getDescription(),
            lecture.getCreatedBy()
        );
    }
    
    public LectureResponse createLecture(String title, String description, Long createdBy) {
        Lecture lecture = lectureService.create(title, description, createdBy);
        return new LectureResponse(
            lecture.getId(),
            lecture.getTitle(),
            lecture.getDescription(),
            lecture.getCreatedBy()
        );
    }
}
