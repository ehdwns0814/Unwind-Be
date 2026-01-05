# [iOS-010] 올인 모드 진입 및 상태 관리

**Epic:** EPIC_ALLIN_MODE  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-01-15  
**Due Date:** 2026-01-16  
**Dependencies:** iOS-005

---

## 목적 및 요약
- **목적**: 하루 전체 스케줄을 완료할 때까지 쉬지 않고 달리는 강력한 모드를 제공한다.
- **요약**: "올인 모드" 토글을 켜고 시작하면, 그날 예정된 모든 스케줄(`isCompleted == false`)이 완료될 때까지 앱 차단이 지속된다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-010
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Init**: 오늘 날짜의 미완료 스케줄 개수 확인. (0개면 시작 불가)
- **Block**: `DeviceActivity`를 시작하되, 시간 제한 없는(또는 24시간) 모니터링 시작.
- **State**: `UserDefaults`에 `isAllInModeActive = true` 저장.

### UI
- 홈 화면 상단에 "올인 모드 진행 중" 배너 표시.
- 일반 스케줄 실행 버튼 비활성화 (체크박스만 활성화).

## Definition of Done (DoD)

- [ ] **Validation**: 오늘 예정된 스케줄이 없거나 모두 완료된 상태라면 "올인 모드를 시작할 수 없습니다" 알림이 떠야 한다.
- **Blocking**:
  - [ ] 올인 모드 시작 시 즉시 차단이 시작되어야 한다.
  - [ ] 일반 타이머와 달리 종료 시간이 정해져 있지 않아야 한다.
- **Persistence**: 앱을 종료했다 다시 켜도 "올인 모드 진행 중" 상태가 유지되어야 한다.
- **UI**: 메인 화면 상단에 현재 상태를 알리는 배너나 인디케이터가 표시되어야 한다.

## 구현 힌트

- AllInModeManager 클래스 구현 (Singleton)
- UI: 올인 모드 스위치 및 시작 버튼 구현
- ScreenTime: 무제한 차단 스케줄 설정

## 테스트

- **Manual**: 올인 모드 시작 시나리오

---

**Labels:** `ios`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

