package com.lecture.lecture.repository.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lectures")
@Getter
@Setter
@NoArgsConstructor
public class LectureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, name = "created_by")
    private Long createdBy;
    
    public LectureEntity(String title, String description, Long createdBy) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
    }
}
