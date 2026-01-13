dependencies {
    implementation(project(":user:user-domain"))
    implementation(project(":user:user-repository"))
    
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
}
