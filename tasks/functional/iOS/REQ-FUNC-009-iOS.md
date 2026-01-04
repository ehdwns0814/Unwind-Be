# REQ-FUNC-009: 타이머 종료 시 자동 성공 처리 (iOS)

## 1. 목적 및 요약
- **목적**: 집중 시간 달성을 축하하고 기록한다.
- **요약**: 타이머가 0이 되면 자동으로 차단을 해제하고, "성공" 화면을 띄운 뒤 스케줄을 완료 처리한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-009
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Timer End**:
  - 차단 해제 (`stopMonitoring`).
  - 로컬 DB 업데이트: `isCompleted = true`, `completedAt = Now`.
- **UI**:
  - Confetti(꽃가루) 효과 등 축하 UI 표시.
  - "완료" 버튼 누르면 홈으로 복귀.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-009-iOS"
title: "타이머 종료 및 성공 처리"
type: "functional"
epic: "EPIC_FOCUS_MODE"
req_ids: ["REQ-FUNC-009"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { ui: "Success Screen", data: "Status Completed" }

steps_hint:
  - "FocusManager: Timer Completion Handler 구현"
  - "UI: SuccessView 구현 (Animation)"
  - "Repository: 완료 상태 저장"

preconditions:
  - "REQ-FUNC-006-iOS 구현 완료"

postconditions:
  - "성공 기록이 남고 리스트에서 완료 표시되어야 함"

tests:
  unit: ["Completion Logic"]
  manual: ["타이머 1분 설정 후 완료 테스트"]

dependencies: ["REQ-FUNC-006-iOS"]
estimated_effort: "S"
start_date: "2026-01-14"
due_date: "2026-01-14"
priority: "Must"
agent_profile: ["ios-developer"]
```
