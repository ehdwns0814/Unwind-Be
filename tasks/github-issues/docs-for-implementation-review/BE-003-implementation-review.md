# BE-003 구현 리뷰 문서

**Issue ID:** BE-003  
**Issue Title:** 인증 로직 및 보안 설정  
**Implementation Date:** 2026-01-14  
**Author:** AI Agent  
**Branch:** `feat/BE-003-auth-logic-security`  
**PR:** [#14](https://github.com/ehdwns0814/Unwind-Be/pull/14)  
**Related Issue:** [issue-024-TASK-LOGIC-024.md](../issue-024-TASK-LOGIC-024.md)

---

## 1. 구현 개요

### 1.1 목적
안전한 JWT 기반 인증 시스템을 구축하고, Spring Security를 통한 보안 설정을 구현합니다. 3-Tier Layered Architecture의 Service 계층과 Infrastructure 계층을 완성합니다.

### 1.2 구현 범위
| 구분 | 내용 |
|------|------|
| **Infrastructure** | JwtProvider, JwtAuthenticationFilter |
| **Config** | SecurityConfig (FilterChain, PasswordEncoder) |
| **Service** | AuthService (회원가입, 로그인, 토큰 갱신) |
| **Exception** | BusinessException, AuthException, GlobalExceptionHandler |
| **Controller** | AuthController 서비스 연결 |
| **Dependencies** | Spring Security, JWT 라이브러리 (jjwt 0.12.3) |

### 1.3 구현 순서 (3-Tier 아키텍처 기반)
> **데이터(Repository) → 로직(Service) → 인터페이스(Controller)** 순서로 구현

| 순서 | 레이어 | 구현 컴포넌트 | 상태 |
|:---:|-------|------------|:---:|
| 1️⃣ | **Data Layer** | UserRepository, RefreshTokenRepository | ✅ BE-001 완료 |
| 2️⃣ | **Infrastructure** | JwtProvider, JwtAuthenticationFilter | ✅ 완료 |
| 3️⃣ | **Business Layer** | AuthService | ✅ 완료 |
| 4️⃣ | **Config** | SecurityConfig | ✅ 완료 |
| 5️⃣ | **Presentation** | AuthController 연결 | ✅ 완료 |
| 6️⃣ | **Exception** | GlobalExceptionHandler | ✅ 완료 |

---

## 2. 아키텍처 구조

### 2.1 패키지 구조

```
src/main/java/com/wombat/screenlock/unwind_be/
├── application/
│   └── auth/
│       └── AuthService.java                    # 인증 비즈니스 로직
├── config/
│   └── SecurityConfig.java                     # Spring Security 설정
├── global/
│   ├── exception/
│   │   ├── BusinessException.java              # 비즈니스 예외 기본 클래스
│   │   ├── AuthException.java                  # 인증 예외
│   │   └── ErrorCode.java                      # 에러 코드 (확장)
│   └── handler/
│       └── GlobalExceptionHandler.java          # 전역 예외 핸들러
├── infrastructure/
│   └── jwt/
│       ├── JwtProvider.java                    # JWT 토큰 생성/검증
│       └── JwtAuthenticationFilter.java        # JWT 인증 필터
└── api/
    └── auth/
        └── controller/
            └── AuthController.java              # 서비스 연결 완료
```

### 2.2 데이터 흐름 다이어그램

#### 회원가입 흐름
```
Client → AuthController → AuthService
  ↓
  ├─ UserRepository.existsByEmail() → MySQL
  ├─ PasswordEncoder.encode() → BCrypt 해시
  ├─ UserRepository.save() → MySQL
  ├─ JwtProvider.generateAccessToken() → JWT 생성
  ├─ JwtProvider.generateRefreshToken() → JWT 생성
  └─ RefreshTokenRepository.save() → Redis
  ↓
TokenResponse → Client
```

#### 로그인 흐름
```
Client → AuthController → AuthService
  ↓
  ├─ UserRepository.findByEmail() → MySQL
  ├─ PasswordEncoder.matches() → BCrypt 검증
  ├─ JwtProvider.generateAccessToken() → JWT 생성
  ├─ JwtProvider.generateRefreshToken() → JWT 생성
  └─ RefreshTokenRepository.save() → Redis
  ↓
TokenResponse → Client
```

#### 토큰 갱신 흐름
```
Client → AuthController → AuthService
  ↓
  ├─ JwtProvider.validateToken() → 토큰 검증
  ├─ JwtProvider.getUserIdFromToken() → userId 추출
  ├─ RefreshTokenRepository.findByUserId() → Redis 조회
  ├─ 토큰 비교 (Redis 저장값 vs 요청값)
  ├─ JwtProvider.generateAccessToken() → 새 JWT 생성
  ├─ JwtProvider.generateRefreshToken() → 새 JWT 생성
  └─ RefreshTokenRepository.save() → Redis 갱신
  ↓
TokenResponse → Client
```

### 2.3 보안 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│              Spring Security Filter Chain              │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. JwtAuthenticationFilter                            │
│     └─ Authorization 헤더에서 Bearer 토큰 추출         │
│     └─ JwtProvider로 토큰 검증                         │
│     └─ SecurityContext에 Authentication 설정           │
│                                                         │
│  2. SecurityConfig                                     │
│     ├─ Public: /api/auth/**, /api/docs/**,            │
│     │         /swagger-ui/**                           │
│     └─ Protected: 그 외 모든 /api/** 경로              │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 3. 구현 상세

### 3.1 JWT Infrastructure

#### JwtProvider

| 메서드 | 반환 타입 | 설명 |
|--------|----------|------|
| `generateAccessToken(Long userId)` | String | Access Token 생성 (30분 만료) |
| `generateRefreshToken(Long userId)` | String | Refresh Token 생성 (7일 만료) |
| `validateToken(String token)` | boolean | 토큰 유효성 검증 (서명, 만료, 형식) |
| `getUserIdFromToken(String token)` | Long | 토큰에서 userId 추출 |
| `getAccessTokenExpirationSeconds()` | long | Access Token 만료 시간 (초) |

**주요 특징:**
- HS256 알고리즘 사용
- Secret Key는 환경변수(`JWT_SECRET_KEY`)에서 로드
- 만료된 토큰, 잘못된 형식, 유효하지 않은 서명 등 예외 처리

#### JwtAuthenticationFilter

**동작 흐름:**
1. HTTP 요청의 `Authorization: Bearer {token}` 헤더에서 토큰 추출
2. `JwtProvider.validateToken()`으로 검증
3. 유효하면 `SecurityContextHolder`에 `Authentication` 설정
4. 무효하면 무시하고 다음 필터로 진행 (인증 실패로 처리되지 않음)

**위치:** `UsernamePasswordAuthenticationFilter` 이전에 실행

### 3.2 Security 설정

#### SecurityConfig

| 설정 항목 | 값 | 설명 |
|----------|-----|------|
| CSRF | 비활성화 | Stateless API이므로 불필요 |
| Session | STATELESS | 세션 사용 안함 |
| Public 경로 | `/api/auth/**`, `/api/docs/**`, `/swagger-ui/**`, `/v3/api-docs/**` | 인증 없이 접근 가능 |
| Protected 경로 | 그 외 모든 `/api/**` | JWT 인증 필요 |
| PasswordEncoder | BCryptPasswordEncoder | 비밀번호 해싱 |

### 3.3 비즈니스 로직 (AuthService)

#### signup() - 회원가입

**로직 순서:**
1. 이메일 중복 체크 (`UserRepository.existsByEmail()`)
2. 비밀번호 BCrypt 해시 (`PasswordEncoder.encode()`)
3. User 엔티티 저장 (`UserRepository.save()`)
4. Access/Refresh Token 발급 (`JwtProvider`)
5. Refresh Token Redis 저장 (`RefreshTokenRepository.save()`)

**예외:**
- `AuthException(EMAIL_ALREADY_EXISTS)` - 이메일 중복 시

#### login() - 로그인

**로직 순서:**
1. 이메일로 사용자 조회 (`UserRepository.findByEmail()`)
2. 비밀번호 BCrypt 검증 (`PasswordEncoder.matches()`)
3. Access/Refresh Token 발급 (`JwtProvider`)
4. Refresh Token Redis 저장 (`RefreshTokenRepository.save()`)

**예외:**
- `AuthException(INVALID_CREDENTIALS)` - 사용자 없음 또는 비밀번호 불일치 시

#### refresh() - 토큰 갱신

**로직 순서:**
1. Refresh Token 유효성 검증 (`JwtProvider.validateToken()`)
2. Token에서 UserId 추출 (`JwtProvider.getUserIdFromToken()`)
3. Redis 저장 토큰과 비교 (`RefreshTokenRepository.findByUserId()`)
4. 새 Access/Refresh Token 발급 (`JwtProvider`)
5. 새 Refresh Token Redis 저장 (`RefreshTokenRepository.save()`)

**예외:**
- `AuthException(INVALID_REFRESH_TOKEN)` - 토큰 무효, 만료, 또는 Redis 불일치 시

### 3.4 예외 처리

#### BusinessException
- 비즈니스 예외 기본 클래스
- `ErrorCode`를 포함하여 표준화된 에러 응답 제공

#### AuthException
- `BusinessException`을 상속
- 인증 관련 예외 (A001, A002, A003)

#### GlobalExceptionHandler
- `@RestControllerAdvice`로 전역 예외 처리
- `BusinessException` → 정의된 HTTP Status
- `MethodArgumentNotValidException` → 400 Bad Request
- `Exception` → 500 Internal Server Error

**로깅 전략:**
- 4xx 예외: `log.warn()` (메시지만)
- 5xx 예외: `log.error()` (전체 스택트레이스)

### 3.5 ErrorCode 확장

| 에러 코드 | HTTP Status | 코드 | 메시지 |
|----------|------------|------|--------|
| INTERNAL_SERVER_ERROR | 500 | S001 | 서버 내부 오류가 발생했습니다 |

---

## 4. 테스트 실행 결과

### 4.1 테스트 환경

| 항목 | 값 |
|------|-----|
| Java Version | 17 (OpenJDK Homebrew) |
| Spring Boot | 3.4.1 |
| Spring Security | 6.4.1 |
| JWT Library | jjwt-api 0.12.3 |
| Build Tool | Gradle 9.2.1 |

### 4.2 빌드 검증

#### 컴파일 검증

```bash
./gradlew clean compileJava --no-daemon
```

**결과:**
```
BUILD SUCCESSFUL in 6s
2 actionable tasks: 2 executed
```

✅ **컴파일 성공** - 모든 Java 파일이 정상적으로 컴파일됨

#### 전체 빌드 검증

```bash
./gradlew clean build -x test --no-daemon
```

**결과:**
```
BUILD SUCCESSFUL in 7s
6 actionable tasks: 6 executed

- compileJava: SUCCESS
- processResources: SUCCESS
- classes: SUCCESS
- resolveMainClassName: SUCCESS
- bootJar: SUCCESS
- jar: SUCCESS
- assemble: SUCCESS
- check: SUCCESS
- build: SUCCESS
```

✅ **빌드 성공** - 모든 빌드 단계 통과

### 4.3 코드 품질 검증

#### Linter 검사 결과

**경고 (비심각):**
- Null safety 경고: 10개 (주로 Redis Template 및 Filter 파라미터)
- 영향도: 낮음 (런타임 동작에 영향 없음)
- 개선 권장: 추후 `@NonNull` 어노테이션 추가

**심각한 오류:** 없음 ✅

### 4.4 기능 검증 (수동 테스트 시나리오)

#### 시나리오 1: 회원가입 성공
```
POST /api/auth/signup
{
  "email": "test@example.com",
  "password": "password123"
}

예상 응답:
- HTTP 201 Created
- TokenResponse (accessToken, refreshToken, expiresIn)
- MySQL users 테이블에 사용자 저장됨
- Redis에 refresh_token:{userId} 저장됨
```

#### 시나리오 2: 회원가입 실패 (이메일 중복)
```
POST /api/auth/signup
{
  "email": "test@example.com",  # 이미 존재하는 이메일
  "password": "password123"
}

예상 응답:
- HTTP 409 Conflict
- ErrorResponse { code: "A002", message: "이미 사용 중인 이메일입니다" }
```

#### 시나리오 3: 로그인 성공
```
POST /api/auth/login
{
  "email": "test@example.com",
  "password": "password123"
}

예상 응답:
- HTTP 200 OK
- TokenResponse (accessToken, refreshToken, expiresIn)
- Redis에 refresh_token:{userId} 갱신됨
```

#### 시나리오 4: 로그인 실패 (잘못된 비밀번호)
```
POST /api/auth/login
{
  "email": "test@example.com",
  "password": "wrongpassword"
}

예상 응답:
- HTTP 401 Unauthorized
- ErrorResponse { code: "A001", message: "이메일 또는 비밀번호가 일치하지 않습니다" }
```

#### 시나리오 5: 토큰 갱신 성공
```
POST /api/auth/refresh
{
  "refreshToken": "valid_refresh_token"
}

예상 응답:
- HTTP 200 OK
- TokenResponse (새로운 accessToken, 새로운 refreshToken, expiresIn)
- Redis에 새 refresh_token 저장됨
```

#### 시나리오 6: 토큰 갱신 실패 (만료된 토큰)
```
POST /api/auth/refresh
{
  "refreshToken": "expired_token"
}

예상 응답:
- HTTP 401 Unauthorized
- ErrorResponse { code: "A003", message: "유효하지 않거나 만료된 토큰입니다" }
```

#### 시나리오 7: Protected 엔드포인트 접근 (JWT 없음)
```
GET /api/protected-endpoint
(Authorization 헤더 없음)

예상 응답:
- HTTP 401 Unauthorized
- Spring Security 기본 응답
```

#### 시나리오 8: Protected 엔드포인트 접근 (유효한 JWT)
```
GET /api/protected-endpoint
Authorization: Bearer {valid_access_token}

예상 응답:
- HTTP 200 OK (또는 해당 엔드포인트의 정상 응답)
- SecurityContext에 userId가 설정됨
```

### 4.5 통합 테스트 권장사항

**현재 상태:** 단위 테스트 미구현 (추후 구현 권장)

**권장 테스트:**
1. **JwtProviderTest** (Unit Test)
   - 토큰 생성 및 검증 로직 테스트
   - 만료된 토큰, 잘못된 형식 등 엣지 케이스

2. **AuthServiceTest** (Unit Test with Mock)
   - 회원가입/로그인/토큰 갱신 로직 테스트
   - 예외 케이스 테스트 (A001, A002, A003)

3. **AuthIntegrationTest** (Integration Test with MockMvc)
   - 실제 HTTP 요청/응답 테스트
   - Spring Security 필터 체인 검증

4. **SecurityConfigTest** (Integration Test)
   - Public/Protected 경로 접근 테스트
   - JWT 필터 동작 검증

---

## 5. 코드 품질 검증

### 5.1 적용된 코딩 규칙 (Cursor Rules)

| Rule | 적용 사항 |
|------|----------|
| **300-java-spring** | 3-Tier 아키텍처, Constructor Injection, `@Transactional(readOnly=true)` |
| **307-api-design-exception-handling** | `@RestControllerAdvice`, `BusinessException`, `ErrorCode` Enum, 표준 응답 형식 |
| **308-spring-security-jwt** | Stateless 인증, BCrypt, Public/Protected 분리, 환경변수 Secret |
| **310-logging-observability** | SLF4J, 4xx/5xx 로깅 레벨 구분, 민감정보 마스킹 |
| **201-code-commenting** | 모든 클래스/메서드에 Javadoc 주석 |

### 5.2 아키텍처 준수

| 원칙 | 준수 여부 | 설명 |
|------|:--------:|------|
| Layered Architecture | ✅ | Controller → Service → Repository 계층 분리 |
| Dependency Injection | ✅ | Constructor Injection (`@RequiredArgsConstructor`) |
| Transaction Management | ✅ | `@Transactional(readOnly=true)` 클래스 레벨, 데이터 변경 메서드만 `@Transactional` |
| DTO 사용 | ✅ | Entity를 Controller에서 직접 반환하지 않음 |
| Exception Handling | ✅ | 표준화된 `ApiResponse` 형식으로 예외 응답 |

### 5.3 보안 규칙 준수

| 규칙 | 준수 여부 | 설명 |
|------|:--------:|------|
| 비밀번호 평문 저장 금지 | ✅ | BCryptPasswordEncoder 사용 |
| Secret Key 하드코딩 금지 | ✅ | 환경변수(`JWT_SECRET_KEY`)에서 로드 |
| Stateless 인증 | ✅ | `SessionCreationPolicy.STATELESS` |
| JWT 검증 | ✅ | 서명, 만료, 형식 검증 |
| 민감정보 로깅 금지 | ✅ | 토큰 전체, 비밀번호는 로깅하지 않음 |

---

## 6. 변경 사항 통계

### 6.1 Git 통계

```
Branch: feat/BE-003-auth-logic-security
Base: main
Files changed: 12
Insertions: 2,100 lines
Deletions: 42 lines
Net change: +2,058 lines
```

### 6.2 파일별 변경 내용

| 파일 | 상태 | 라인 수 | 설명 |
|------|------|---------|------|
| `build.gradle` | Modified | +7 | Spring Security, JWT 의존성 추가 |
| `application.yml` | Modified | +6 | JWT 설정 추가 |
| `JwtProvider.java` | Created | +178 | JWT 토큰 생성/검증 |
| `JwtAuthenticationFilter.java` | Created | +111 | JWT 인증 필터 |
| `SecurityConfig.java` | Created | +96 | Spring Security 설정 |
| `AuthService.java` | Created | +194 | 인증 비즈니스 로직 |
| `BusinessException.java` | Created | +35 | 비즈니스 예외 기본 클래스 |
| `AuthException.java` | Created | +31 | 인증 예외 |
| `GlobalExceptionHandler.java` | Created | +98 | 전역 예외 핸들러 |
| `ErrorCode.java` | Modified | +5 | INTERNAL_SERVER_ERROR 추가 |
| `AuthController.java` | Modified | +9 -18 | AuthService 연결 |
| `issue-024-TASK-LOGIC-024.md` | Modified | +1,330 -24 | ERD/CLD/ORM 문서 추가 |

### 6.3 커밋 히스토리

| 커밋 해시 | 메시지 | 파일 수 |
|----------|--------|--------|
| `9d19d48` | chore(auth): Spring Security 및 JWT 의존성 추가 | 2 |
| `9fadfd3` | feat(auth): JWT 토큰 생성 및 검증 Provider 구현 | 2 |
| `0baf334` | feat(exception): 비즈니스 예외 및 인증 예외 클래스 구현 | 3 |
| `94332d1` | feat(exception): 전역 예외 핸들러 구현 | 1 |
| `d0050be` | feat(security): Spring Security 설정 구현 | 1 |
| `84c0500` | feat(auth): 인증 서비스 구현 | 1 |
| `ee5e224` | feat(auth): AuthController에 AuthService 연결 | 1 |
| `3d2ac51` | docs(auth): 인증 로직 이슈 문서에 ERD/CLD/ORM 섹션 추가 | 1 |

**총 8개 커밋** (원자적 커밋 원칙 준수)

---

## 7. Definition of Done 체크리스트

### 의존성 및 설정
- [x] **Spring Security 의존성 추가**: `build.gradle`에 추가 완료
- [x] **JWT 라이브러리 추가**: `jjwt-api 0.12.3` 추가 완료
- [x] **JWT 설정 추가**: `application.yml`에 secret, expiration 설정 완료

### Infrastructure
- [x] **JwtProvider 구현**:
  - [x] Access Token 생성 (30분 만료)
  - [x] Refresh Token 생성 (7일 만료)
  - [x] Token 검증 (서명, 만료, 형식)
  - [x] UserId 추출
  - [x] Secret Key 환경변수 로드
- [x] **JwtAuthenticationFilter 구현**:
  - [x] Authorization 헤더에서 Bearer 토큰 추출
  - [x] SecurityContext에 Authentication 설정
  - [x] 유효하지 않은 토큰 시 무시 (다음 필터 진행)

### Security 설정
- [x] **SecurityConfig 구현**:
  - [x] CSRF 비활성화
  - [x] SessionCreationPolicy.STATELESS 설정
  - [x] `/api/auth/**` permitAll() 설정
  - [x] 그 외 경로 authenticated() 설정
  - [x] BCryptPasswordEncoder Bean 등록

### 비즈니스 로직
- [x] **AuthService.signup() 구현**:
  - [x] 이메일 중복 체크 → A002 예외
  - [x] BCrypt 해시 후 User 저장
  - [x] Token 발급 및 Redis 저장
- [x] **AuthService.login() 구현**:
  - [x] 이메일로 User 조회 → 없으면 A001 예외
  - [x] BCrypt 비밀번호 검증 → 불일치 시 A001 예외
  - [x] Token 발급 및 Redis 저장
- [x] **AuthService.refresh() 구현**:
  - [x] Token 유효성 검증 → 실패 시 A003 예외
  - [x] Redis 저장 토큰과 비교 → 불일치 시 A003 예외
  - [x] 새 Token 발급 및 Redis 갱신

### 예외 처리
- [x] **BusinessException, AuthException 구현**
- [x] **GlobalExceptionHandler 구현**:
  - [x] BusinessException → 적절한 HTTP Status
  - [x] MethodArgumentNotValidException → 400
  - [x] Exception → 500
- [x] **ErrorCode 확장** (INTERNAL_SERVER_ERROR 추가)

### Controller 연결
- [x] **AuthController에 AuthService 주입**
- [x] **스텁 메서드 → 실제 로직 연결**

### 빌드 및 검증
- [x] **컴파일 성공**: 모든 Java 파일 정상 컴파일
- [x] **빌드 성공**: 전체 빌드 프로세스 통과
- [ ] **단위 테스트**: 추후 구현 권장
- [ ] **통합 테스트**: 추후 구현 권장

---

## 8. 다음 단계

### 8.1 후속 작업 권장사항

#### 테스트 코드 작성 (우선순위: 높음)
- **JwtProviderTest**: 토큰 생성/검증 로직 단위 테스트
- **AuthServiceTest**: 회원가입/로그인/토큰 갱신 로직 단위 테스트 (Mock 사용)
- **AuthIntegrationTest**: MockMvc를 활용한 통합 테스트
- **SecurityConfigTest**: Public/Protected 경로 접근 테스트

#### 코드 품질 개선 (우선순위: 중간)
- Null safety 경고 해결 (`@NonNull` 어노테이션 추가)
- 로깅 레벨 최적화 (DEBUG 레벨 세분화)

#### 기능 확장 (우선순위: 낮음)
- 로그아웃 기능 구현 (Refresh Token 삭제)
- 토큰 블랙리스트 기능 (보안 강화)
- 다중 기기 로그인 관리

### 8.2 통합 테스트 환경 구성

**필요 사항:**
- MySQL Testcontainers 설정
- Redis Testcontainers 설정
- Spring Security Test 의존성 활용

**예상 테스트 시나리오:**
1. 회원가입 → 로그인 → 토큰 갱신 전체 플로우
2. 예외 케이스 (이메일 중복, 잘못된 비밀번호 등)
3. JWT 필터 동작 검증
4. Protected 엔드포인트 접근 제어 검증

---

## 9. 참고 자료

- [Issue: BE-003 인증 로직 및 보안 설정](../issue-024-TASK-LOGIC-024.md)
- [PR #14: BE-003 구현](https://github.com/ehdwns0814/Unwind-Be/pull/14)
- [BE-001 구현 리뷰 문서](./BE-001-implementation-review.md)
- [Spring Security 공식 문서](https://docs.spring.io/spring-security/reference/index.html)
- [JJWT 라이브러리 문서](https://github.com/jwtk/jjwt)

---

**문서 작성일:** 2026-01-14  
**최종 검토:** AI Agent  
**상태:** ✅ 구현 완료 (테스트 코드 추후 구현 권장)

