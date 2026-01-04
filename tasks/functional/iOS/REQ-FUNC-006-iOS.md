# REQ-FUNC-006: 개별 스케줄 실행 및 타이머/차단 로직 (iOS)

## 1. 목적 및 요약
- **목적**: 설정한 시간 동안 실제 집중을 강제하기 위해 앱 차단을 수행한다.
- **요약**: 스케줄을 실행하면 카운트다운 타이머가 시작되고, `ScreenTime API`를 통해 설정된 앱들의 접근을 차단한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-006
- **Component**: iOS App (FamilyControls, DeviceActivity)

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Timer**: `Timer.publish` 또는 `DispatchSourceTimer` 사용하여 1초 단위 카운트다운.
- **Block**:
  - `FamilyControls.requestAuthorization()` (최초 1회).
  - `DeviceActivityCenter.startMonitoring(...)` 호출하여 차단 활성화.
  - `ManagedSettingsStore`를 통해 차단할 앱 목록(`shield.applicationCategories`) 적용.

### 3.2 UI
- 차단 중 화면(Timer View)으로 전환. "포기하기" 버튼 제공.

## 4. Definition of Done (DoD)
> SRS의 Acceptance Criteria를 기반으로 한 완료 조건입니다.

- [ ] **Authorization**: 최초 실행 시 스크린타임 권한 요청 팝업이 떠야 한다.
- **Blocking**:
  - [ ] 시작 버튼을 누르면 즉시 지정된 앱(예: YouTube)의 아이콘이 흐려지거나 실행 시 차단 화면이 떠야 한다.
  - [ ] 앱을 강제로 종료하고 다시 켜도 차단 상태가 유지되어야 한다.
- **Timer**:
  - [ ] 타이머가 1초 단위로 정확히 감소해야 한다.
  - [ ] 백그라운드 진입 후 다시 돌아와도 시간이 정확히 동기화되어 있어야 한다.
- **State**: 실행 중인 스케줄은 `isRunning` 상태여야 한다.

## 5. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-006-iOS"
title: "개별 스케줄 실행 및 앱 차단(ScreenTime) 연동"
type: "functional"
epic: "EPIC_FOCUS_MODE"
req_ids: ["REQ-FUNC-006", "REQ-NF-001", "REQ-NF-003"]
component: ["ios-app", "screentime", "timer"]

inputs:
  fields:
    - { name: "schedule", type: "Schedule", validation: "required" }
    - { name: "blockedApps", type: "FamilyActivitySelection" }

outputs:
  success: { ui: "Timer Running", system: "Apps Blocked" }

steps_hint:
  - "ScreenTime 권한 요청 로직 구현"
  - "DeviceActivityMonitorExtension 타겟 생성 및 설정"
  - "FocusManager (Timer + ScreenTime 제어) 클래스 구현"
  - "TimerView UI 구현"

preconditions:
  - "Apple Developer Account (FamilyControls 권한 필요)"
  - "REQ-FUNC-015-iOS (차단 앱 선택) 선행 권장 (없으면 전체 차단 테스트)"

postconditions:
  - "실행 시 지정된 앱이 실행되지 않고 Shield가 떠야 함"

tests:
  unit: ["Timer Logic"]
  manual: ["실기기에서 차단 동작 확인"]

dependencies: ["REQ-FUNC-001-iOS"]
estimated_effort: "L"
start_date: "2026-01-06"
due_date: "2026-01-08"
priority: "Must"
agent_profile: ["ios-developer"]
```
