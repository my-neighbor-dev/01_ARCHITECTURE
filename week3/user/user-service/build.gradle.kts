dependencies {
    implementation(project(":user:user-domain"))
    implementation(project(":user:user-repository"))
    implementation(project(":authorization:authorization-common"))
    
    implementation("org.springframework:spring-context")
}
