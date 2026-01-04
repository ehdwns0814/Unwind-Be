# REQ-FUNC-014: 올인 모드 수동 포기 및 실패 처리 (iOS)

## 1. 목적 및 요약
- **목적**: 사용자가 도저히 지속할 수 없을 때 중단할 수 있게 하되, 실패 기록을 남긴다.
- **요약**: "올인 모드 중단" 버튼 -> 강력한 경고 팝업 -> 확인 시 차단 해제 및 `DailyRecord`에 실패(`Failure`) 기록.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-014
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Warning**: "중단하면 오늘은 실패로 기록됩니다." 경고.
- **Action**: 차단 해제, `Database`에 오늘 날짜 `status = .failure` 저장.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-014-iOS"
title: "올인 모드 수동 포기"
type: "functional"
epic: "EPIC_ALLIN_MODE"
req_ids: ["REQ-FUNC-014"]
component: ["ios-app", "ui"]

inputs:
  fields: []

outputs:
  success: { data: "Daily Status Failed", system: "Unblocked" }

steps_hint:
  - "UI: Give Up Button & Alert"
  - "Repository: updateDailyStatus(.failure)"

preconditions:
  - "REQ-FUNC-010-iOS 구현 완료"

postconditions:
  - "포기 후 재진입 시 실패 상태가 유지되어야 함"

tests:
  manual: ["포기 프로세스 확인"]

dependencies: ["REQ-FUNC-010-iOS"]
estimated_effort: "S"
start_date: "2026-01-21"
due_date: "2026-01-21"
priority: "Must"
agent_profile: ["ios-developer"]
```
