dependencies {
    implementation(project(":authorization:authorization-common"))
    implementation(project(":authorization:authorization-annotation"))
    implementation(project(":group:group-service"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
}
