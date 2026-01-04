# REQ-FUNC-017: 권한 해제 감지 및 사유 입력 강제 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자가 차단을 우회하기 위해 권한을 끄는 행위를 억제하고 패널티를 부여한다.
- **요약**: 앱 진입 시 `AuthorizationCenter.shared.authorizationStatus`를 확인하고, 권한이 해제되었다면 "사유 입력" 모달을 강제로 띄운다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-017
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Check**: `SceneDelegate` 또는 `App` 최상위에서 `sceneDidBecomeActive` 시 권한 체크.
- **Revoked**: 집중 모드 중이었는데 권한이 `.denied` 또는 `.notDetermined`라면 우회로 판단.
- **Action**: `UserDefaults`에 `isPenaltyActive = true` 저장 후 모달 띄움.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-017-iOS"
title: "권한 해제 감지 및 강제 모달"
type: "functional"
epic: "EPIC_PENALTY"
req_ids: ["REQ-FUNC-017"]
component: ["ios-app", "screentime", "ui"]

inputs:
  fields:
    - { name: "reason", type: "String", validation: "required, min:10" }

outputs:
  success: { ui: "Penalty Modal", data: "Penalty Logged" }

steps_hint:
  - "PenaltyManager: 권한 체크 로직 구현"
  - "PenaltyView: 사유 입력 UI (닫기 불가)"
  - "AppLifecycle 연결"

preconditions:
  - "REQ-FUNC-006-iOS 구현 완료"

postconditions:
  - "권한 해제 시 다른 화면으로 이동이 불가능해야 함"

tests:
  manual: ["설정에서 권한 끄고 앱 진입 테스트"]

dependencies: ["REQ-FUNC-006-iOS"]
estimated_effort: "M"
start_date: "2026-01-27"
due_date: "2026-01-28"
priority: "Should"
agent_profile: ["ios-developer"]
```
