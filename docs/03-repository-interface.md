# 3. Repository 인터페이스화

## 질문

> **"Repository도 인터페이스로 분리하나요?"**

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

**생각해볼 점:**
- JPA에서 JDBC로 바꾸려면 어떻게 해야 할까?
- 기술 스택을 교체할 때 코드를 얼마나 수정해야 할까?

---

## 3.1 Service는 기술에 의존하면 안 된다

**Service 정의: 비즈니스 로직을 담당하는 곳**

### 문제 상황

Service에서 JPA Entity나 Feign Client를 직접 사용하면:

```java
// ❌ 잘못된 예시
@Service
public class UserService {
    @Autowired
    private UserRepositoryUsingJpa userRepository;  // ❌ JPA 구현체 직접 참조
    
    public User getUserById(Long id) {
        UserEntity entity = userRepository.findById(id);  // ❌ Entity 직접 사용
        // Service가 JPA에 의존하게 됨!
    }
}
```

**문제점**:
- Service가 JPA에 의존적이 됨
- 기술 스택을 바꾸려면 Service 코드를 수정해야 함
- Service가 비즈니스 로직에 집중할 수 없음

### 해결: Repository 인터페이스 사용

```java
// ✅ 올바른 예시
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;  // ✅ 인터페이스 참조
    
    public User getUserById(Long id) {
        User user = userRepository.findById(id);  // ✅ 도메인 모델 사용
        // Service는 기술에 의존하지 않음!
    }
}
```

**핵심 원칙**: **Service는 비즈니스 로직만 담당하고, 기술에 의존하지 않아야 합니다.**

---

## 3.2 Repository도 분리하는 이유

### 질문

> "Repository도 인터페이스로 분리하나요?"

> 💡 **스터디 전에 생각해보기**: [질문들 모음](./00-questions.md)을 먼저 확인해보세요!

### 답변

**네! Repository는 항상 인터페이스로 분리합니다.**

**이유**: 
1. **Service가 기술에 의존하지 않도록** 하기 위해서
2. JPA와 다른 기술로 유연하게 교체하기 위해서

**핵심**: Repository는 기술 스택이 바뀔 수 있으므로 항상 인터페이스로 분리합니다.

---

## 3.3 Entity는 JPA에 의존적이다

### 일반적인 오해

> "Entity를 도메인 모델로 사용하면 되지 않나요?"

### 문제: Entity는 JPA에 의존적

```java
// user-repository-using-jpa 모듈
@Entity
@Table(name = "users")
public class UserEntity {  // ❌ JPA 어노테이션에 의존
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}
```

**문제점**:
- `@Entity`, `@Table`, `@Id` 등 JPA 어노테이션에 의존
- JPA를 사용하지 않는 기술 스택(JDBC, MyBatis 등)으로 교체 불가능
- 도메인 모델이 특정 기술에 종속됨

### 해결: 도메인 모델과 Entity 분리

```java
// user-domain 모듈 (순수 Java 클래스)
public class User {  // ✅ JPA 의존성 없음
    private Long id;
    private String email;
    private String name;
    private String password;
}

// user-repository-using-jpa 모듈
@Entity
@Table(name = "users")
public class UserEntity {  // ✅ JPA 구현체에만 존재
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}

// Repository 구현체에서 변환
public User findById(Long id) {
    UserEntity entity = userJpaRepository.findById(id);
    return convertToUser(entity);  // Entity → Domain 변환
}
```

**장점**:
- 도메인 모델은 기술에 독립적
- JPA, JDBC, MyBatis 등 어떤 기술로도 교체 가능
- 도메인 모델은 비즈니스 로직에서 사용 가능

---

## 3.4 실제 교체 과정 보여주기

### Before: 구체 클래스 참조

```java
// user-service 모듈
@Service
@RequiredArgsConstructor
public class UserService {
    
    // ❌ Repository 구현체 직접 참조 - Service가 기술에 의존하게 됨
    private final UserRepositoryUsingJpa userRepository;
    
    // JPA에서 JDBC로 바꾸려면?
    // → 이 코드를 수정해야 함
}
```

**문제**: 기술 스택을 바꾸려면 Service 코드를 수정해야 함

### After: 인터페이스 참조

```java
// user-repository 모듈 (인터페이스)
public interface UserRepository {
    User findById(Long id);
    User findByEmail(String email);
}

// user-repository-using-jpa 모듈
@Repository
public class UserRepositoryUsingJpa implements UserRepository {
    // JPA 구현
}

// user-repository-using-jdbc 모듈 (교체 예시)
@Repository
public class UserRepositoryUsingJdbc implements UserRepository {
    // JDBC 구현
    // → Service 코드는 전혀 변경하지 않아도 됨!
}

// user-service 모듈
@Service
@RequiredArgsConstructor
public class UserService {
    
    // ✅ Repository 인터페이스 참조 - Service가 기술에 의존하지 않음
    private final UserRepository userRepository;
    
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // JPA에서 JDBC로 바꾸려면?
    // → build.gradle.kts에서 의존성만 변경하면 됨!
    // → Service 코드는 전혀 변경하지 않아도 됨!
}
```

**해결**: 기술 스택을 바꿔도 Service 코드는 변경하지 않아도 됨

---

## 3.5 실제 교체 과정

### Step 1: 새로운 구현체 생성

```java
// user-repository-using-jdbc 모듈
@Repository
public class UserRepositoryUsingJdbc implements UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public User findById(Long id) {
        String sql = "SELECT id, email, name, password FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, userRowMapper(), id);
    }
    
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT id, email, name, password FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, userRowMapper(), email);
    }
}
```

### Step 2: build.gradle.kts에서 의존성 변경

**Before (JPA 사용)**:
```kotlin
dependencies {
    implementation(project(":user:user-repository-using-jpa"))
}
```

**After (JDBC 사용)**:
```kotlin
dependencies {
    implementation(project(":user:user-repository-using-jdbc"))
    // user-repository-using-jpa는 제거
}
```

### Step 3: Service 코드는 변경 없음!

```java
// user-service 모듈
@Service
@RequiredArgsConstructor
public class UserService {
    
    // ✅ Service 코드는 변경 없음!
    private final UserRepository userRepository;
    
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // 모든 메서드도 변경 없음!
}
```

---

## 3.6 왜 이렇게 할까?

### 시나리오 1: 성능 최적화

- 초기: JPA 사용 (개발 속도 빠름)
- 나중: 특정 쿼리가 느려서 JDBC로 최적화
- **해결**: 해당 Repository만 JDBC 구현체로 교체

### 시나리오 2: 기술 스택 변경

- 초기: JPA 사용
- 나중: MyBatis로 변경
- **해결**: MyBatis 구현체 추가, 의존성만 변경

### 시나리오 3: 다중 데이터소스

- Primary DB: JPA 사용
- Secondary DB: JDBC 사용
- **해결**: 각각 다른 구현체 사용

---

## 3.7 모듈 구조

```
user-repository/                    ← 인터페이스
├── UserRepository.java

user-repository-using-jpa/         ← JPA 구현체
├── UserRepositoryUsingJpa.java

user-repository-using-jdbc/        ← JDBC 구현체
├── UserRepositoryUsingJdbc.java
```

**핵심**: Service는 인터페이스만 참조하여 기술에 의존하지 않음!

---

## 3.8 실무 적용 팁

### 팁 1: 구현체는 하나만 활성화

```kotlin
// user-service/build.gradle.kts
dependencies {
    // 둘 중 하나만 활성화
    implementation(project(":user:user-repository-using-jpa"))
    // implementation(project(":user:user-repository-using-jdbc"))
}
```

### 팁 2: @ConditionalOnProperty 사용

```java
@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jpa")
public class UserRepositoryUsingJpa implements UserRepository {
    // ...
}

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jdbc")
public class UserRepositoryUsingJdbc implements UserRepository {
    // ...
}
```

설정 파일에서 선택:
```yaml
repository:
  type: jpa  # 또는 jdbc
```

---

## 실습
코드에서 `UserEntity`와 `User` 도메인 모델 확인

Repository 구현체의 변환 로직 확인

실제 교체 과정 보여주기 (JPA → JDBC)

---

## 정리

✅ **Entity는 JPA에 의존적이므로 도메인 모델과 분리해야 한다**

✅ **도메인 모델은 기술에 독립적이어야 하며, Entity는 Repository 구현체에만 존재한다**

✅ **Repository도 인터페이스화하면 기술 스택을 유연하게 교체할 수 있다**

✅ **Service는 기술에 의존하지 않으므로 Repository 구현체를 교체해도 Service 코드는 변경하지 않아도 된다**
