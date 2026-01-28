dependencies {
    implementation(project(":lecture:lecture-api"))
    implementation(project(":lecture:lecture-orchestrator"))
    implementation(project(":authorization:authorization-annotation"))
    implementation(project(":authorization:authorization-common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}
