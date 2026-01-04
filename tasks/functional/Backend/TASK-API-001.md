# TASK-API-001: 스케줄 생성 API 인터페이스 설계 (Backend)

## 1. 목적 및 요약
- **목적**: 클라이언트와 통신할 계약(Contract)을 정의한다.
- **요약**: `POST /api/schedules` 엔드포인트에 대한 Request/Response DTO를 작성하고 Validation 어노테이션을 적용한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-001 (Interface Layer)
- **Component**: Backend API (Web Layer)

## 3. Definition of Done (DoD)
- [ ] **DTO**: `CreateScheduleRequest`, `ScheduleResponse` 클래스 작성.
- [ ] **Validation**: `@NotNull`, `@Min` 등 입력값 검증 어노테이션 적용.
- **Controller**:
  - [ ] 엔드포인트 껍데기(Stub) 구현.
  - [ ] Swagger(@Operation) 문서화 어노테이션 적용.

## 4. 메타데이터 (YAML)

```yaml
task_id: "TASK-API-001"
title: "스케줄 생성 API 명세 (DTO/Controller)"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-001"]
component: ["backend", "api"]

inputs:
  fields: []

outputs:
  success: { code: "Compilable DTOs" }

dependencies: []
priority: "Must"
start_date: "2026-02-06"
due_date: "2026-02-06"
agent_profile: ["backend-developer"]
```
