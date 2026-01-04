# REQ-FUNC-025: 사용자 로그인 및 세션 복구 (Backend)

## 1. 목적 및 요약
- **목적**: 기존 사용자의 재진입을 처리한다.
- **요약**: `POST /api/auth/login` 처리 및 Refresh Token을 이용한 `POST /api/auth/refresh` 기능 구현.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-025
- **Component**: Backend API

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Login**: 이메일/비번 검증 -> 토큰 발급.
- **Refresh**: 만료된 AccessToken 갱신. (Redis 등에 RefreshToken 저장 권장).

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-025-BE"
title: "로그인 및 토큰 갱신(Refresh)"
type: "functional"
epic: "EPIC_AUTH"
req_ids: ["REQ-FUNC-025"]
component: ["backend", "auth"]

inputs:
  fields:
    - { name: "email", type: "String" }
    - { name: "password", type: "String" }

outputs:
  success: { body: "AccessToken, RefreshToken" }

steps_hint:
  - "Login 로직 구현"
  - "RefreshToken 저장소 (Redis/DB) 구현"
  - "TokenReissueService 구현"

preconditions:
  - "REQ-FUNC-024-BE 완료"

postconditions:
  - "유효한 RefreshToken으로 새 AccessToken을 받아야 함"

tests:
  unit: ["Login Service"]
  integration: ["Refresh Flow"]

dependencies: ["REQ-FUNC-024-BE"]
estimated_effort: "M"
start_date: "2026-02-03"
due_date: "2026-02-04"
priority: "Must"
agent_profile: ["backend-developer"]
```
