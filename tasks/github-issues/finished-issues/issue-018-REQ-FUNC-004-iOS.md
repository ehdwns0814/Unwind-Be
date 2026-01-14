# [iOS-018] 스케줄 수정 기능 (로컬)

**Epic:** EPIC_SCHEDULE_MGMT  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-22  
**Due Date:** 2026-01-22  
**Dependencies:** iOS-001

---

## 목적 및 요약
- **목적**: 기 생성된 스케줄의 정보를 수정할 수 있게 한다.
- **요약**: 리스트 아이템 길게 누르기(Context Menu)를 통해 "수정"을 선택하면 모달을 띄워 내용을 변경하고 로컬 저장소를 업데이트한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-004
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Input**: 기존 데이터(`name`, `duration`)가 채워진 상태로 모달 오픈.
- **Update**: 내용 변경 후 저장 시 `lastModified` 업데이트.
- **Sync**: 변경된 데이터는 `syncStatus = .pending`으로 마킹 (추후 백엔드 전송).

### 제약
- 완료된 스케줄(`isCompleted = true`)은 수정 불가.

## Definition of Done (DoD)

- [ ] **UI**: Context Menu가 표시되어야 한다.
- [ ] **Update**: 수정 내용이 즉시 리스트에 반영되어야 한다.
- [ ] **Validation**: 완료된 스케줄은 수정 불가해야 한다.

## 구현 힌트

- UI: Context Menu (Long Press) 구현
- ViewModel: Edit Mode 상태 관리
- Repository: updateSchedule() 메서드 구현

## 테스트

- **Unit**: Update Logic Test
- **UI**: Edit Flow

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

