dependencies {
    implementation(project(":authorization:authorization-common"))
    // 순환 참조를 피하기 위해 compileOnly로 선언
    compileOnly(project(":lecture:lecture-service"))
    compileOnly(project(":group:group-service"))
    compileOnly(project(":user:user-service"))
}
