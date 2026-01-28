package com.lecture.group.repository.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_group_mappings")
@Getter
@Setter
@NoArgsConstructor
public class UserGroupMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "user_id")
    private Long userId;
    
    @Column(nullable = false, name = "group_id")
    private Long groupId;
    
    public UserGroupMappingEntity(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }
}
