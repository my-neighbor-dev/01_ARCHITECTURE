dependencies {
    implementation(project(":lecture:lecture-repository"))
    implementation(project(":lecture:lecture-domain"))
    implementation(project(":authorization:authorization-common"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
}
