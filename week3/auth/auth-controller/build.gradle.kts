dependencies {
    implementation(project(":auth:auth-api"))
    implementation(project(":auth:auth-orchestrator"))
    implementation(project(":auth:auth-infrastructure"))  // DeviceInfo 추가
    implementation(project(":auth:auth-domain"))  // AuthToken 접근용
    
    implementation("org.springframework.boot:spring-boot-starter-web")
}
