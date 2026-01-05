# [BE-003] 인증 로직 및 보안 설정

**Epic:** EPIC_AUTH  
**Priority:** Must  
**Effort:** L  
**Start Date:** 2026-02-01  
**Due Date:** 2026-02-02  
**Dependencies:** BE-001, BE-002

---

## 목적 및 요약
- **목적**: 안전한 인증 시스템을 구축한다.
- **요약**: Spring Security 설정(FilterChain), `AuthService`(로그인/가입), JWT Provider 구현.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-024 (Business Layer)
- **Component**: Backend Logic

## Sub-Tasks (구현 상세)

### 보안 설정
- **Spring Security**: SecurityFilterChain 구성
- **JWT**: JwtProvider (토큰 생성, 검증, 파싱)
- **Service**: AuthService (회원가입, 로그인 로직)
- **Password**: BCryptPasswordEncoder 적용

## Definition of Done (DoD)

- [ ] **Security**: 패스워드는 반드시 BCrypt 등으로 해싱되어야 한다.
- [ ] **JWT**: 토큰 생성, 검증, 만료 시간 처리가 정상 동작해야 한다.
- [ ] **Service**: 중복 가입 시도 시 예외 발생 확인.
- [ ] **Filter**: JWT 인증 필터가 동작해야 한다.

## 구현 힌트

- SecurityConfig 작성
- JwtProvider 구현
- AuthService 구현
- JwtAuthenticationFilter 구현

## 테스트

- **Unit**: JWT Provider Test, AuthService Test
- **Integration**: 로그인 플로우 테스트

---

**Labels:** `backend`, `must`, `phase-1`  
**Milestone:** v1.0-MVP

