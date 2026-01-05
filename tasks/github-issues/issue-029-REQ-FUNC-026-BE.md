# [BE-008] 스케줄 동기화 (조회 API)

**Epic:** EPIC_SYNC  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-02-09  
**Due Date:** 2026-02-10  
**Dependencies:** BE-007

---

## 목적 및 요약
- **목적**: 기기 간 데이터 일관성을 유지한다.
- **요약**: `GET /api/schedules` 요청 시 `lastSyncTime` 파라미터를 받아, 그 이후 변경된(생성/수정/삭제) 데이터를 반환한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-026 (SRS상 동기화 전체지만 여기선 조회 위주)
- **Component**: Backend API

## Sub-Tasks (구현 상세)

### 동기화 로직
- **Select**: `updatedAt > lastSyncTime` 인 스케줄 조회
- **Deleted**: `deletedAt`이 있는 항목도 포함하여 반환 (클라이언트가 삭제 처리할 수 있도록)
- **Query**: QueryDSL 또는 JPA 메서드 활용

## Definition of Done (DoD)

- [ ] **Query**: `lastSyncTime` 이후 변경된 데이터만 조회되어야 한다.
- [ ] **Deleted Items**: Soft Delete된 항목도 포함되어야 한다.
- [ ] **Performance**: 인덱스를 활용하여 조회 성능이 보장되어야 한다.
- [ ] **Response**: 변경분만 정확히 내려가야 한다.

## 구현 힌트

- QueryDSL 또는 JPA 메서드로 수정일 기준 조회 구현
- Soft Delete된 항목 포함 로직
- Response DTO 설계

## 테스트

- **Integration**: Sync Scenario

---

**Labels:** `backend`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

