# TASK-API-024: 인증 시스템 API 인터페이스 설계 (Backend)

## 1. 목적 및 요약
- **목적**: 회원가입, 로그인, 재발급에 필요한 API 스펙을 정의한다.
- **요약**: `AuthController` 스텁 구현 및 DTO(`SignUpRequest`, `LoginRequest`, `TokenResponse`) 정의.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-024 (Interface Layer)
- **Component**: Backend API

## 3. Definition of Done (DoD)
- [ ] **DTO**:
  - `SignUpRequest`: 이메일 형식, 비번 길이 검증 포함.
  - `TokenResponse`: accessToken, refreshToken 포함.
- **Controller**: `/api/auth/*` 경로 매핑 확인.

## 4. 메타데이터 (YAML)

```yaml
task_id: "TASK-API-024"
title: "인증 API 명세 (DTO/Controller)"
type: "functional"
epic: "EPIC_AUTH"
req_ids: ["REQ-FUNC-024"]
component: ["backend", "api"]

inputs: []
outputs:
  success: { code: "Auth DTOs" }

dependencies: []
priority: "Must"
start_date: "2026-01-31"
due_date: "2026-01-31"
agent_profile: ["backend-developer"]
```
