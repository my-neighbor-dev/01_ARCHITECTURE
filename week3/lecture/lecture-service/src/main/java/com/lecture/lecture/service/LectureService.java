package com.lecture.lecture.service;

import com.lecture.lecture.domain.Lecture;
import com.lecture.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LectureService {
    
    private final LectureRepository lectureRepository;
    
    public Lecture findById(Long id) {
        return lectureRepository.findById(id);
    }
    
    public Lecture create(String title, String description, Long createdBy) {
        Lecture lecture = new Lecture(null, title, description, createdBy);
        return lectureRepository.save(lecture);
    }
}
