# REQ-FUNC-021: 저장 실패 시 재시도 UI 구현 (iOS)

## 1. 목적 및 요약
- **목적**: 데이터 유실을 방지하고 사용자에게 인지시킨다.
- **요약**: 로컬 저장이 실패(디스크 풀 등)하거나 동기화 오류 시 Alert을 띄워 "재시도" 할 수 있게 한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-021
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Catch**: `Repository.save()` throws catch 블록.
- **UI**: `.alert("저장 실패", isPresented: ...)` 표시.
- **Retry**: 사용자가 재시도 누르면 다시 `save()` 호출.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-021-iOS"
title: "예외 처리: 저장 실패 및 재시도 UI"
type: "functional"
epic: "EPIC_EXCEPTION"
req_ids: ["REQ-FUNC-021"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { ui: "Error Alert" }

steps_hint:
  - "ErrorHandler 유틸리티 구현"
  - "View Modifier: .withErrorHandling()"

preconditions: []

postconditions: []

tests:
  manual: ["강제 에러 발생 시나리오"]

dependencies: []
estimated_effort: "XS"
priority: "Could"
agent_profile: ["ios-developer"]
```
