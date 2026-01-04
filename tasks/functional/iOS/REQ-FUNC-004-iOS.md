# REQ-FUNC-004: 스케줄 수정 기능 (iOS)

## 1. 목적 및 요약
- **목적**: 기 생성된 스케줄의 정보를 수정할 수 있게 한다.
- **요약**: 리스트 아이템 길게 누르기(Context Menu)를 통해 "수정"을 선택하면 모달을 띄워 내용을 변경하고 로컬 저장소를 업데이트한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-004
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Input**: 기존 데이터(`name`, `duration`)가 채워진 상태로 모달 오픈.
- **Update**: 내용 변경 후 저장 시 `lastModified` 업데이트.
- **Sync**: 변경된 데이터는 `syncStatus = .pending`으로 마킹 (추후 백엔드 전송).

### 3.2 제약
- 완료된 스케줄(`isCompleted = true`)은 수정 불가.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-004-iOS"
title: "스케줄 수정 (로컬)"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-004"]
component: ["ios-app", "ui", "local-db"]

inputs:
  fields:
    - { name: "id", type: "UUID", validation: "required" }
    - { name: "newName", type: "String" }

outputs:
  success: { ui: "List Updated", data: "UserDefaults Updated" }

steps_hint:
  - "UI: Context Menu (Long Press) 구현"
  - "ViewModel: Edit Mode 상태 관리"
  - "Repository: updateSchedule() 메서드 구현"

preconditions:
  - "REQ-FUNC-001-iOS 구현 완료"

postconditions:
  - "수정 내용이 즉시 리스트에 반영되어야 함"

tests:
  unit: ["Update Logic Test"]
  ui: ["Edit Flow"]

dependencies: ["REQ-FUNC-001-iOS"]
estimated_effort: "S"
start_date: "2026-01-22"
due_date: "2026-01-22"
priority: "Must"
agent_profile: ["ios-developer"]
```
