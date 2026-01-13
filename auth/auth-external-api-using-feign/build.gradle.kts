dependencies {
    implementation(project(":auth:auth-external-api"))  // AuthUserApi 인터페이스
    implementation(project(":auth:auth-domain"))  // AuthUser 도메인
    implementation(project(":user:user-api"))  // UserApi (Feign 호출용)
    
    // Feign Client
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")
    
    implementation("org.springframework:spring-context")
}
