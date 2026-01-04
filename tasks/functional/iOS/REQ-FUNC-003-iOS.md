# REQ-FUNC-003: 날짜 탭 내비게이션 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자가 오늘 외의 다른 날짜(과거/미래)의 스케줄을 관리할 수 있게 한다.
- **요약**: 메인 화면 상단에 7일간의 날짜 탭(좌우 스와이프)을 제공하고, 선택 시 해당 날짜의 스케줄 목록을 불러온다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-003
- **Component**: iOS App (HomeView)

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **UI**: 오늘을 기준으로 `[-3, +3]`일 또는 주간 캘린더 뷰 제공.
- **Interaction**: 날짜 선택 시 `ViewModel.selectedDate` 변경 및 데이터 리로드.
- **Data**: `Repository.getSchedules(for: Date)` 호출.

### 3.2 설정
- 표시 범위: 기본적으로 "오늘"이 중심, 필요 시 무한 스크롤 또는 달력 모달 확장 가능.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-003-iOS"
title: "날짜 선택 및 스케줄 목록 필터링"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-003"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { ui: "Date Tabs & Filtered List" }

steps_hint:
  - "DateHelper 유틸리티 (날짜 생성/포맷팅) 구현"
  - "UI: DateStripView (Horizontal Scroll) 구현"
  - "ViewModel: selectedDate 상태 관리 및 필터링 로직"

preconditions:
  - "REQ-FUNC-001-iOS 구현 완료"

postconditions:
  - "날짜 변경 시 해당 일자의 데이터만 표시되어야 함"

tests:
  unit: ["Date Filtering Logic Test"]
  ui: ["Swipe/Tap Date -> List Refresh"]

dependencies: ["REQ-FUNC-001-iOS"]
estimated_effort: "S"
start_date: "2026-01-05"
due_date: "2026-01-05"
priority: "Must"
agent_profile: ["ios-developer"]
```
