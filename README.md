# 🌟 Unwind Backend API

> **집중력 향상과 목표 달성을 위한 강력한 앱 차단 솔루션의 백엔드 서버**

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat&logo=spring)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue?style=flat&logo=gradle)](https://gradle.org/)

---

## 📖 프로젝트 개요

Unwind는 사용자가 목표를 달성할 때까지 산만함을 차단하여 **진정한 집중**과 **죄책감 없는 휴식**을 제공하는 iOS 앱의 백엔드 시스템입니다.

### 🎯 핵심 목표

- **일일 집중 시간**: 1.2시간 → **2.5시간** 증가
- **스케줄 완전 달성률**: 40% → **75%** 향상
- **앱 차단 우회율**: 60% → **15%** 감소

### ✨ 주요 기능

- 🔐 **사용자 인증**: JWT 기반 회원가입/로그인 및 토큰 관리
- 📅 **스케줄 관리**: 사용자별 일정 CRUD 및 실시간 동기화
- 📊 **통계 수집**: 집중 시간, 달성률, 스트릭 추적
- 🔄 **데이터 동기화**: 여러 기기 간 seamless 데이터 동기화
- 🛡️ **보안**: Spring Security 6.x + BCrypt 암호화

---

## 🏗️ 기술 스택

### Core Runtime
- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle (Groovy DSL)

### Persistence
- **Database**: MySQL 8.0 / PostgreSQL 15
- **ORM**: Spring Data JPA (Hibernate)
- **Dynamic Query**: QueryDSL 5.x
- **Migrations**: Flyway / Liquibase

### Caching & Messaging
- **Caching**: Redis (Spring Data Redis)
- **Message Queue**: Apache Kafka (event-driven architecture, Saga pattern)

### Security
- **Authentication**: Spring Security 6.x
- **Token**: JWT (JSON Web Token)
- **Encryption**: BCrypt for password hashing

### Documentation & Testing
- **API Docs**: SpringDoc OpenAPI (Swagger UI)
- **Unit Testing**: JUnit 5, Mockito
- **Integration Testing**: Spring Boot Test, Testcontainers

### External Services
- **AI/ML Integration**: OpenAI API / Hugging Face API (RestClient/WebClient)

---

## 🚀 시작하기

### 사전 요구사항

- **Java 21** (LTS) 이상
- **Gradle 8.x**
- **MySQL 8.0** 또는 **PostgreSQL 15**
- **Redis** (선택사항, 캐싱 사용 시)
- **Docker** (선택사항, Testcontainers 사용 시)

### 설치 및 실행

1. **레포지토리 클론**

```bash
git clone https://github.com/your-org/Unwind-Be.git
cd Unwind-Be
```

2. **환경 변수 설정**

보안을 위해 모든 민감한 정보는 환경 변수로 관리합니다. 프로젝트 루트에 `.env` 파일을 생성하고 다음 변수들을 설정하세요. (가이드는 `application-local.yml.template`을 참조하세요.)

```properties
# Database
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT
JWT_SECRET_KEY=your_jwt_secret_key_at_least_32_chars

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# AI Remote Model
AI_API_KEY=your_api_key_here
AI_BASE_URL=https://api.openai.com/v1
```

> [!NOTE]
> `me.paulschwarz:spring-dotenv` 라이브러리가 추가되어 있어, 별도의 설정 없이 애플리케이션 시작 시 `.env` 파일의 값을 자동으로 읽어옵니다.

3. **빌드 및 실행**

```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# 애플리케이션 실행
./gradlew bootRun
```

4. **API 문서 확인**

서버 실행 후 다음 URL에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui.html
```

---

## 📂 프로젝트 구조

```
Unwind-Be/
├── src/
│   ├── main/
│   │   ├── java/com/wombat/screenlock/unwind_be/
│   │   │   ├── domain/          # 엔티티 및 도메인 로직
│   │   │   ├── repository/      # Spring Data JPA 리포지토리
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   ├── controller/      # REST API 컨트롤러
│   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   ├── security/        # 인증 및 보안 설정
│   │   │   ├── config/          # Spring 설정
│   │   │   └── exception/       # 전역 예외 처리
│   │   └── resources/
│   │       ├── application.yml  # 애플리케이션 설정
│   │       └── db/migration/    # Flyway 마이그레이션
│   └── test/
│       └── java/                # 단위 및 통합 테스트
├── build.gradle                 # Gradle 빌드 설정
├── settings.gradle              # Gradle 설정
├── .cursor/rules/               # 개발 규칙 및 가이드라인
├── docs/                        # 프로젝트 문서
│   ├── PRD.md                   # 제품 요구사항 문서
│   └── SRS.md                   # 소프트웨어 요구사항 명세
└── README.md                    # 이 파일
```

---

## 🔌 주요 API 엔드포인트

### 인증 (Authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 및 토큰 발급 |
| POST | `/api/auth/refresh` | 액세스 토큰 갱신 |
| POST | `/api/auth/logout` | 로그아웃 |

### 스케줄 (Schedules)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/schedules` | 스케줄 목록 조회 |
| POST | `/api/schedules` | 스케줄 생성 |
| PUT | `/api/schedules/{id}` | 스케줄 수정 |
| DELETE | `/api/schedules/{id}` | 스케줄 삭제 |

### 통계 (Statistics)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/stats/completion` | 스케줄 완료 기록 |
| GET | `/api/stats/summary` | 사용자 통계 요약 |
| POST | `/api/stats/force-quit` | 앱 강제 종료 이벤트 기록 |
| POST | `/api/stats/revocation` | 권한 해제 로그 |

---

## 🏛️ 아키텍처 원칙

### Layered Architecture

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (MySQL/PostgreSQL)
```

### 핵심 원칙

- **관심사의 분리**: Controller는 Repository를 직접 참조하지 않음
- **DTO 사용**: 모든 API 요청/응답은 DTO를 통해 처리 (Entity 직접 노출 금지)
- **의존성 주입**: Constructor Injection 사용 (`@RequiredArgsConstructor`)
- **트랜잭션 관리**: `@Transactional` 적절히 활용 (readOnly 최적화)
- **예외 처리**: `@RestControllerAdvice`를 통한 전역 예외 처리

---

## 🧪 테스팅

### 테스트 전략

- **Unit Tests**: Service/Domain 계층의 비즈니스 로직 검증 (Mockito)
- **Integration Tests**: Repository/Controller 계층의 통합 동작 검증 (Testcontainers)
- **Given-When-Then**: BDD 패턴을 통한 가독성 높은 테스트 작성

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests UserServiceTest

# 통합 테스트만 실행
./gradlew integrationTest
```

### 테스트 커버리지

```bash
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

---

### 보안 정보 관리 방침

- **환경 변수 관리**: DB 접속 정보, API 키, JWT Secret 등은 절대 코드에 포함시키지 않으며 `.env` 파일을 통해 주입받습니다.
- **Local Template**: 신규 개발자를 위해 `application-local.yml.template`을 제공합니다.
- **.gitignore**: `.env`, `application-local.yml` 등 실제 설정값은 Git 추적에서 제외됩니다.

### JWT 토큰 관리

- **Access Token**: 30분 수명 (단기)
- **Refresh Token**: 7-14일 수명 (장기, HttpOnly Cookie 또는 보안 저장소)
- **Secret Key**: 환경 변수로 관리 (절대 하드코딩 금지)

### 비밀번호 보안

- **BCrypt 해싱**: 모든 비밀번호는 BCrypt로 해싱 후 저장
- **평문 저장 금지**: 절대 평문 비밀번호를 저장하지 않음

### CORS 설정

- iOS 앱 스키마 및 개발 환경(localhost)에 대한 명시적 CORS 허용
- 프로덕션 환경에서는 특정 Origin만 허용

---

## 📊 API 응답 포맷

### 성공 응답

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "독서",
    "duration": 3600
  },
  "error": null
}
```

### 에러 응답

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "U001",
    "message": "User not found",
    "status": 404
  }
}
```

### HTTP 상태 코드

- **200 OK**: 성공적인 요청
- **201 Created**: 리소스 생성 성공
- **204 No Content**: 성공 (응답 본문 없음)
- **400 Bad Request**: 유효성 검증 실패
- **401 Unauthorized**: 인증 실패
- **403 Forbidden**: 권한 부족
- **404 Not Found**: 리소스 없음
- **500 Internal Server Error**: 서버 오류

---

## 🤝 기여 가이드

### Git Workflow

1. **이슈 생성**: GitHub Issues에서 작업할 이슈 생성
2. **브랜치 생성**: `<type>/<issue-number>-<description>` 형식
   ```bash
   git checkout -b feat/123-user-auth
   ```
3. **커밋**: Conventional Commits 규칙 준수 (한글 사용)
   ```bash
   git commit -m "feat(auth): JWT 토큰 검증 기능 구현"
   ```
4. **PR 생성**: Draft PR 생성 후 리뷰 요청
   ```bash
   gh pr create --draft --title "feat: 사용자 인증 기능 구현"
   ```

### 커밋 메시지 규칙

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `style`, `perf`

**예시**:
```
feat(auth): JWT 기반 로그인 API 구현

- 이메일/비밀번호 검증 로직 추가
- 액세스/리프레시 토큰 발급 기능 구현
- 단위 테스트 추가

Closes #123
```

### 코드 리뷰 체크리스트

- [ ] 모든 테스트 통과
- [ ] 코드 스타일 가이드 준수
- [ ] 적절한 주석 추가 (복잡한 로직 설명)
- [ ] DTO를 통한 데이터 전달 (Entity 직접 노출 금지)
- [ ] 예외 처리 적절히 구현
- [ ] API 문서(Swagger) 업데이트

---

## 📝 개발 규칙 (Cursor Rules)

이 프로젝트는 `.cursor/rules/` 디렉토리에 정의된 개발 규칙을 따릅니다:

- **002-tech-stack.mdc**: 기술 스택 정의
- **100-error-fixing-process.mdc**: 에러 수정 프로세스
- **101-build-and-env-setup.mdc**: 빌드 및 환경 설정
- **200-git-commit-push-pr.mdc**: Git 워크플로우
- **300-java-spring-cursor-rules.mdc**: Java/Spring Boot 개발 규칙
- **306-spring-boot-testing-rules.mdc**: 테스팅 규칙
- **307-api-design-exception-handling.mdc**: API 설계 및 예외 처리
- **308-spring-security-jwt-rules.mdc**: 보안 및 JWT 규칙

자세한 내용은 각 파일을 참조하세요.

---

## 📚 추가 문서

### 요구사항 및 설계
- [**PRD (Product Requirements Document)**](./docs/PRD.md): 제품 요구사항 문서
- [**SRS (Software Requirements Specification)**](./docs/SRS.md): 소프트웨어 요구사항 명세
- [**고객 여정 지도**](./docs/Unwind%20고객%20여정%20지도.md): 사용자 시나리오 분석
- [**App Store 설명**](./docs/unwind_app_store_desc.md): 앱 스토어 제출용 설명

### 작업 계획 및 실행 전략 🆕
- [**🎯 작업 실행 전략 및 의존성 DAG**](./docs/TASK_EXECUTION_DAG.md): 상세 작업 분석 및 의존성 구조
- [**⚡ 작업 실행 요약**](./docs/TASK_EXECUTION_SUMMARY.md): 빠른 참조 가이드

### API 문서
- [**API Documentation**](http://localhost:8080/swagger-ui.html): Swagger UI (서버 실행 후 접근)

---

## 🐛 문제 해결

### 빌드 실패 시

```bash
# Gradle 캐시 정리
./gradlew clean

# 의존성 재다운로드
./gradlew build --refresh-dependencies
```

### 데이터베이스 연결 오류

- `.env` 파일의 데이터베이스 설정 확인
- MySQL/PostgreSQL 서버가 실행 중인지 확인
- 방화벽 설정 확인

### JWT 관련 오류

- `JWT_SECRET` 환경 변수가 설정되어 있는지 확인 (최소 256bit)
- 토큰 만료 시간 설정 확인

---

## 📧 연락처 & 지원

- **이슈 제보**: [GitHub Issues](https://github.com/your-org/Unwind-Be/issues)
- **이메일**: support@unwind.app
- **문서**: [Wiki](https://github.com/your-org/Unwind-Be/wiki)

---

## 📄 라이선스

이 프로젝트는 [MIT License](LICENSE)를 따릅니다.

---

## 🙏 감사의 말

Unwind Backend는 **Spring Boot**, **Hibernate**, **JWT**, **Testcontainers** 등 오픈소스 커뮤니티의 훌륭한 프로젝트들을 기반으로 구축되었습니다.

---

<div align="center">

**Made with ❤️ by Wombat Screenlock Team**

⭐ 이 프로젝트가 도움이 되셨다면 Star를 눌러주세요!

</div>

