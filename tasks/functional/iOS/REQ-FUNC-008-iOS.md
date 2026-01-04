# REQ-FUNC-008: 개별 스케줄 수동 중단 처리 (iOS)

## 1. 목적 및 요약
- **목적**: 급한 사유로 집중을 중단해야 할 때를 위한 탈출구를 제공한다.
- **요약**: 타이머 화면에서 "포기하기" 버튼을 누르면 경고 팝업 후 차단을 즉시 해제하고, 해당 스케줄을 `Fail` 상태로 기록한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-008
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Stop**:
  - `DeviceActivityCenter.stopMonitoring()` 호출.
  - `ManagedSettingsStore.clearAllSettings()` 호출하여 차단 해제.
- **Record**:
  - 현재 스케줄 상태를 `Incomplete` 또는 `Failed`로 업데이트.
  - 중단 시각 기록.

### 3.2 UI
- "정말 포기하시겠습니까? 이번 집중은 실패로 기록됩니다." Alert 표시.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-008-iOS"
title: "스케줄 수동 포기 및 차단 해제"
type: "functional"
epic: "EPIC_FOCUS_MODE"
req_ids: ["REQ-FUNC-008"]
component: ["ios-app", "screentime"]

inputs:
  fields: []

outputs:
  success: { ui: "Block Cleared", data: "Status Failed" }

steps_hint:
  - "FocusManager: stopSession() 메서드 구현"
  - "UI: Alert Action 핸들링"
  - "Repository: 상태 업데이트 로직"

preconditions:
  - "REQ-FUNC-006-iOS 구현 완료"

postconditions:
  - "차단이 즉시 풀리고 메인 화면으로 돌아가야 함"

tests:
  manual: ["포기 시나리오 테스트"]

dependencies: ["REQ-FUNC-006-iOS"]
estimated_effort: "S"
start_date: "2026-01-13"
due_date: "2026-01-13"
priority: "Must"
agent_profile: ["ios-developer"]
```
