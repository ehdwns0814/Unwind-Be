# REQ-FUNC-023: 세션 중단 복구 로직 (iOS)

## 1. 목적 및 요약
- **목적**: 앱이 비정상 종료(크래시, 배터리 등)된 후 재실행되었을 때 상태를 복구한다.
- **요약**: 앱 시작 시 `UserDefaults`에 저장된 `activeScheduleId`가 있다면, 남은 시간을 계산하여 타이머를 재개하거나 만료되었다면 완료 처리한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-023
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Launch**: `didFinishLaunching`에서 실행 중이던 스케줄 확인.
- **Logic**:
  - `Now > startTime + duration`: 이미 끝남 -> 완료 처리 화면 이동.
  - `Now < startTime + duration`: 아직 남음 -> 남은 시간 타이머 재시작.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-023-iOS"
title: "앱 재실행 시 세션 복구(Recovery)"
type: "functional"
epic: "EPIC_EXCEPTION"
req_ids: ["REQ-FUNC-023"]
component: ["ios-app", "logic"]

inputs:
  fields: []

outputs:
  success: { system: "Timer Resumed or Completed" }

steps_hint:
  - "SessionManager: restoreSession() 구현"
  - "AppLifecycle 핸들링"

preconditions:
  - "REQ-FUNC-006-iOS 구현 완료"

postconditions:
  - "크래시 후 재실행 시 차단이 유지되거나 결과가 나와야 함"

tests:
  manual: ["실행 중 강제 종료 후 재실행"]

dependencies: ["REQ-FUNC-006-iOS"]
estimated_effort: "M"
priority: "Could"
agent_profile: ["ios-developer"]
```
