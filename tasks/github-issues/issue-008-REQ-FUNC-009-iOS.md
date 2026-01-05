# [iOS-008] 타이머 종료 및 성공 처리

**Epic:** EPIC_FOCUS_MODE  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-14  
**Due Date:** 2026-01-14  
**Dependencies:** iOS-005

---

## 목적 및 요약
- **목적**: 집중 시간 달성을 축하하고 기록한다.
- **요약**: 타이머가 0이 되면 자동으로 차단을 해제하고, "성공" 화면을 띄운 뒤 스케줄을 완료 처리한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-009
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Timer End**:
  - 차단 해제 (`stopMonitoring`).
  - 로컬 DB 업데이트: `isCompleted = true`, `completedAt = Now`.
- **UI**:
  - Confetti(꽃가루) 효과 등 축하 UI 표시.
  - "완료" 버튼 누르면 홈으로 복귀.

## Definition of Done (DoD)

- [ ] **Auto Complete**: 타이머가 0이 되면 자동으로 차단이 해제되어야 한다.
- [ ] **Success Screen**: 축하 화면이 표시되어야 한다.
- [ ] **State**: 성공 기록이 남고 리스트에서 완료 표시되어야 한다.
- [ ] **Animation**: 축하 애니메이션(Confetti 등)이 표시되어야 한다.

## 구현 힌트

- FocusManager: Timer Completion Handler 구현
- UI: SuccessView 구현 (Animation)
- Repository: 완료 상태 저장

## 테스트

- **Unit**: Completion Logic
- **Manual**: 타이머 1분 설정 후 완료 테스트

---

**Labels:** `ios`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

