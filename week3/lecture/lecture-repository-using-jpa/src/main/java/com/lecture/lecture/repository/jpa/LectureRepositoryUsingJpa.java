package com.lecture.lecture.repository.jpa;

import com.lecture.lecture.domain.Lecture;
import com.lecture.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryUsingJpa implements LectureRepository {
    
    private final LectureJpaRepository jpaRepository;
    
    @Override
    public Lecture findById(Long id) {
        LectureEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lecture not found: " + id));
        return convertToDomain(entity);
    }
    
    @Override
    public Lecture save(Lecture lecture) {
        LectureEntity entity = convertToEntity(lecture);
        LectureEntity saved = jpaRepository.save(entity);
        return convertToDomain(saved);
    }
    
    private LectureEntity convertToEntity(Lecture lecture) {
        if (lecture.getId() == null) {
            return new LectureEntity(
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getCreatedBy()
            );
        }
        LectureEntity entity = new LectureEntity();
        entity.setId(lecture.getId());
        entity.setTitle(lecture.getTitle());
        entity.setDescription(lecture.getDescription());
        entity.setCreatedBy(lecture.getCreatedBy());
        return entity;
    }
    
    private Lecture convertToDomain(LectureEntity entity) {
        return new Lecture(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getCreatedBy()
        );
    }
}
