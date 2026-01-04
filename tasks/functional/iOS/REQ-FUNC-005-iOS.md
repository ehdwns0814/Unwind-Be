# REQ-FUNC-005: 스케줄 삭제 기능 (iOS)

## 1. 목적 및 요약
- **목적**: 불필요한 스케줄을 제거한다.
- **요약**: Context Menu 또는 스와이프 액션을 통해삭제 요청을 하고, 사용자 확인 후 로컬 저장소에서 제거(Soft Delete 또는 Hard Delete)한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-005
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **UI**: 삭제 확인 Alert 표시 (실수 방지).
- **Process**:
  - 로컬 전용 데이터(`syncStatus == .pending`) -> Hard Delete (완전 삭제).
  - 서버 동기화된 데이터(`syncStatus == .synced`) -> Soft Delete 마킹 (추후 서버 동기화 시 삭제 요청).

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-005-iOS"
title: "스케줄 삭제 (로컬)"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-005"]
component: ["ios-app", "ui", "local-db"]

inputs:
  fields:
    - { name: "id", type: "UUID", validation: "required" }

outputs:
  success: { ui: "Item Removed from List" }

steps_hint:
  - "UI: Swipe to Delete & Alert 구현"
  - "Repository: deleteSchedule() 로직 구현 (Sync 상태 따른 분기)"

preconditions:
  - "REQ-FUNC-001-iOS 구현 완료"

postconditions:
  - "삭제된 항목이 리스트에서 사라져야 함"

tests:
  unit: ["Delete Logic Test"]
  ui: ["Delete Action & Cancel"]

dependencies: ["REQ-FUNC-001-iOS"]
estimated_effort: "S"
start_date: "2026-01-23"
due_date: "2026-01-23"
priority: "Must"
agent_profile: ["ios-developer"]
```
