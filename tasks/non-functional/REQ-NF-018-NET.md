# REQ-NF-018: 네트워크 안정성 및 오프라인 대응 (iOS)

## 1. 목적 및 요약
- **목적**: 불안정한 네트워크 환경에서도 데이터 유실을 방지한다.
- **요약**: 오프라인 상태에서도 스케줄 생성/수정이 가능해야 하며(`Pending` 상태), 네트워크 복구 시 자동으로 재동기화(Retry/Sync)를 수행해야 한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-NF-018, REQ-FUNC-021
- **Component**: iOS App

## 3. Definition of Done (DoD)
- [ ] **Offline Capability**:
  - 비행기 모드에서 스케줄 생성 시 에러 없이 로컬에 저장되어야 한다.
  - `syncStatus`가 `.pending`으로 마킹되어야 한다.
- **Auto Sync**:
  - 네트워크 연결이 복구되면(Reachability Monitor), 백그라운드에서 동기화 로직이 트리거되어야 한다.
- **Graceful Failure**:
  - API 타임아웃(10초) 발생 시, 재시도(Exponential Backoff) 하거나 나중에 다시 시도하도록 큐에 넣어야 한다.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-NF-018-NET"
title: "네트워크 안정성 및 오프라인 동기화"
type: "non-functional"
epic: "EPIC_NFR"
req_ids: ["REQ-NF-018"]
component: ["ios-app", "network"]

steps_hint:
  - "NetworkMonitor (NWPathMonitor) 구현"
  - "SyncManager: Queue 및 Retry 로직 구현"

tests:
  manual: ["비행기 모드 -> 생성 -> 해제 -> 동기화 확인"]

priority: "Should"
agent_profile: ["ios-developer"]
```
