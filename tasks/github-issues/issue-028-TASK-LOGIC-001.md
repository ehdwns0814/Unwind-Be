# [BE-007] 스케줄 생성 서비스 로직 구현

**Epic:** EPIC_SCHEDULE_MGMT  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-02-07  
**Due Date:** 2026-02-08  
**Dependencies:** BE-005, BE-006

---

## 목적 및 요약
- **목적**: 실제 비즈니스 규칙을 수행하고 데이터를 처리한다.
- **요약**: `ScheduleService`에서 중복 검사(`clientId`), 엔티티 변환, 저장 로직을 구현하고 트랜잭션을 관리한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-001 (Business Layer)
- **Component**: Backend Logic (Service Layer)

## Sub-Tasks (구현 상세)

### 비즈니스 로직
- **Idempotency**: 동일한 `clientId`로 요청이 오면 기존 데이터 반환
- **Transaction**: `@Transactional` 적용
- **Mapping**: DTO -> Entity 변환

## Definition of Done (DoD)

- [ ] **Idempotency**: 동일한 `clientId`로 요청이 오면 기존 데이터를 반환해야 한다 (에러 아님).
- **Transaction**:
  - [ ] `@Transactional` 적용.
  - [ ] 저장 실패 시 롤백 확인.
- **Mapping**: DTO -> Entity 변환 로직이 정확해야 한다.

## 구현 힌트

- ScheduleService 구현
- Mapper 또는 Builder 패턴 활용
- Exception Handling

## 테스트

- **Unit**: Service Logic Test
- **Integration**: E2E Schedule Creation Test

---

**Labels:** `backend`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

