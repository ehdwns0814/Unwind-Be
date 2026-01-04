# REQ-FUNC-029: 통계 데이터 수집 및 전송 (Backend)

## 1. 목적 및 요약
- **목적**: 사용자의 집중 데이터를 수집하여 분석 기반을 마련한다.
- **요약**: `POST /api/stats/completion` 등을 통해 완료 여부, 집중 시간 등을 수신하고 `DailyStatistics` 테이블을 갱신한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-029, REQ-FUNC-030
- **Component**: Backend API

## 3. Sub-Tasks (구현 상세)

### 3.1 처리 (Process)
- **Aggregation**: 날짜별 `DailyStatistics` 레코드 생성 또는 업데이트.
- **Increment**: `totalFocusTime += duration`, `completedSchedules += 1`.
- **ForceQuit**: 강제 종료 이벤트 수신 시 `forceQuitCount += 1`.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-029-BE"
title: "통계 데이터 수집 API"
type: "functional"
epic: "EPIC_STATS"
req_ids: ["REQ-FUNC-029", "REQ-FUNC-030"]
component: ["backend", "api", "stats"]

inputs:
  fields:
    - { name: "type", type: "Enum(COMPLETE, FAIL, FORCE_QUIT)" }
    - { name: "duration", type: "Int" }

outputs:
  success: { http_status: 200 }

steps_hint:
  - "DailyStatistics Entity 및 Repo 생성"
  - "StatsService 구현 (UPSERT 로직)"

preconditions:
  - "REQ-FUNC-024-BE 완료"

postconditions:
  - "사용자의 일별 통계가 누적되어야 함"

tests:
  integration: ["Stats Accumulation"]

dependencies: ["REQ-FUNC-024-BE"]
estimated_effort: "M"
start_date: "2026-02-12"
due_date: "2026-02-13"
priority: "Must"
agent_profile: ["backend-developer"]
```
