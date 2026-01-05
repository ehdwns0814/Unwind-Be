# [BE-002] Auth API 명세 (DTO/Controller)

**Epic:** EPIC_AUTH  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-31  
**Due Date:** 2026-01-31  
**Dependencies:** None (병렬 실행 가능)

---

## 목적 및 요약
- **목적**: 회원가입, 로그인, 재발급에 필요한 API 스펙을 정의한다.
- **요약**: `AuthController` 스텁 구현 및 DTO(`SignUpRequest`, `LoginRequest`, `TokenResponse`) 정의.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-024 (Interface Layer)
- **Component**: Backend API

## Sub-Tasks (구현 상세)

### API 설계
- **Endpoints**:
  - `POST /api/auth/signup`
  - `POST /api/auth/login`
  - `POST /api/auth/refresh`
- **DTOs**:
  - `SignUpRequest`: 이메일 형식, 비번 길이 검증 포함
  - `TokenResponse`: accessToken, refreshToken 포함

## Definition of Done (DoD)

- [ ] **DTO**:
  - `SignUpRequest`: 이메일 형식, 비번 길이 검증 포함.
  - `TokenResponse`: accessToken, refreshToken 포함.
- **Controller**: `/api/auth/*` 경로 매핑 확인.
- [ ] **Swagger**: API 문서화 어노테이션 적용.

## 구현 힌트

- DTO 클래스 작성 (@Valid 어노테이션)
- AuthController 스텁 구현
- Swagger @Operation 적용

## 테스트

- **Unit**: DTO Validation Test

---

**Labels:** `backend`, `must`, `phase-1`  
**Milestone:** v1.0-MVP

