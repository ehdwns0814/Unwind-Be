# REQ-FUNC-002: 최근 항목 빠른 추가 (iOS)

## 1. 목적 및 요약
- **목적**: 반복 입력 입력을 최소화하여 사용자 경험을 개선한다.
- **요약**: 최근에 사용한 스케줄(이름/시간)을 칩(Chip) 형태로 제공하여, 원터치로 입력 필드를 채운다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-002
- **Component**: iOS App (AddScheduleView)

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Query**: 로컬 저장소(`UserDefaults`)에서 `lastModified` 기준 내림차순 정렬 후, 중복된 이름/시간 조합 제거.
- **Limit**: 상위 5개 항목 추출.
- **UI**: `AddScheduleView` 상단에 가로 스크롤 가능한 칩 목록 표시.

### 3.2 입력 (Interaction)
- 사용자가 칩을 탭하면 `name`과 `duration` 필드에 값 자동 입력.

### 3.3 예외 (Exceptions)
- 저장된 스케줄이 없는 경우: 섹션 숨김 처리.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-002-iOS"
title: "최근 사용 스케줄 빠른 추가 (Chips UI)"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-002"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { ui: "Recent History Chips" }

steps_hint:
  - "Repository: getRecentSchedules() 메서드 구현 (Distinct 로직)"
  - "UI: Horizontal ScrollView 및 Chip 컴포넌트 구현"
  - "ViewModel: 칩 선택 시 Form 데이터 바인딩 로직"

preconditions:
  - "REQ-FUNC-001-iOS(저장소) 구현 완료"

postconditions:
  - "스케줄 생성 시마다 최근 목록이 갱신되어야 함"

tests:
  unit: ["Distinct Query Logic Test"]
  ui: ["Chip Selection -> Input Field Fill"]

dependencies: ["REQ-FUNC-001-iOS"]
estimated_effort: "S"
priority: "Should"
agent_profile: ["ios-developer"]
```
