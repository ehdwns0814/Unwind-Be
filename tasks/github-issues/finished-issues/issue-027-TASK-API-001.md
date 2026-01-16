# [BE-006] 스케줄 생성 API 명세 (DTO/Controller)

**Epic:** EPIC_SCHEDULE_MGMT  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-02-06  
**Due Date:** 2026-02-06  
**Dependencies:** None (병렬 실행 가능)

---

## 목적 및 요약
- **목적**: 클라이언트와 통신할 계약(Contract)을 정의한다.
- **요약**: `POST /api/schedules` 엔드포인트에 대한 Request/Response DTO를 작성하고 Validation 어노테이션을 적용한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-001 (Interface Layer)
- **Component**: Backend API (Web Layer)

## Sub-Tasks (구현 상세)

### API 설계
- **Endpoint**: `POST /api/schedules`
- **DTOs**:
  - `CreateScheduleRequest`: name, duration, date, clientId
  - `ScheduleResponse`: id, clientId, name, duration, createdAt

## Definition of Done (DoD)

- [ ] **DTO**: `CreateScheduleRequest`, `ScheduleResponse` 클래스 작성.
- [ ] **Validation**: `@NotNull`, `@Min` 등 입력값 검증 어노테이션 적용.
- **Controller**:
  - [ ] 엔드포인트 껍데기(Stub) 구현.
  - [ ] Swagger(@Operation) 문서화 어노테이션 적용.

## 구현 힌트

- DTO 클래스 작성
- ScheduleController 스텁
- Swagger 문서화

## 테스트

- **Unit**: DTO Validation Test

---

**Labels:** `backend`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

