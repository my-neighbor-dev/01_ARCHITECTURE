dependencies {
    implementation(project(":auth:auth-infrastructure"))  // DeviceInfo 추가
    
    implementation("org.springframework:spring-web")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.15")
    
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
}
