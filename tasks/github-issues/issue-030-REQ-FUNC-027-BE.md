# [BE-009] 스케줄 수정/삭제 API

**Epic:** EPIC_SYNC  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-02-11  
**Due Date:** 2026-02-11  
**Dependencies:** BE-007

---

## 목적 및 요약
- **목적**: 클라이언트의 변경사항을 서버에 반영한다.
- **요약**: `PUT /api/schedules/{id}`, `DELETE ...` 등을 처리한다. (001 Create 외 나머지 CUD)

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-027
- **Component**: Backend API

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Update**: 단순 필드 수정 및 `updatedAt` 갱신.
- **Delete**: Soft Delete 처리 (`deletedAt` 마킹).
- **Conflict**: (이번 단계에선 LWW: Last Write Wins 적용).

## Definition of Done (DoD)

- [ ] **Update**: PUT 요청 시 데이터가 수정되어야 한다.
- [ ] **Delete**: DELETE 요청 시 Soft Delete되어야 한다.
- [ ] **Timestamp**: `updatedAt`이 자동 갱신되어야 한다.
- [ ] **Auth**: 본인의 스케줄만 수정/삭제 가능해야 한다.

## 구현 힌트

- Update/Delete Service 로직 구현
- Soft Delete 패턴 적용
- Authorization 체크

## 테스트

- **Integration**: Update -> Get, Delete -> Get

---

**Labels:** `backend`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

