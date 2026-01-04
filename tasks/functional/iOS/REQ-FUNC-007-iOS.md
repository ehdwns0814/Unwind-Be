# REQ-FUNC-007: ShieldConfigurationExtension 구현 (iOS)

## 1. 목적 및 요약
- **목적**: 차단된 앱 실행 시 사용자에게 동기를 부여하거나 현재 상황을 안내하는 커스텀 화면을 제공한다.
- **요약**: `ShieldConfigurationDataSource`를 구현하여 차단 화면의 아이콘, 문구, 배경색 등을 커스터마이징한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-007
- **Component**: iOS App Extension (ShieldConfiguration)

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **DataSource**: `ShieldConfigurationExtension` 타겟 생성.
- **Config**: 현재 실행 중인 스케줄 이름 및 남은 시간(가능하다면) 표시.
  *(iOS 제약상 Shield 내에서 실시간 타이머는 어려울 수 있으므로 정적 멘트 위주 구성)*
- **UI**: 친근하지만 단호한 톤의 메시지 ("지금은 집중할 시간이에요!").

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-007-iOS"
title: "앱 차단 화면 (Shield) 커스터마이징"
type: "functional"
epic: "EPIC_FOCUS_MODE"
req_ids: ["REQ-FUNC-007"]
component: ["ios-app", "screentime"]

inputs:
  fields: []

outputs:
  success: { ui: "Custom Shield Displayed" }

steps_hint:
  - "ShieldConfigurationExtension 타겟 추가"
  - "ShieldConfigurationDataSource 프로토콜 구현"
  - "메시지 및 디자인 Asset 적용"

preconditions:
  - "REQ-FUNC-006-iOS (차단 로직) 구현 중"

postconditions:
  - "차단 앱 실행 시 기본 시스템 화면 대신 커스텀 화면이 떠야 함"

tests:
  manual: ["실기기 테스트"]

dependencies: ["REQ-FUNC-006-iOS"]
estimated_effort: "M"
start_date: "2026-01-11"
due_date: "2026-01-12"
priority: "Must"
agent_profile: ["ios-developer"]
```
