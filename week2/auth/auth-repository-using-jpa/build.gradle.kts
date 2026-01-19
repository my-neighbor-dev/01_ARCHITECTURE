dependencies {
    implementation(project(":auth:auth-repository"))
    implementation(project(":auth:auth-domain"))
    
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
}
