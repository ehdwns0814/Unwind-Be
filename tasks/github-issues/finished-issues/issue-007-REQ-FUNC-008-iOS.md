# [iOS-007] 스케줄 수동 포기 및 차단 해제

**Epic:** EPIC_FOCUS_MODE  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-13  
**Due Date:** 2026-01-13  
**Dependencies:** iOS-005

---

## 목적 및 요약
- **목적**: 급한 사유로 집중을 중단해야 할 때를 위한 탈출구를 제공한다.
- **요약**: 타이머 화면에서 "포기하기" 버튼을 누르면 경고 팝업 후 차단을 즉시 해제하고, 해당 스케줄을 `Fail` 상태로 기록한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-008
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Stop**:
  - `DeviceActivityCenter.stopMonitoring()` 호출.
  - `ManagedSettingsStore.clearAllSettings()` 호출하여 차단 해제.
- **Record**:
  - 현재 스케줄 상태를 `Incomplete` 또는 `Failed`로 업데이트.
  - 중단 시각 기록.

### UI
- "정말 포기하시겠습니까? 이번 집중은 실패로 기록됩니다." Alert 표시.

## Definition of Done (DoD)

- [ ] **Alert**: "포기하기" 버튼 탭 시 경고 Alert이 표시되어야 한다.
- [ ] **Unblock**: 확인 후 차단이 즉시 풀려야 한다.
- [ ] **State**: 해당 스케줄이 실패 상태로 저장되어야 한다.
- [ ] **Navigation**: 메인 화면으로 돌아가야 한다.

## 구현 힌트

- FocusManager: stopSession() 메서드 구현
- UI: Alert Action 핸들링
- Repository: 상태 업데이트 로직

## 테스트

- **Manual**: 포기 시나리오 테스트

---

**Labels:** `ios`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

