dependencies {
    implementation(project(":user:user-api"))
    implementation(project(":user:user-orchestrator"))

    implementation(project(":authorization:authorization-annotation"))
    implementation(project(":authorization:authorization-common"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
}
