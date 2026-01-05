# [iOS-019] 스케줄 삭제 기능 (로컬)

**Epic:** EPIC_SCHEDULE_MGMT  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-23  
**Due Date:** 2026-01-23  
**Dependencies:** iOS-001

---

## 목적 및 요약
- **목적**: 불필요한 스케줄을 제거한다.
- **요약**: Context Menu 또는 스와이프 액션을 통해 삭제 요청을 하고, 사용자 확인 후 로컬 저장소에서 제거(Soft Delete 또는 Hard Delete)한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-005
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **UI**: 삭제 확인 Alert 표시 (실수 방지).
- **Process**:
  - 로컬 전용 데이터(`syncStatus == .pending`) -> Hard Delete (완전 삭제).
  - 서버 동기화된 데이터(`syncStatus == .synced`) -> Soft Delete 마킹 (추후 서버 동기화 시 삭제 요청).

## Definition of Done (DoD)

- [ ] **UI**: 스와이프 삭제 동작이 되어야 한다.
- [ ] **Confirmation**: 삭제 확인 Alert이 표시되어야 한다.
- [ ] **Deletion**: 삭제된 항목이 리스트에서 사라져야 한다.

## 구현 힌트

- UI: Swipe to Delete & Alert 구현
- Repository: deleteSchedule() 로직 구현 (Sync 상태 따른 분기)

## 테스트

- **Unit**: Delete Logic Test
- **UI**: Delete Action & Cancel

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

