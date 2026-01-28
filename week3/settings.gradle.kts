rootProject.name = "lecture-service-root"

include("lecture-service")

// User 모듈
include("user:user-domain")
include("user:user-api")
include("user:user-controller")
include("user:user-orchestrator")
include("user:user-service")
include("user:user-repository")
include("user:user-repository-using-jpa")

// Auth 모듈
include("auth:auth-domain")
include("auth:auth-api")
include("auth:auth-controller")
include("auth:auth-orchestrator")
include("auth:auth-service")
include("auth:auth-repository")
include("auth:auth-repository-using-jpa")
include("auth:auth-external-api")
include("auth:auth-external-api-using-feign")

// Auth Infrastructure (week2)
include("auth:auth-infrastructure")

// Auth Repository 구현체 (week2)
include("auth:auth-repository-using-redis")
include("auth:auth-repository-using-local-cache")

// Authorization 모듈 (week3)
include("authorization:authorization-common")
include("authorization:authorization-annotation")
include("authorization:authorization-aspect")

// Lecture 모듈 (week3)
include("lecture:lecture-domain")
include("lecture:lecture-api")
include("lecture:lecture-repository")
include("lecture:lecture-repository-using-jpa")
include("lecture:lecture-service")
include("lecture:lecture-orchestrator")
include("lecture:lecture-controller")

// Group 모듈 (week3)
include("group:group-domain")
include("group:group-api")
include("group:group-repository")
include("group:group-repository-using-jpa")
include("group:group-service")
include("group:group-orchestrator")
include("group:group-controller")
