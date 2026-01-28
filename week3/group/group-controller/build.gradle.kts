dependencies {
    implementation(project(":group:group-api"))
    implementation(project(":group:group-orchestrator"))
    implementation(project(":authorization:authorization-annotation"))
    implementation(project(":authorization:authorization-common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}
