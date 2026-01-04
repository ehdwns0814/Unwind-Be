# REQ-NF-001: iOS 성능 요구사항 (응답 속도 및 메모리)

## 1. 목적 및 요약
- **목적**: 쾌적한 사용자 경험을 보장한다.
- **요약**: UI 인터랙션 응답 시간을 100ms 이내로 유지하고, 앱 차단(Shield) 적용 지연 시간을 1초 이내로 줄인다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-NF-001, REQ-NF-002
- **Component**: iOS App

## 3. Definition of Done (DoD)
- [ ] **UI Latency**: 버튼 탭 등 사용자 입력에 대한 피드백이 100ms 이내에 나타나야 한다. (Spinning, Highlight 등)
- [ ] **Shield Latency**: 집중 시작 후 실제 앱 실행이 차단되기까지 1초를 넘지 않아야 한다.
- **Resource Usage**:
  - [ ] 아이들(Idle) 상태에서 CPU 사용률 5% 미만.
  - [ ] 메모리 누수(Memory Leak)가 없어야 함 (Instruments 검증).

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-NF-001-PERF"
title: "성능 목표 검증 (UI 응답 및 Shield 지연)"
type: "non-functional"
epic: "EPIC_NFR"
req_ids: ["REQ-NF-001"]
component: ["ios-app", "performance"]

steps_hint:
  - "XCTest MeasureBlock을 이용한 성능 테스트 작성"
  - "Instruments(Time Profiler)로 병목 구간 분석"

tests:
  manual: ["초시계 측정 (Shield Delay)"]
  automated: ["Performance Test Suite 실행"]

priority: "Should"
agent_profile: ["ios-developer"]
```
