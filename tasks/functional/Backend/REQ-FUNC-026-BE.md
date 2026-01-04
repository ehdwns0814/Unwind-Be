# REQ-FUNC-026: 앱 실행 시 스케줄 동기화 (Backend)

## 1. 목적 및 요약
- **목적**: 기기 간 데이터 일관성을 유지한다.
- **요약**: `GET /api/schedules` 요청 시 `lastSyncTime` 파라미터를 받아, 그 이후 변경된(생성/수정/삭제) 데이터를 반환한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-026 (SRS상 동기화 전체지만 여기선 조회 위주)
- **Component**: Backend API

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Select**: `updatedAt > lastSyncTime` 인 스케줄 조회.
- **Deleted**: `deletedAt`이 있는 항목도 포함하여 반환 (클라이언트가 삭제 처리할 수 있도록).

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-026-BE"
title: "스케줄 동기화 (조회 API)"
type: "functional"
epic: "EPIC_SYNC"
req_ids: ["REQ-FUNC-026"]
component: ["backend", "api"]

inputs:
  fields:
    - { name: "lastSyncTime", type: "DateTime", optional: true }

outputs:
  success: { body: "List of Schedules (Active & Deleted)" }

steps_hint:
  - "QueryDSL 또는 JPA 메서드로 수정일 기준 조회 구현"
  - "Soft Delete된 항목 포함 로직"

preconditions:
  - "REQ-FUNC-001-BE 완료"

postconditions:
  - "변경분만 정확히 내려가야 함"

tests:
  integration: ["Sync Scenario"]

dependencies: ["REQ-FUNC-001-BE"]
estimated_effort: "M"
start_date: "2026-02-09"
due_date: "2026-02-10"
priority: "Must"
agent_profile: ["backend-developer"]
```
