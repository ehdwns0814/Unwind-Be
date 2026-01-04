# REQ-FUNC-027: 스케줄 변경사항 업로드 (Backend)

## 1. 목적 및 요약
- **목적**: 클라이언트의 변경사항을 서버에 반영한다.
- **요약**: `PUT /api/schedules/{id}`, `DELETE ...` 등을 처리한다. (001 Create 외 나머지 CUD)

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-027
- **Component**: Backend API

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Update**: 단순 필드 수정 및 `updatedAt` 갱신.
- **Delete**: Soft Delete 처리 (`deletedAt` 마킹).
- **Conflict**: (이번 단계에선 LWW: Last Write Wins 적용).

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-027-BE"
title: "스케줄 수정/삭제 API"
type: "functional"
epic: "EPIC_SYNC"
req_ids: ["REQ-FUNC-027", "REQ-FUNC-004", "REQ-FUNC-005"]
component: ["backend", "api"]

inputs:
  fields: []

outputs:
  success: { http_status: 200 }

steps_hint:
  - "Update/Delete Service 로직 구현"
  - "Soft Delete 패턴 적용"

preconditions:
  - "REQ-FUNC-001-BE 완료"

postconditions: []

tests:
  integration: ["Update -> Get", "Delete -> Get"]

dependencies: ["REQ-FUNC-001-BE"]
estimated_effort: "S"
start_date: "2026-02-11"
due_date: "2026-02-11"
priority: "Must"
agent_profile: ["backend-developer"]
```
