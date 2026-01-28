plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.4"
}

allprojects {
    group = "com.lecture"
    version = "1.0.0"
    
    repositories {
        mavenCentral()
    }
}

configure(subprojects.filter { it.name != "lecture-service" }) {
    group = ""
    
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    
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
    }
}
