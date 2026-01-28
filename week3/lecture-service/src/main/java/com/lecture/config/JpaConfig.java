package com.lecture.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
    "com.lecture.user.repository.jpa",
    "com.lecture.auth.repository.jpa",
    "com.lecture.group.repository.jpa",
    "com.lecture.lecture.repository.jpa"
})
@EntityScan(basePackages = {
    "com.lecture.user.repository.jpa",
    "com.lecture.auth.repository.jpa",
    "com.lecture.group.repository.jpa",
    "com.lecture.lecture.repository.jpa"
})
public class JpaConfig {
}
