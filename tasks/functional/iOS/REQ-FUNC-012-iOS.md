# REQ-FUNC-012: 올인 모드 전용 Shield UI 구성 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자가 현재 올인 모드 중임을 인지하고, 얼마나 남았는지 Shield 화면에서 확인하게 한다.
- **요약**: `ShieldConfigurationDataSource`에서 현재 모드가 "올인 모드"인지 확인하고, 그렇다면 "올인 모드 진행 중 (3/5)"와 같은 문구를 표시한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-012
- **Component**: iOS App (ShieldExtension)

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Shared Storage**: Main App과 Extension 간 데이터 공유 (`App Group` 활용 필요).
- **Display**: `UserDefaults(suiteName: ...)`에서 진행률 읽어와 Shield에 표시.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-012-iOS"
title: "올인 모드 Shield UI (진행률 표시)"
type: "functional"
epic: "EPIC_ALLIN_MODE"
req_ids: ["REQ-FUNC-012"]
component: ["ios-app", "screentime"]

inputs:
  fields: []

outputs:
  success: { ui: "Sield with Progress Info" }

steps_hint:
  - "App Group 설정 (Capabilities)"
  - "SharedUserDefaultsManager 구현"
  - "ShieldDataSource: 모드에 따른 분기 처리"

preconditions:
  - "REQ-FUNC-007-iOS (기본 Shield) 구현 완료"
  - "REQ-FUNC-010-iOS (올인 모드) 구현 완료"

postconditions:
  - "올인 모드 중에는 차단 화면 멘트가 달라져야 함"

tests:
  manual: ["올인 모드 진입 후 차단 앱 실행 확인"]

dependencies: ["REQ-FUNC-007-iOS", "REQ-FUNC-010-iOS"]
estimated_effort: "M"
start_date: "2026-01-18"
due_date: "2026-01-19"
priority: "Must"
agent_profile: ["ios-developer"]
```
