package com.lecture.user.repository.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import jakarta.persistence.Column;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
// @Where(clause = "deleted = false") // Use @Where for implicit filtering
// Note: @Where is deprecated in newer Hibernate versions but standard in Spring
// Boot 3.2.0 (Hibernate 6.2).
// Checking Hibernate version: Spring Boot 3.2.0 uses Hibernate 6.2.
// Alternatively can use @FilterDef and @Filter for dynamic filtering.
// For simplicity and common practice in this project (assuming based on
// standard JPA usage), @Where is fine or just relying on repository method
// names if we want explicit control.
// However, since we want findById to return null/empty for deleted users,
// @Where is convenient.
// Let's check imports.
@Where(clause = "deleted = false")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    private String password;
    private String phoneNumber;

    @Column(name = "deleted")
    private boolean deleted = false;
}
