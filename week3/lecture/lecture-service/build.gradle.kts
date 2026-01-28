plugins {
    id("java")
    id("io.spring.dependency-management")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    implementation(project(":lecture:lecture-repository"))
    implementation(project(":lecture:lecture-domain"))
    implementation(project(":authorization:authorization-common"))
    
    implementation("org.springframework:spring-context")
}
