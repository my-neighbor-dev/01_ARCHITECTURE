dependencies {
    implementation(project(":authorization:authorization-common"))
    implementation(project(":authorization:authorization-annotation"))
    implementation(project(":group:group-service"))
    implementation(project(":group:group-domain"))
    implementation(project(":auth:auth-repository"))
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-web")
}
