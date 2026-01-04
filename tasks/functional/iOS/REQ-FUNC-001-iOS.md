# REQ-FUNC-001: 스케줄 생성 UI 및 로컬 저장 로직 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자가 새로운 집중 스케줄을 생성하여 로컬에 저장한다.
- **요약**: 메인 화면의 "+" 버튼을 통해 스케줄 생성 모달을 띄우고, 이름과 집중 시간을 입력받아 `UserDefaults`에 저장한다. API 연동은 추후 진행하므로, 동기화 상태는 `Pending`으로 설정한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-001
- **Component**: iOS App (AddScheduleView, ScheduleRepository)

## 3. Sub-Tasks (구현 상세)

### 3.1 입력 (Input)
- **UI Component**: `AddScheduleView` (Sheet 형태)
- **Fields**:
  - `name`: String (필수, max 100)
  - `duration`: Int (필수, 분 단위 입력 -> 내부적으로 초 단위 저장)
  - `date`: Date (기본값 Today)

### 3.2 처리 (Process)
- **Validation**:
  - 이름: 빈 문자열 불가
  - 시간: 0분 초과
- **Persistence (Local)**:
  - `Schedule` 모델 생성 (UUID 발급)
  - `UserDefaults` 배열에 저장 (Optimistic Update)
  - `syncStatus` = `.pending` 초기화

### 3.3 출력 (Output)
- **Success**: 모달 닫힘, 메인 리스트에 새 항목 즉시 추가
- **Failure**: 입력값 검증 실패 시 오류 메시지 표시

## 4. Definition of Done (DoD)
> SRS의 Acceptance Criteria를 기반으로 한 완료 조건입니다.

- [ ] **UI**: "+" 버튼 탭 시 모달이 부드럽게 열려야 한다.
- [ ] **Validation**: 이름 누락 또는 시간이 0일 경우 "입력 오류" 알림이 떠야 한다.
- [ ] **Persistence**: 앱을 껐다 켜도 생성한 스케줄이 리스트에 남아 있어야 한다.
- [ ] **Data Integrity**: 저장된 데이터의 `syncStatus`는 반드시 `.pending` 이어야 한다.
- [ ] **Performance**: 저장 버튼 탭 후 0.5초 이내에 리스트에 반영되고 모달이 닫혀야 한다.

## 5. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-001-iOS"
title: "스케줄 생성 UI 및 로컬 저장 구현"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-001"]
component: ["ios-app", "ui", "local-db"]

inputs:
  fields:
    - { name: "name", type: "String", validation: "required" }
    - { name: "duration", type: "Int", validation: "min:1" }

outputs:
  success: { ui: "List Updated", data: "Saved to UserDefaults" }

steps_hint:
  - "Schedule 모델 구조체 정의 (Codable, Identifiable)"
  - "ScheduleRepository (UserDefaults Manager) 구현"
  - "AddScheduleView (SwiftUI) 구현"
  - "ViewModel 연결 및 입력 검증 로직 작성"

preconditions:
  - "Xcode 프로젝트 생성 완료"

postconditions:
  - "앱 재실행 후에도 생성한 스케줄이 유지되어야 함"

tests:
  unit: ["ViewModel Validation Test", "Repository Save Test"]
  ui: ["Add Schedule Flow"]

dependencies: []
estimated_effort: "M"
start_date: "2026-01-03"
due_date: "2026-01-04"
priority: "Must"
agent_profile: ["ios-developer"]
```
