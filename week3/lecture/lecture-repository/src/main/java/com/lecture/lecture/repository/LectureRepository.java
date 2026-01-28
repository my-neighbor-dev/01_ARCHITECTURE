package com.lecture.lecture.repository;

import com.lecture.lecture.domain.Lecture;

public interface LectureRepository {
    Lecture findById(Long id);
    Lecture save(Lecture lecture);
}
