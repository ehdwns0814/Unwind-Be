# REQ-FUNC-018: 패널티 적용 및 스트릭 리셋 로직 (iOS)

## 1. 목적 및 요약
- **목적**: 권한 해제 우회 시 달성 기록을 무효화하여 공정한 도전 환경을 만든다.
- **요약**: 사유를 입력하고 제출하면 해당 일자의 `status`를 `Failure`로 변경하고, 현재까지의 스트릭(Streak)을 0으로 초기화한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-018
- **Component**: iOS App

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Update**: `repository.updateDailyStatus(.failure)`.
- **Reset**: `UserDefaults.currentStreak = 0`.
- **Log**: 입력된 사유를 로컬 로그(또는 추후 서버)에 저장.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-018-iOS"
title: "패널티 적용 (실패 처리 및 스트릭 초기화)"
type: "functional"
epic: "EPIC_PENALTY"
req_ids: ["REQ-FUNC-018"]
component: ["ios-app", "logic"]

inputs:
  fields: []

outputs:
  success: { data: "Streak Reset, Daily Failed" }

steps_hint:
  - "StreakManager: resetStreak() 메서드 구현"
  - "Repository: Penalty 적용 로직"

preconditions:
  - "REQ-FUNC-017-iOS 구현 완료"

postconditions:
  - "메인 화면에서 스트릭이 깨진 것으로 보여야 함"

tests:
  unit: ["Streak Reset Logic"]

dependencies: ["REQ-FUNC-017-iOS"]
estimated_effort: "S"
start_date: "2026-01-29"
due_date: "2026-01-29"
priority: "Should"
agent_profile: ["ios-developer"]
```
