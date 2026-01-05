# [iOS-020] 권한 해제 패널티 (스트릭 리셋)

**Epic:** EPIC_STATS  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-01-23  
**Due Date:** 2026-01-24  
**Dependencies:** iOS-016

---

## 목적 및 요약
- **목적**: 사용자가 차단을 우회하기 위해 권한을 끄는 행위를 억제하고 패널티를 부여한다.
- **요약**: 앱 진입 시 `AuthorizationCenter.shared.authorizationStatus`를 확인하고, 권한이 해제되었다면 "사유 입력" 모달을 강제로 띄운 후 스트릭을 리셋한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-017, REQ-FUNC-018, REQ-FUNC-022
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Check**: `SceneDelegate` 또는 `App` 최상위에서 `sceneDidBecomeActive` 시 권한 체크.
- **Revoked**: 집중 모드 중이었는데 권한이 `.denied` 또는 `.notDetermined`라면 우회로 판단.
- **Action**: 
  - `UserDefaults`에 `isPenaltyActive = true` 저장 후 모달 띄움
  - 사유 입력 후 `currentStreak = 0` 리셋
  - `DailyRecord.status = .failure` 저장

## Definition of Done (DoD)

- [ ] **Detection**: 권한 해제 감지가 정확해야 한다.
- [ ] **Modal**: 사유 입력 모달이 강제로 떠야 한다.
- [ ] **Reset**: 스트릭이 0으로 초기화되어야 한다.
- [ ] **State**: 실패 상태로 기록되어야 한다.

## 구현 힌트

- PenaltyManager: 권한 체크 로직 구현
- PenaltyView: 사유 입력 UI (닫기 불가)
- StreakManager: resetStreak() 메서드 구현
- AppLifecycle 연결

## 테스트

- **Manual**: 설정에서 권한 끄고 앱 진입 테스트

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

