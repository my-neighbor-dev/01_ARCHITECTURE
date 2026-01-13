dependencies {
    implementation(project(":auth:auth-api"))
    implementation(project(":auth:auth-domain"))
    implementation(project(":auth:auth-external-api"))  // AuthUserApi 인터페이스
    
    // Servlet API for Cookie
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.springframework:spring-context")
}
