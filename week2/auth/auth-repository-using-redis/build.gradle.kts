dependencies {
    implementation(project(":auth:auth-repository"))
    
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.lettuce:lettuce-core")
}
