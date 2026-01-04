# FIX-BUG-147-iOS: 앱 초기 실행 시 하얀 화면 및 Signal 9 크래시 해결

## 1. 목적 및 요약
- **목적**: 앱 실행 시 발생하는 화이트 스크린 현상과 프로세스 강제 종료(Signal 9) 문제를 해결하여 정상적인 서비스 이용이 가능하게 한다.
- **요약**: 초기화 로직에서의 권한 체크 충돌을 해결하고, 시뮬레이터 환경에서의 안정성을 확보한다.

## 2. 관련 스펙 (SRS)
- **ID**: FIX-BUG-147
- **Component**: iOS App (Core)

## 3. Sub-Tasks (구현 상세)

### 3.1 진단 (Diagnosis)
- [x] **Logging**: `FocusManager`, `ScreentimeManager`, `PenaltyManager`의 초기화 시점에 로그 추가.
- [x] **Thread Analysis**: 메인 스레드 차단 여부 및 교착 상태 확인.

### 3.2 수정 (Action)
- [x] **Async Initialization**: 권한 체크 로직을 비동기로 분리하여 메인 스레드 프리징 방지.
- [x] **Mock Mode**: 유료 계정 부재 시에도 개발이 가능하도록 `ScreentimeManager`에 Mock 모드 도입.
- [x] **Infinite Loop Fix**: `StreakCalculator`의 무한 루프 가능성 제거 및 탐색 범위 제한.
- [x] **Cleanup**: 디버깅을 위해 추가했던 로그 및 하드코딩 제거.

## 4. 메타데이터 (YAML)

```yaml
task_id: "FIX-BUG-147-iOS"
title: "앱 초기화 크래시 해결"
type: "bug-fix"
epic: "STABILITY"
req_ids: ["FIX-BUG-147"]
component: ["ios-app", "core"]

inputs:
  fields: []

outputs:
  success: { system: "App Launched Successfully" }

steps_hint:
  - "Add debug logs to Manager classes"
  - "Refactor authorization check to be safer on main thread"

preconditions:
  - "Xcode 16 / iOS 18.6 환경"

postconditions:
  - "앱 실행 시 하얀 화면 없이 MainTabView가 나타나야 함"

tests:
  manual: ["App Launch Test", "Preview Stability Test"]

dependencies: []
estimated_effort: "M"
priority: "Critical"
agent_profile: ["ios-developer"]
```

