# REQ-FUNC-016: 앱 버전 정보 표시 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자가 현재 설치된 앱 버전을 확인할 수 있게 한다.
- **요약**: 설정 화면 하단에 `Bundle.main.infoDictionary` 정보를 읽어 표시한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-016
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Read**: `CFBundleShortVersionString` (버전), `CFBundleVersion` (빌드 번호).
- **UI**: 텍스트 뷰 표시 (예: "v1.0.0 (Build 1)").

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-016-iOS"
title: "앱 버전 정보 표시"
type: "functional"
epic: "EPIC_SETTINGS"
req_ids: ["REQ-FUNC-016"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { ui: "Version Text Displayed" }

steps_hint:
  - "AppInfoUtil 구현"
  - "SettingsView 하단에 추가"

preconditions: []

postconditions: []

tests:
  unit: ["Version Reading Logic"]

dependencies: []
estimated_effort: "XS"
priority: "Must"
agent_profile: ["ios-developer"]
```
