# TASK-LOGIC-001: 스케줄 생성 비즈니스 로직 (Backend)

## 1. 목적 및 요약
- **목적**: 실제 비즈니스 규칙을 수행하고 데이터를 처리한다.
- **요약**: `ScheduleService`에서 중복 검사(`clientId`), 엔티티 변환, 저장 로직을 구현하고 트랜잭션을 관리한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-001 (Business Layer)
- **Component**: Backend Logic (Service Layer)

## 3. Definition of Done (DoD)
- [ ] **Idempotency**: 동일한 `clientId`로 요청이 오면 기존 데이터를 반환해야 한다 (에러 아님).
- **Transaction**:
  - [ ] `@Transactional` 적용.
  - [ ] 저장 실패 시 롤백 확인.
- **Mapping**: DTO -> Entity 변환 로직이 정확해야 한다.

## 4. 메타데이터 (YAML)

```yaml
task_id: "TASK-LOGIC-001"
title: "스케줄 생성 서비스 로직 구현"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-001"]
component: ["backend", "logic"]

inputs:
  fields: []

outputs:
  success: { code: "Service Logic Implemented" }

dependencies: ["TASK-DB-001", "TASK-API-001"]
priority: "Must"
start_date: "2026-02-07"
due_date: "2026-02-08"
agent_profile: ["backend-developer"]
```
