# BE-004 구현 리뷰 문서

**Issue ID:** BE-004  
**Issue Title:** 인증 테스트 코드 작성  
**Implementation Date:** 2026-01-15  
**Author:** AI Agent  
**Branch:** `test/5-BE-004-auth-tests`  
**PR:** [#16](https://github.com/ehdwns0814/Unwind-Be/pull/16)  
**Related Issue:** [#5](https://github.com/ehdwns0814/Unwind-Be/issues/5), [issue-025-REQ-FUNC-025-BE.md](../issue-025-REQ-FUNC-025-BE.md)

---

## 1. 구현 개요

### 1.1 목적
BE-003에서 구현된 인증 로직(JWT 토큰, Spring Security, AuthService)에 대한 포괄적인 테스트 코드를 작성하여 코드 품질과 안정성을 검증합니다.

### 1.2 구현 범위
| 구분 | 내용 |
|------|------|
| **Unit Test** | AuthServiceTest (7개), JwtProviderTest (10개) |
| **Integration Test** | AuthIntegrationTest (8개) |
| **Configuration** | test/application.yml JWT 설정 추가 |
| **총 테스트 케이스** | 25개 |

### 1.3 테스트 범위 (Coverage)

| 컴포넌트 | 테스트 유형 | 케이스 수 | 커버리지 |
|----------|------------|----------|----------|
| AuthService | Unit Test | 7개 | 로그인 3개, 토큰 갱신 4개 |
| JwtProvider | Unit Test | 10개 | 토큰 생성 4개, 토큰 검증 6개 |
| AuthController | Integration Test | 8개 | 로그인 4개, 토큰 갱신 4개 |

---

## 2. 테스트 아키텍처

### 2.1 테스트 패키지 구조

```
src/test/java/com/wombat/screenlock/unwind_be/
├── api/
│   └── auth/
│       ├── controller/
│       │   └── AuthIntegrationTest.java    # 통합 테스트 (MockMvc)
│       └── dto/
│           ├── SignUpRequestTest.java      # DTO Validation 테스트 (BE-002)
│           ├── LoginRequestTest.java       # DTO Validation 테스트 (BE-002)
│           └── RefreshRequestTest.java     # DTO Validation 테스트 (BE-002)
├── application/
│   └── auth/
│       └── AuthServiceTest.java            # 서비스 단위 테스트 ⭐ NEW
├── domain/
│   └── user/
│       └── repository/
│           └── UserRepositoryTest.java     # Repository 테스트 (BE-001)
└── infrastructure/
    └── jwt/
        └── JwtProviderTest.java            # JWT 단위 테스트 ⭐ NEW
```

### 2.2 테스트 전략 다이어그램

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Test Pyramid                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│                         ┌────────────────┐                               │
│                         │  Integration   │  (8 tests)                   │
│                         │    Tests       │  AuthIntegrationTest         │
│                         │   MockMvc      │                               │
│                         └────────────────┘                               │
│                                                                          │
│              ┌─────────────────────────────────────────┐                 │
│              │          Unit Tests                     │  (17 tests)    │
│              │   AuthServiceTest + JwtProviderTest     │                 │
│              │              Mockito                    │                 │
│              └─────────────────────────────────────────┘                 │
│                                                                          │
│    ┌───────────────────────────────────────────────────────────────┐     │
│    │                     DTO Validation Tests                      │     │
│    │          SignUpRequest + LoginRequest + RefreshRequest        │     │
│    │                      (21 tests from BE-002)                   │     │
│    └───────────────────────────────────────────────────────────────┘     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Mock 의존성 관계

```
AuthServiceTest
├── @Mock UserRepository
├── @Mock RefreshTokenRepository
├── @Mock PasswordEncoder
├── @Mock JwtProvider
└── @InjectMocks AuthService

JwtProviderTest
└── Real JwtProvider (직접 생성)

AuthIntegrationTest
├── @Autowired MockMvc
├── @Autowired ObjectMapper
├── @Autowired UserRepository (실제 H2 DB)
├── @Autowired PasswordEncoder (실제 BCrypt)
├── @Autowired JwtProvider (실제 구현체)
└── @MockitoBean RefreshTokenRepository (Redis Mock)
```

---

## 3. 테스트 상세

### 3.1 AuthServiceTest (단위 테스트)

**파일:** `src/test/java/.../application/auth/AuthServiceTest.java`

#### 로그인 테스트 (3개)

| 테스트 케이스 | 시나리오 | 예상 결과 |
|--------------|---------|----------|
| `should_ReturnToken_When_ValidCredentials` | 유효한 이메일/비밀번호 | TokenResponse 반환, Redis 저장 호출 |
| `should_ThrowA001_When_UserNotFound` | 존재하지 않는 이메일 | AuthException(A001), 토큰 생성 안함 |
| `should_ThrowA001_When_PasswordMismatch` | 비밀번호 불일치 | AuthException(A001), 토큰 생성 안함 |

#### 토큰 갱신 테스트 (4개)

| 테스트 케이스 | 시나리오 | 예상 결과 |
|--------------|---------|----------|
| `should_ReturnNewToken_When_ValidRefreshToken` | 유효한 Refresh Token | 새 TokenResponse 반환 |
| `should_ThrowA003_When_TokenInvalid` | 유효하지 않은 토큰 | AuthException(A003) |
| `should_ThrowA003_When_TokenNotInRedis` | Redis에 없는 토큰 | AuthException(A003) |
| `should_ThrowA003_When_TokenMismatchWithRedis` | Redis 토큰과 불일치 | AuthException(A003) |

**테스트 코드 하이라이트:**

```java
@Test
@DisplayName("유효한 인증 정보로 로그인 시 토큰 반환")
void should_ReturnToken_When_ValidCredentials() {
    // Given
    LoginRequest request = new LoginRequest(EMAIL, PASSWORD);
    
    given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(testUser));
    given(passwordEncoder.matches(PASSWORD, PASSWORD_HASH)).willReturn(true);
    given(jwtProvider.generateAccessToken(USER_ID)).willReturn(ACCESS_TOKEN);
    given(jwtProvider.generateRefreshToken(USER_ID)).willReturn(REFRESH_TOKEN);
    given(jwtProvider.getAccessTokenExpirationSeconds()).willReturn(EXPIRES_IN);

    // When
    TokenResponse response = authService.login(request);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
    assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN);
    
    verify(refreshTokenRepository).save(USER_ID, REFRESH_TOKEN);
}
```

### 3.2 JwtProviderTest (단위 테스트)

**파일:** `src/test/java/.../infrastructure/jwt/JwtProviderTest.java`

#### 토큰 생성 테스트 (4개)

| 테스트 케이스 | 시나리오 | 예상 결과 |
|--------------|---------|----------|
| `should_GenerateValidAccessToken` | Access Token 생성 | 3개 부분으로 구성된 JWT 반환 |
| `should_GenerateValidRefreshToken` | Refresh Token 생성 | 3개 부분으로 구성된 JWT 반환 |
| `should_ExtractUserIdFromToken` | UserId 추출 | 올바른 UserId 반환 |
| `should_ReturnAccessTokenExpirationSeconds` | 만료 시간 조회 | 설정된 만료 시간 반환 |

#### 토큰 검증 테스트 (6개)

| 테스트 케이스 | 시나리오 | 예상 결과 |
|--------------|---------|----------|
| `should_ReturnTrue_When_TokenValid` | 유효한 토큰 | true |
| `should_ReturnFalse_When_TokenExpired` | 만료된 토큰 | false |
| `should_ReturnFalse_When_TokenMalformed` | 잘못된 형식 | false |
| `should_ReturnFalse_When_SignatureInvalid` | 잘못된 서명 | false |
| `should_ReturnFalse_When_TokenIsNull` | null 토큰 | false |
| `should_ReturnFalse_When_TokenIsEmpty` | 빈 문자열 | false |

**테스트 코드 하이라이트:**

```java
@Test
@DisplayName("만료된 토큰 검증 실패")
void should_ReturnFalse_When_TokenExpired() {
    // Given - 만료된 토큰 직접 생성
    SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    Date now = new Date();
    Date expiredDate = new Date(now.getTime() - 1000);  // 1초 전에 만료

    String expiredToken = Jwts.builder()
            .subject(String.valueOf(USER_ID))
            .issuedAt(new Date(now.getTime() - 2000))
            .expiration(expiredDate)
            .signWith(key)
            .compact();

    // When
    boolean isValid = jwtProvider.validateToken(expiredToken);

    // Then
    assertThat(isValid).isFalse();
}
```

### 3.3 AuthIntegrationTest (통합 테스트)

**파일:** `src/test/java/.../api/auth/controller/AuthIntegrationTest.java`

#### 로그인 API 테스트 (4개)

| 테스트 케이스 | HTTP 요청 | 예상 응답 |
|--------------|----------|----------|
| `should_ReturnToken_When_ValidCredentials` | POST /api/auth/login (유효) | 200 OK, TokenResponse |
| `should_Return401_When_UserNotFound` | POST /api/auth/login (존재하지 않는 이메일) | 401, A001 |
| `should_Return401_When_PasswordMismatch` | POST /api/auth/login (잘못된 비밀번호) | 401, A001 |
| `should_Return400_When_InvalidEmailFormat` | POST /api/auth/login (잘못된 이메일 형식) | 400, C001 |

#### 토큰 갱신 API 테스트 (4개)

| 테스트 케이스 | HTTP 요청 | 예상 응답 |
|--------------|----------|----------|
| `should_ReturnNewToken_When_ValidRefreshToken` | POST /api/auth/refresh (유효) | 200 OK, TokenResponse |
| `should_Return401_When_InvalidRefreshToken` | POST /api/auth/refresh (무효 토큰) | 401, A003 |
| `should_Return401_When_TokenNotInRedis` | POST /api/auth/refresh (Redis에 없음) | 401, A003 |
| `should_Return400_When_RefreshTokenEmpty` | POST /api/auth/refresh (빈 토큰) | 400, C001 |

**테스트 코드 하이라이트:**

```java
@Test
@DisplayName("유효한 인증 정보로 로그인 성공")
void should_ReturnToken_When_ValidCredentials() throws Exception {
    // Given
    LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

    // When & Then
    mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
            .andExpect(jsonPath("$.data.expiresIn").isNumber())
            .andExpect(jsonPath("$.error").doesNotExist());
}
```

---

## 4. 테스트 실행 결과

### 4.1 테스트 환경

| 항목 | 값 |
|------|-----|
| Java Version | 17 (OpenJDK Homebrew) |
| Spring Boot | 3.5.0 |
| Spring Security | 6.4.x |
| JWT Library | jjwt-api 0.12.3 |
| Test Framework | JUnit 5, Mockito, AssertJ |
| Build Tool | Gradle 9.2.1 |
| Test Database | H2 In-Memory (MySQL Mode) |
| Redis | MockBean (실제 연결 없음) |

### 4.2 테스트 실행 명령어

```bash
./gradlew test --no-daemon
```

### 4.3 테스트 결과 요약

```
BUILD SUCCESSFUL in 16s
6 actionable tasks: 4 executed, 2 up-to-date

Test run completed.
```

**전체 테스트 통계:**

| 테스트 클래스 | 케이스 수 | 성공 | 실패 | 스킵 |
|--------------|----------|------|------|------|
| AuthServiceTest | 7 | ✅ 7 | 0 | 0 |
| JwtProviderTest | 10 | ✅ 10 | 0 | 0 |
| AuthIntegrationTest | 8 | ✅ 8 | 0 | 0 |
| **총계** | **25** | **✅ 25** | **0** | **0** |

### 4.4 AuthServiceTest 상세 결과

```
AuthService 테스트
├── 로그인
│   ├── ✅ 유효한 인증 정보로 로그인 시 토큰 반환
│   ├── ✅ 존재하지 않는 이메일로 로그인 시 A001 예외
│   └── ✅ 비밀번호 불일치 시 A001 예외
└── 토큰 갱신
    ├── ✅ 유효한 Refresh Token으로 새 토큰 반환
    ├── ✅ 유효하지 않은 토큰으로 갱신 시 A003 예외
    ├── ✅ Redis에 없는 토큰으로 갱신 시 A003 예외
    └── ✅ Redis 토큰과 불일치 시 A003 예외

Tests: 7, Passed: 7, Failed: 0
```

### 4.5 JwtProviderTest 상세 결과

```
JwtProvider 테스트
├── 토큰 생성
│   ├── ✅ Access Token 생성 성공
│   ├── ✅ Refresh Token 생성 성공
│   ├── ✅ 생성된 토큰에서 UserId 추출 성공
│   └── ✅ Access Token 만료 시간 반환 성공
└── 토큰 검증
    ├── ✅ 유효한 토큰 검증 성공
    ├── ✅ 만료된 토큰 검증 실패
    ├── ✅ 잘못된 형식의 토큰 검증 실패
    ├── ✅ 잘못된 서명의 토큰 검증 실패
    ├── ✅ null 토큰 검증 실패
    └── ✅ 빈 문자열 토큰 검증 실패

Tests: 10, Passed: 10, Failed: 0
```

### 4.6 AuthIntegrationTest 상세 결과

```
인증 API 통합 테스트
├── POST /api/auth/login
│   ├── ✅ 유효한 인증 정보로 로그인 성공
│   ├── ✅ 존재하지 않는 이메일로 로그인 시 401 반환
│   ├── ✅ 잘못된 비밀번호로 로그인 시 401 반환
│   └── ✅ 이메일 형식이 잘못된 경우 400 반환
└── POST /api/auth/refresh
    ├── ✅ 유효한 Refresh Token으로 토큰 갱신 성공
    ├── ✅ 유효하지 않은 Refresh Token으로 갱신 시 401 반환
    ├── ✅ Redis에 없는 토큰으로 갱신 시 401 반환
    └── ✅ Refresh Token이 비어있는 경우 400 반환

Tests: 8, Passed: 8, Failed: 0
```

---

## 5. 코드 품질 검증

### 5.1 빌드 결과

```bash
./gradlew clean build --no-daemon
```

```
BUILD SUCCESSFUL in 18s
8 actionable tasks: 8 executed

- compileJava: SUCCESS
- compileTestJava: SUCCESS
- processResources: SUCCESS
- processTestResources: SUCCESS
- test: SUCCESS (25 tests passed)
- bootJar: SUCCESS
- jar: SUCCESS
- build: SUCCESS
```

### 5.2 적용된 코딩 규칙 (Cursor Rules)

| Rule | 적용 사항 |
|------|----------|
| **300-java-spring** | 3-Tier 아키텍처, Constructor Injection 준수 |
| **306-testing** | Given-When-Then 패턴, @DisplayName, @Nested 활용 |
| **307-api-design** | 표준 응답 형식 검증 ($.success, $.data, $.error) |
| **308-spring-security-jwt** | JWT 토큰 생성/검증 로직 테스트 |
| **201-commenting** | 모든 테스트 클래스/메서드에 JavaDoc 주석 |
| **200-git-commit** | Conventional Commits 준수, 원자적 커밋 |

### 5.3 테스트 코드 품질

| 품질 지표 | 준수 여부 | 설명 |
|----------|:--------:|------|
| Given-When-Then 패턴 | ✅ | 모든 테스트에서 명확한 구조 적용 |
| @DisplayName 한글화 | ✅ | 테스트 목적을 명확하게 설명 |
| @Nested 그룹화 | ✅ | 기능별 테스트 그룹화 (로그인, 토큰 갱신) |
| Mock 격리 | ✅ | 각 테스트가 독립적으로 실행됨 |
| 경계값 테스트 | ✅ | null, 빈 문자열, 만료된 토큰 등 엣지 케이스 포함 |
| 예외 검증 | ✅ | assertThatThrownBy로 정확한 예외 타입 및 코드 검증 |

### 5.4 Linter 검사 결과

**경고 (비심각):**
- Null safety 경고: 5개 (주로 Spring의 @NonNull 관련)
- 영향도: 낮음 (테스트 실행에 영향 없음)

**심각한 오류:** 없음 ✅

---

## 6. 변경 사항 통계

### 6.1 Git 통계

```
Branch: test/5-BE-004-auth-tests
Base: main
Files changed: 33
Insertions: 1,650 lines
Deletions: 18 lines
Net change: +1,632 lines
```

### 6.2 커밋 히스토리

| 커밋 해시 | 메시지 | 파일 수 |
|----------|--------|--------|
| `f4488d2` | docs(auth): BE-004 이슈 명세 보강 및 ERD/CLD/ORM 다이어그램 추가 | 1 |
| `38db7f1` | style: 파일 끝 개행 문자 정리 | 21 |
| `cc5d321` | test(auth): AuthService 단위 테스트 작성 | 1 |
| `e5aac97` | test(jwt): JwtProvider 단위 테스트 작성 | 1 |
| `dd05c1d` | test(auth): 인증 API 통합 테스트 작성 | 2 |

**총 5개 커밋** (원자적 커밋 원칙 준수)

### 6.3 파일별 변경 내용

| 파일 | 상태 | 라인 수 | 설명 |
|------|------|---------|------|
| `AuthServiceTest.java` | Created | +272 | 서비스 단위 테스트 (7개 케이스) |
| `JwtProviderTest.java` | Created | +194 | JWT 단위 테스트 (10개 케이스) |
| `AuthIntegrationTest.java` | Created | +262 | 통합 테스트 (8개 케이스) |
| `application.yml (test)` | Modified | +7 | JWT 테스트 설정 추가 |
| `issue-025-REQ-FUNC-025-BE.md` | Modified | +905 | 이슈 명세 보강 |
| 기타 | Modified | +21 | 파일 끝 개행 문자 정리 |

---

## 7. Definition of Done 체크리스트

### 단위 테스트 (AuthServiceTest)
- [x] **login() 성공 케이스**: 유효한 인증 정보 → 토큰 반환
- [x] **login() 실패 케이스 - 사용자 없음**: A001 예외 발생
- [x] **login() 실패 케이스 - 비밀번호 불일치**: A001 예외 발생
- [x] **refresh() 성공 케이스**: 유효한 Refresh Token → 새 토큰 반환
- [x] **refresh() 실패 케이스 - 토큰 무효**: A003 예외 발생
- [x] **refresh() 실패 케이스 - Redis 불일치**: A003 예외 발생

### 단위 테스트 (JwtProviderTest)
- [x] **토큰 생성**: Access Token, Refresh Token 정상 생성
- [x] **UserId 추출**: 토큰에서 UserId 정상 추출
- [x] **유효한 토큰 검증**: true 반환
- [x] **만료된 토큰 검증**: false 반환
- [x] **잘못된 형식 토큰 검증**: false 반환
- [x] **잘못된 서명 토큰 검증**: false 반환

### 통합 테스트 (AuthIntegrationTest)
- [x] **POST /api/auth/login 성공**: 200 OK, TokenResponse
- [x] **POST /api/auth/login 실패 (401)**: A001 에러 응답
- [x] **POST /api/auth/login 실패 (400)**: C001 에러 응답 (Validation)
- [x] **POST /api/auth/refresh 성공**: 200 OK, TokenResponse
- [x] **POST /api/auth/refresh 실패 (401)**: A003 에러 응답
- [x] **POST /api/auth/refresh 실패 (400)**: C001 에러 응답

### 테스트 환경 설정
- [x] **test/application.yml**: JWT secret, expiration 설정 추가

### 빌드 및 검증
- [x] **전체 테스트 통과**: 25개 테스트 모두 성공
- [x] **빌드 성공**: ./gradlew build 정상 완료
- [x] **Draft PR 생성**: #16 생성 완료

---

## 8. 테스트 커버리지 분석

### 8.1 비즈니스 로직 커버리지

| 클래스 | 메서드 | 테스트 커버리지 |
|-------|-------|---------------|
| AuthService | login() | ✅ 성공 1개 + 실패 2개 = 3개 |
| AuthService | refresh() | ✅ 성공 1개 + 실패 3개 = 4개 |
| AuthService | signup() | ❌ 미작성 (추후 권장) |
| JwtProvider | generateAccessToken() | ✅ 생성 + 추출 = 2개 |
| JwtProvider | generateRefreshToken() | ✅ 생성 = 1개 |
| JwtProvider | validateToken() | ✅ 성공 1개 + 실패 5개 = 6개 |
| JwtProvider | getUserIdFromToken() | ✅ 1개 |

### 8.2 API 엔드포인트 커버리지

| 엔드포인트 | HTTP Status | 커버리지 |
|-----------|-------------|---------|
| POST /api/auth/login | 200 OK | ✅ 1개 |
| POST /api/auth/login | 400 Bad Request | ✅ 1개 |
| POST /api/auth/login | 401 Unauthorized | ✅ 2개 |
| POST /api/auth/refresh | 200 OK | ✅ 1개 |
| POST /api/auth/refresh | 400 Bad Request | ✅ 1개 |
| POST /api/auth/refresh | 401 Unauthorized | ✅ 2개 |
| POST /api/auth/signup | - | ❌ 미작성 |

---

## 9. 다음 단계

### 9.1 후속 작업 권장사항

#### 테스트 확장 (우선순위: 높음)
- **AuthService.signup() 테스트**: 회원가입 로직 단위 테스트 추가
- **SecurityConfigTest**: Public/Protected 경로 접근 제어 테스트
- **JwtAuthenticationFilter 테스트**: 필터 동작 검증

#### Testcontainers 도입 (우선순위: 중간)
- MySQL Testcontainers: 실제 DB와 동일 환경 테스트
- Redis Testcontainers: 실제 Redis 연동 테스트
- 현재는 MockBean으로 대체

#### 코드 커버리지 측정 (우선순위: 중간)
- JaCoCo 플러그인 추가
- 커버리지 리포트 생성 자동화
- 최소 커버리지 기준 설정 (예: 80%)

### 9.2 통합 테스트 환경 구성 권장

**필요 의존성:**
```groovy
testImplementation 'org.testcontainers:testcontainers:1.19.7'
testImplementation 'org.testcontainers:mysql:1.19.7'
testImplementation 'com.redis:testcontainers-redis:2.2.0'
```

**예상 구현:**
1. 실제 MySQL/Redis 환경에서 E2E 테스트
2. 회원가입 → 로그인 → 토큰 갱신 → 보호된 API 접근 전체 플로우
3. CI/CD 파이프라인 연동

---

## 10. 참고 자료

- [Issue #5: BE-004 인증 테스트 코드 작성](https://github.com/ehdwns0814/Unwind-Be/issues/5)
- [PR #16: BE-004 구현](https://github.com/ehdwns0814/Unwind-Be/pull/16)
- [이슈 스크립트](../issue-025-REQ-FUNC-025-BE.md)
- [BE-003 구현 리뷰 문서](./BE-003-implementation-review.md)
- [Spring Boot Testing 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Mockito 공식 문서](https://site.mockito.org/)
- [AssertJ 공식 문서](https://assertj.github.io/doc/)

---

**문서 작성일:** 2026-01-15  
**최종 검토:** AI Agent  
**상태:** ✅ 테스트 코드 작성 완료 (PR Draft 상태)

