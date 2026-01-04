# REQ-FUNC-030: 다국어 지원 리소스 관리 (iOS)

## 1. 목적 및 요약
- **목적**: 글로벌 확장을 고려한다.
- **요약**: 모든 UI 텍스트를 `Localizable.strings`로 관리한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-030 (SRS 번호 할당은 없었으나 다국어는 기본 요건) -> *SRS REQ-NF-011 등과 연계* (실제 SRS에는 030이 통계 전송이나, 편의상 다국어로 매핑) -> **수정: SRS 030은 강제 종료 카운트임. 이 파일은 NFR로 빼는 게 맞으나 iOS 공통으로 생성.** (파일명: REQ-NF-COMMON-iOS.md로 대체하거나, 우선 030이 강제 종료 백엔드이므로 iOS 대응 코드로 간주)

*Note: SRS REQ-FUNC-030은 "앱 강제 종료 횟수 카운트"이므로, 위 029/030 Backend Task에 병합됨. 여기서는 iOS 클라이언트 측 감지 로직을 기술.*

## 1. 목적 및 요약 (Revisied)
- **목적**: 사용자가 앱을 강제로 껐을 때 이를 감지하여 서버에 보고한다.
- **요약**: `applicationWillTerminate` 또는 `SceneDelegate`의 연결 끊김, 그리고 다음 실행 시 비정상 종료 여부를 판단하여 서버로 전송한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-030
- **Component**: iOS App

## 3. Sub-Tasks
- **Detect**: 집중 세션 중이었는데 앱이 종료됨 -> 플래그 저장.
- **Report**: 재실행 시 플래그 확인 -> `POST /api/stats/force-quit`.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-FUNC-030-iOS"
title: "앱 강제 종료 감지 및 리포팅"
type: "functional"
epic: "EPIC_STATS"
req_ids: ["REQ-FUNC-030"]
component: ["ios-app", "logic"]

inputs: []
outputs:
  success: { data: "Report Sent" }

steps_hint:
  - "AppLifecycleObserver 구현"
  - "Launch 시 비정상 종료 체크 로직"

preconditions: []

postconditions: []

tests:
  manual: ["강제 종료 후 재실행 시 로그 확인"]

dependencies: ["REQ-FUNC-006-iOS"]
estimated_effort: "S"
priority: "Should"
agent_profile: ["ios-developer"]
```
