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
    apply(plugin = "pmd")
    apply(plugin = "checkstyle")
    
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

    // PMD 설정 (detekt의 complexity, potential-bugs, style 규칙 대응)
    configure<PmdExtension> {
        isConsoleOutput = true
        isIgnoreFailures = false  // detekt: maxIssues: 0
        ruleSetFiles = files("${rootProject.projectDir}/config/pmd/ruleset.xml")
        ruleSets = listOf()  // 기본 룰셋 비활성화, 커스텀만 사용
    }

    // Checkstyle 설정 (detekt의 MaxLineLength, WildcardImport, MagicNumber, ForbiddenComment 대응)
    configure<CheckstyleExtension> {
        configFile = file("${rootProject.projectDir}/config/checkstyle/checkstyle.xml")
        isIgnoreFailures = false  // detekt: maxIssues: 0
    }
}

// 통합 검증 태스크: 컴파일 + 테스트 + PMD + Checkstyle 을 한 번에 실행
tasks.register("devCheck") {
    group = "verification"
    description = "Runs compile, test, PMD, and Checkstyle for all subprojects"

    dependsOn(subprojects.filter { it.name != "lecture-service" }.flatMap { subproject ->
        listOfNotNull(
            subproject.tasks.findByName("compileJava"),
            subproject.tasks.findByName("test"),
            subproject.tasks.findByName("pmdMain"),
            subproject.tasks.findByName("checkstyleMain")
        )
    })
}
