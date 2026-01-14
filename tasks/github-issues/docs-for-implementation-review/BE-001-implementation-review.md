# BE-001 구현 리뷰 문서

**Issue ID:** BE-001  
**Issue Title:** User Entity 데이터 모델링  
**Implementation Date:** 2026-01-11  
**Author:** AI Agent  
**Branch:** `feat/be-001-user-entity`  
**PR:** [#12](https://github.com/ehdwns0814/Unwind-Be/pull/12)  
**Related Issue:** [#2](https://github.com/ehdwns0814/Unwind-Be/issues/2)

---

## 1. 구현 개요

### 1.1 목적
사용자 및 인증 정보를 저장하기 위한 데이터베이스 구조를 정의하고, 3-Tier Layered Architecture의 Repository 계층을 구현합니다.

### 1.2 구현 범위
| 구분 | 내용 |
|------|------|
| Entity | User, Role (Enum) |
| Repository | UserRepository (JPA), RefreshTokenRepository (Redis) |
| Infrastructure | JPA Auditing, Redis Config, Flyway Migration |
| Test | Unit Tests (17개 케이스) |

---

## 2. 아키텍처 구조

### 2.1 패키지 구조

```
src/main/java/com/wombat/screenlock/unwind_be/
├── config/
│   ├── JpaAuditingConfig.java       # JPA Auditing 활성화
│   └── RedisConfig.java             # Redis 연결 설정
├── domain/
│   ├── common/
│   │   └── BaseTimeEntity.java      # 공통 시간 필드 (createdAt, updatedAt)
│   └── user/
│       ├── entity/
│       │   ├── User.java            # 사용자 엔티티
│       │   └── Role.java            # 권한 Enum (USER, ADMIN)
│       └── repository/
│           └── UserRepository.java  # JPA Repository
└── infrastructure/
    └── redis/
        └── RefreshTokenRepository.java  # Redis 기반 토큰 저장소
```

### 2.2 데이터 흐름

```
┌──────────────────────────────────────────────────────────────┐
│                    Repository Layer (BE-001)                 │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────────┐    ┌─────────────────────────────┐  │
│  │   UserRepository    │    │  RefreshTokenRepository     │  │
│  │   (JPA/MySQL)       │    │  (Redis)                    │  │
│  ├─────────────────────┤    ├─────────────────────────────┤  │
│  │ save(User)          │    │ save(userId, token)         │  │
│  │ findById(Long)      │    │ findByUserId(Long)          │  │
│  │ findByEmail(String) │    │ delete(userId)              │  │
│  │ existsByEmail()     │    │ exists(userId)              │  │
│  │ delete(User)        │    │ refreshTtl(userId)          │  │
│  └─────────────────────┘    └─────────────────────────────┘  │
│            │                           │                     │
│            ▼                           ▼                     │
│  ┌─────────────────────┐    ┌─────────────────────────────┐  │
│  │     MySQL 8.0       │    │        Redis                │  │
│  │   (users table)     │    │   (refresh_token:*)         │  │
│  └─────────────────────┘    └─────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

---

## 3. 구현 상세

### 3.1 Entity 설계

#### User Entity

| 필드 | 타입 | 제약조건 | 설명 |
|------|------|----------|------|
| id | Long | PK, AUTO_INCREMENT | 사용자 고유 ID |
| email | String | UNIQUE, NOT NULL, 255자 | 로그인 ID |
| passwordHash | String | NOT NULL, 60자 | BCrypt 해시 비밀번호 |
| role | Role (Enum) | NOT NULL, 기본값 USER | 사용자 권한 |
| createdAt | LocalDateTime | NOT NULL | 생성일시 (자동) |
| updatedAt | LocalDateTime | NOT NULL | 수정일시 (자동) |

#### Role Enum

| 값 | 설명 |
|-----|------|
| USER | 일반 사용자 (기본값) |
| ADMIN | 관리자 |

### 3.2 Repository 메서드

#### UserRepository (JPA)

| 메서드 | 반환 타입 | 설명 |
|--------|----------|------|
| `save(User user)` | User | 사용자 저장/수정 |
| `findById(Long id)` | Optional\<User\> | ID로 조회 |
| `findByEmail(String email)` | Optional\<User\> | 이메일로 조회 |
| `existsByEmail(String email)` | boolean | 이메일 존재 확인 |
| `delete(User user)` | void | 사용자 삭제 |

#### RefreshTokenRepository (Redis)

| 메서드 | 반환 타입 | 설명 |
|--------|----------|------|
| `save(Long userId, String token)` | void | 토큰 저장 (TTL: 7일) |
| `findByUserId(Long userId)` | Optional\<String\> | 토큰 조회 |
| `delete(Long userId)` | void | 토큰 삭제 (로그아웃) |
| `exists(Long userId)` | boolean | 토큰 존재 확인 |
| `refreshTtl(Long userId)` | boolean | TTL 갱신 |

### 3.3 DB Migration (Flyway)

**파일:** `V1__create_users_table.sql`

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 4. 테스트 실행 결과

### 4.1 테스트 환경

| 항목 | 값 |
|------|-----|
| Java Version | 17 (OpenJDK Homebrew) |
| Spring Boot | 3.4.1 |
| Test Database | H2 In-Memory (MySQL Mode) |
| Test Framework | JUnit 5, Mockito, AssertJ |
| Build Tool | Gradle 9.2.1 |

### 4.2 테스트 실행 명령어

```bash
./gradlew test --no-daemon
```

### 4.3 테스트 결과 요약

```
BUILD SUCCESSFUL in 11s
5 actionable tasks: 2 executed, 3 up-to-date

Total Tests: 17
Passed: 17 ✅
Failed: 0
Skipped: 0
```

### 4.4 UserRepositoryTest 상세 (9개 테스트)

| 테스트 케이스 | 결과 | 설명 |
|--------------|------|------|
| `should_SaveAndFindById_When_ValidUser` | ✅ PASS | 사용자 저장 및 ID 조회 |
| `should_FindByEmail_When_UserExists` | ✅ PASS | 이메일로 사용자 조회 |
| `should_ReturnEmpty_When_EmailNotExists` | ✅ PASS | 존재하지 않는 이메일 조회 시 빈 Optional |
| `should_ReturnTrue_When_EmailExists` | ✅ PASS | 이메일 존재 확인 (존재) |
| `should_ReturnFalse_When_EmailNotExists` | ✅ PASS | 이메일 존재 확인 (미존재) |
| `should_SetDefaultRole_When_RoleIsNull` | ✅ PASS | Role null시 USER 기본값 |
| `should_SetCreatedAtAndUpdatedAt_When_Save` | ✅ PASS | BaseTimeEntity 자동 설정 |
| `should_UpdatePassword_When_Called` | ✅ PASS | 비밀번호 변경 메서드 |
| `should_ChangeRole_When_Called` | ✅ PASS | 권한 변경 메서드 |

### 4.5 RefreshTokenRepositoryTest 상세 (8개 테스트)

| 테스트 케이스 | 결과 | 설명 |
|--------------|------|------|
| `should_Save_When_ValidInput` | ✅ PASS | 토큰 저장 |
| `should_FindByUserId_When_TokenExists` | ✅ PASS | 토큰 조회 (존재) |
| `should_ReturnEmpty_When_TokenNotExists` | ✅ PASS | 토큰 조회 (미존재) |
| `should_Delete_When_Called` | ✅ PASS | 토큰 삭제 |
| `should_ReturnTrue_When_TokenExists` | ✅ PASS | 존재 확인 (존재) |
| `should_ReturnFalse_When_TokenNotExists` | ✅ PASS | 존재 확인 (미존재) |
| `should_RefreshTtl_When_TokenExists` | ✅ PASS | TTL 갱신 성공 |
| `should_ReturnFalse_When_RefreshTtlFails` | ✅ PASS | TTL 갱신 실패 |

---

## 5. 코드 품질 검증

### 5.1 빌드 결과

```
BUILD SUCCESSFUL in 13s
6 actionable tasks: 6 executed

- compileJava: SUCCESS
- processResources: SUCCESS
- classes: SUCCESS
- resolveMainClassName: SUCCESS
- bootJar: SUCCESS
- jar: SUCCESS
- assemble: SUCCESS
- check: SUCCESS (all tests passed)
- build: SUCCESS
```

### 5.2 적용된 코딩 규칙 (Cursor Rules)

| Rule | 적용 사항 |
|------|----------|
| **300-java-spring** | Layered Architecture, Lombok 규칙 준수 |
| **301-gradle-groovy** | Gradle 의존성 구조 |
| **303-redis-lettuce** | Lettuce 클라이언트 사용 (Simple caching) |
| **306-testing** | @DataJpaTest, Given-When-Then 패턴 |
| **201-commenting** | 모든 클래스/메서드에 Javadoc 주석 |

### 5.3 Lombok 사용 규칙 준수

| 규칙 | 적용 |
|------|------|
| `@Data` 사용 금지 (Entity) | ✅ `@Getter`만 사용 |
| `@NoArgsConstructor(PROTECTED)` | ✅ 적용됨 |
| `@Builder` 패턴 | ✅ 생성자에 적용 |
| `@RequiredArgsConstructor` (DI) | ✅ Repository에 적용 |

---

## 6. 변경 사항 통계

### 6.1 Git 통계

```
Branch: feat/be-001-user-entity
Commit: 65fe519
Files changed: 14
Insertions: 997 lines
Deletions: 5 lines
```

### 6.2 파일별 변경 내용

| 파일 | 상태 | 라인 수 |
|------|------|---------|
| build.gradle | Modified | +61 -5 |
| JpaAuditingConfig.java | Created | +18 |
| RedisConfig.java | Created | +44 |
| BaseTimeEntity.java | Created | +43 |
| Role.java | Created | +26 |
| User.java | Created | +98 |
| UserRepository.java | Created | +42 |
| RefreshTokenRepository.java | Created | +107 |
| application.yml (main) | Created | +134 |
| V1__create_users_table.sql | Created | +27 |
| UserRepositoryTest.java | Created | +167 |
| RefreshTokenRepositoryTest.java | Created | +132 |
| application.yml (test) | Created | +36 |
| application.properties | Deleted | -1 |

---

## 7. Definition of Done 체크리스트

### 환경 설정
- [x] **MySQL 연결**: application.yml 데이터베이스 설정 완료
- [x] **Flyway 설정**: 마이그레이션 자동 실행 설정 완료
- [x] **Redis 연결**: Redis 연결 설정 완료

### Entity & Repository
- [x] **BaseTimeEntity**: @MappedSuperclass 추상 클래스 구현 완료
- [x] **JPA Auditing**: @EnableJpaAuditing 설정 완료
- [x] **Role Enum**: USER, ADMIN 권한 정의 완료
- [x] **User Entity**:
  - [x] id, email, passwordHash, role 필드 포함
  - [x] BaseTimeEntity(createdAt, updatedAt) 상속
  - [x] 비즈니스 메서드 (updatePassword, changeRole) 구현
- [x] **Migration**: Flyway 스크립트 (V1__create_users_table.sql) 작성
- [x] **UserRepository**: 인터페이스 생성 (findByEmail, existsByEmail 포함)
- [x] **RefreshTokenRepository**: Redis 기반 구현 완료

### 테스트
- [x] **Unit Test**: Entity Mapping 테스트 통과 (9개)
- [x] **Unit Test**: Repository Mock 테스트 통과 (8개)

---

## 8. 다음 단계

### 8.1 후속 작업 (BE-002)
- Auth API 명세 (DTO/Controller) 구현
- RegisterRequest, LoginRequest DTO 정의
- AuthController 엔드포인트 구현

### 8.2 통합 테스트 권장사항
- Testcontainers를 활용한 실제 MySQL/Redis 통합 테스트
- 프로덕션 환경과 동일한 조건에서 Flyway 마이그레이션 검증

---

## 9. 참고 자료

- [Issue #2: BE-001 User Entity 데이터 모델링](https://github.com/ehdwns0814/Unwind-Be/issues/2)
- [PR #12: BE-001 구현](https://github.com/ehdwns0814/Unwind-Be/pull/12)
- [ISSUE_EXECUTION_GUIDE.md](../ISSUE_EXECUTION_GUIDE.md)
- [issue-022-TASK-DB-024.md](../issue-022-TASK-DB-024.md)

---

**문서 작성일:** 2026-01-11  
**최종 검토:** AI Agent  
**상태:** ✅ 구현 완료


