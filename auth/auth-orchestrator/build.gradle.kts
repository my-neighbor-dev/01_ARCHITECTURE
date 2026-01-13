dependencies {
    implementation(project(":auth:auth-api"))
    implementation(project(":auth:auth-domain"))
    implementation(project(":auth:auth-service"))  // AuthService, CookieService 등 내부 Service
    
    // Servlet API for Cookie
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    
    implementation("org.springframework:spring-context")
}
