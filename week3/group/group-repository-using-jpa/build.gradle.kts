dependencies {
    implementation(project(":group:group-repository"))
    implementation(project(":group:group-domain"))
    implementation(project(":authorization:authorization-common"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
}
