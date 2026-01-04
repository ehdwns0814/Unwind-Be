# REQ-FUNC-019: 스트릭 계산 및 UI 표시 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자의 지속적인 참여를 유도한다.
- **요약**: 최근 며칠 연속으로 성공(`success`)했는지 계산하여 메인 화면 상단에 "🔥 5일 연속" 배지를 표시한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-019
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Calculation**: 어제부터 역순으로 탐색하며 `status == .success`인 날짜 카운트. (계획 없는 날은 스킵할지 정책 결정 필요 -> SRS상 유지)
- **UI**: 메인 헤더에 아이콘과 숫자 표시.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-019-iOS"
title: "스트릭(Streak) 계산 및 표시"
type: "functional"
epic: "EPIC_STATS"
req_ids: ["REQ-FUNC-019"]
component: ["ios-app", "ui", "logic"]

inputs:
  fields: []

outputs:
  success: { ui: "Streak Badge Displayed" }

steps_hint:
  - "StreakCalculator 클래스 구현"
  - "UI: HomeHeaderView 구현"

preconditions:
  - "REQ-FUNC-001-iOS 구현 완료 (데이터 누적 필요)"

postconditions:
  - "날짜가 바뀌고 성공 시 스트릭이 증가해야 함"

tests:
  unit: ["Streak Calculation Logic"]

dependencies: ["REQ-FUNC-001-iOS"]
estimated_effort: "S"
start_date: "2026-01-24"
due_date: "2026-01-24"
priority: "Must"
agent_profile: ["ios-developer"]
```
