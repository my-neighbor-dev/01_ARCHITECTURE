dependencies {
    implementation(project(":auth:auth-domain"))
    implementation(project(":auth:auth-repository"))
    implementation(project(":auth:auth-infrastructure"))
    
    // Servlet API for Cookie
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    
    implementation("org.springframework:spring-context")
}
