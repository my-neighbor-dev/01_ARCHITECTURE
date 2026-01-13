plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    java
}

dependencies {
    implementation(project(":user:user-controller"))
    implementation(project(":user:user-repository-using-jpa"))
    implementation(project(":auth:auth-controller"))
    implementation(project(":auth:auth-repository-using-jpa"))
    implementation(project(":auth:auth-external-api-using-feign"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    
    // Feign Client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")
    
    // Swagger/OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
