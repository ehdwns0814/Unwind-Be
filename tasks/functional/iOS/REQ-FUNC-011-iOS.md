# REQ-FUNC-011: 올인 모드 진행률(Progress) 관리 (iOS)

## 1. 목적 및 요약
- **목적**: 올인 모드 중 사용자가 스케줄을 하나씩 클리어하는 경험을 제공한다.
- **요약**: 사용자가 스케줄 목록에서 "완료" 체크를 할 때마다 진행률(n/m)을 업데이트하고, 남아있는 스케줄이 있는지 확인한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-011
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Interaction**: 리스트 아이템의 체크박스 탭 -> `isCompleted` 토글.
- **Check**: 모든 스케줄이 완료되었는지(`completedCount == totalCount`) 검사.
- **UI**: Progress Bar 또는 "3/5 완료" 텍스트 업데이트.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-011-iOS"
title: "올인 모드 스케줄 완료 체크 및 진행률"
type: "functional"
epic: "EPIC_ALLIN_MODE"
req_ids: ["REQ-FUNC-011"]
component: ["ios-app", "ui"]

inputs:
  fields:
    - { name: "scheduleId", type: "UUID" }

outputs:
  success: { ui: "Progress Updated" }

steps_hint:
  - "ViewModel: toggleCompletion() 메서드 구현"
  - "UI: Checkbox Component 구현"
  - "AllInModeManager: 진행률 계산 로직"

preconditions:
  - "REQ-FUNC-010-iOS 구현 완료"

postconditions:
  - "체크 시 진행률이 즉시 반영되어야 함"

tests:
  unit: ["Progress Calculation"]
  ui: ["Check/Uncheck Interaction"]

dependencies: ["REQ-FUNC-010-iOS"]
estimated_effort: "S"
start_date: "2026-01-17"
due_date: "2026-01-17"
priority: "Must"
agent_profile: ["ios-developer"]
```
