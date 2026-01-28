plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    java
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")  // Redis 추가
    implementation("org.springframework.boot:spring-boot-starter-aop")  // AOP 추가 (week3)
    
    // H2 Database
    runtimeOnly("com.h2database:h2")
    
    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // 프로젝트 모듈
    implementation(project(":auth:auth-controller"))
    implementation(project(":auth:auth-repository-using-jpa"))
    implementation(project(":auth:auth-repository-using-redis"))  // Rate Limiting Redis 구현체
    implementation(project(":auth:auth-external-api-using-feign"))
    implementation(project(":auth:auth-infrastructure"))  // Infrastructure 추가
    
    // User 모듈
    implementation(project(":user:user-controller"))
    implementation(project(":user:user-repository-using-jpa"))

    // Authorization 모듈 (week3)
    implementation(project(":authorization:authorization-aspect"))
    
    // Lecture 모듈 (week3)
    implementation(project(":lecture:lecture-controller"))
    implementation(project(":lecture:lecture-repository-using-jpa"))
    
    // Group 모듈 (week3)
    implementation(project(":group:group-controller"))
    implementation(project(":group:group-repository-using-jpa"))

    // Feign Client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")
}
