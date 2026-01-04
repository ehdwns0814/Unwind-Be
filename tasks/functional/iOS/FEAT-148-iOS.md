# FEAT-148-iOS: 실기기 Screen Time API 통합 및 프로덕션 안정화

## 1. 목적 및 요약
- **목적**: 유료 개발자 계정 확보 후, 현재 적용된 Mock(가짜) 모드를 제거하고 실제 Screen Time API(FamilyControls)를 통합하여 프로덕션 수준의 앱 차단 기능을 완성한다.
- **요약**: `#if DEBUG` 블록으로 격리된 로직을 실제 API 호출로 전환하고, 실기기에서의 동작을 검증한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-148
- **Component**: iOS App (Screentime, ManagedSettings)

## 3. Sub-Tasks (구현 상세)

### 3.1 Mock 모드 제거 및 실제 API 전환
- **ScreentimeManager**: `#if DEBUG` 블록을 제거하고 `AuthorizationCenter.shared`를 직접 호출하도록 복구.
- **PenaltyManager**: 권한 상태 체크 시 실제 `AuthorizationCenter` 상태를 반영하도록 수정.

### 3.2 실기기 환경 검증 (Production Readiness)
- **Entitlements**: `Family Controls` Capability가 정식으로 등록되었는지 확인.
- **Shield Extension**: 실제 기기에서 앱 차단 화면(Shield)이 정상적으로 노출되는지 테스트.
- **ManagedSettings**: `ManagedSettingsStore`를 통한 앱 차단/해제가 실시간으로 작동하는지 확인.

### 3.3 예외 처리 및 UX 개선
- **Denied Status**: 사용자가 권한을 거부했을 때 `PermissionRequestView`가 적절히 안내되는지 확인.
- **Revoked Status**: 사용 중에 권한이 취소되었을 때의 페널티 로직 안정성 확보.

## 4. 메타데이터 (YAML)

```yaml
task_id: "FEAT-148-iOS"
title: "실기기 Screen Time API 통합"
type: "feature"
epic: "CORE_FUNCTION"
req_ids: ["REQ-FUNC-148"]
component: ["ios-app", "screentime"]

inputs:
  fields: []

outputs:
  success: { system: "Real Screen Time API integrated and tested on physical device" }

steps_hint:
  - "Remove #if DEBUG blocks from ScreentimeManager"
  - "Test on physical device with paid developer program membership"
  - "Verify Shield UI and ManagedSettings on real device"

preconditions:
  - "Paid Apple Developer Program membership"
  - "Xcode 16 / iOS 18.x"

postconditions:
  - "App should successfully block selected apps on a physical device"

tests:
  manual: ["Physical Device Blocking Test", "Permission Denial Recovery Test"]

dependencies: ["FIX-BUG-147-iOS"]
estimated_effort: "M"
priority: "Must"
agent_profile: ["ios-developer"]
```

