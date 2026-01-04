# REQ-FUNC-022: 권한 거부 대응 안내 UI (iOS)

## 1. 목적 및 요약
- **목적**: 앱의 핵심 기능인 차단을 사용하기 위해 필수 권한을 유도한다.
- **요약**: `AuthorizationStatus == .denied` 일 때, 메인 화면을 가리고 "설정으로 이동하여 권한을 허용해주세요" 안내 뷰를 표시한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-022
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Check**: `DeviceActivityCenter` 권한 확인.
- **UI**: PermissionRequestView (FullScreenCover).
- **Link**: `UIApplication.openSettingsURLString` 이동 버튼.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-022-iOS"
title: "권한 거부 시 안내 화면(Empty State)"
type: "functional"
epic: "EPIC_EXCEPTION"
req_ids: ["REQ-FUNC-022"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { ui: "Permission Request View" }

steps_hint:
  - "PermissionManager 구현"
  - "UI: PermissionView 구현"

preconditions: []

postconditions:
  - "권한 허용 전까지는 메인 기능 사용 불가해야 함"

tests:
  manual: ["권한 거부 상태 테스트"]

dependencies: []
estimated_effort: "S"
priority: "Must"
agent_profile: ["ios-developer"]
```
