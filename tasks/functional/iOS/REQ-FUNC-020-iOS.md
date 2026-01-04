# REQ-FUNC-020: 주간/월간 통계 그래프 구현 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자의 성취도를 시각적으로 보여준다.
- **요약**: `Charts` 프레임워크를 사용하여 지난 7일/30일간의 집중 시간 및 성공률을 막대/선 그래프로 표현한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-020
- **Component**: iOS App (Swift Charts)

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Query**: `Repository.getDailyRecords(range: ...)` 호춮.
- **Transform**: `[Date: Record]` -> `[ChartDataEntry]` 변환.
- **UI**: 탭(주간/월간) 전환 시 그래프 애니메이션.

## 4. Definition of Done (DoD)
> SRS의 Acceptance Criteria를 기반으로 한 완료 조건입니다.

- [ ] **Data Accuracy**: 지난 7일간의 집중 시간 합계가 DB에 저장된 실제 시간과 정확히 일치해야 한다.
- **UI Interaction**:
  - [ ] 주간/월간 탭 전환 시 그래프가 부드럽게 애니메이션되어야 한다.
  - [ ] 데이터가 없는 날짜는 0으로 표시되어야 한다 (누락되지 않음).
- **Visual**: X축(날짜)과 Y축(시간) 레이블이 가독성 있게 표시되어야 한다.

## 5. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-020-iOS"
title: "통계 그래프 (Charts) 구현"
type: "functional"
epic: "EPIC_STATS"
req_ids: ["REQ-FUNC-020"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { ui: "Charts View Displayed" }

steps_hint:
  - "Swift Charts 라이브러리 활용"
  - "StatsViewModel: 데이터 가공 로직"
  - "StatsView UI 구현"

preconditions:
  - "REQ-FUNC-001-iOS 데이터 필요"

postconditions: []

tests:
  ui: ["Chart Rendering Check"]

dependencies: ["REQ-FUNC-001-iOS"]
estimated_effort: "M"
start_date: "2026-01-25"
due_date: "2026-01-26"
priority: "Should"
agent_profile: ["ios-developer"]
```
