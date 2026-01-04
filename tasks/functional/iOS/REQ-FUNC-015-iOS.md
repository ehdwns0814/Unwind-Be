# REQ-FUNC-015: 차단 대상 앱/웹사이트 선택 UI (iOS)

## 1. 목적 및 요약
- **목적**: 사용자가 집중 시 차단할 방해 요소들을 직접 고를 수 않게 한다.
- **요약**: `FamilyActivityPicker`를 호출하여 앱, 카테고리, 웹사이트를 선택하고 선택된 토큰을 `UserDefaults`에 저장한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-015
- **Component**: iOS App (FamilyControls)

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Picker**: `FamilyActivityPicker(selection: $model.selection)` 표시.
- **Save**: 선택된 `FamilyActivitySelection` 객체를 인코딩하여 저장.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-015-iOS"
title: "차단 앱 관리 (FamilyActivityPicker)"
type: "functional"
epic: "EPIC_SETTINGS"
req_ids: ["REQ-FUNC-015"]
component: ["ios-app", "ui", "screentime"]

inputs:
  fields: []

outputs:
  success: { data: "Selection Saved" }

steps_hint:
  - "ShieldSettingsViewModel 구현 (Selection 상태 관리)"
  - "ShieldSettingsView 구현 (Picker 호출)"
  - "Selection Encoder/Decoder 구현 (UserDefaults 저장용)"

preconditions:
  - "FamilyControls 권한 획득 상태"

postconditions:
  - "선택한 앱들이 차단 로직(REQ-FUNC-006)에 반영되어야 함"

tests:
  manual: ["Picker 열기 및 선택 저장 테스트"]

dependencies: []
estimated_effort: "M"
start_date: "2026-01-09"
due_date: "2026-01-10"
priority: "Must"
agent_profile: ["ios-developer"]
```
