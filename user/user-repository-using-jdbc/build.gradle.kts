dependencies {
    implementation(project(":user:user-domain"))
    implementation(project(":user:user-repository"))
    
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.h2database:h2")
}
