# [iOS-004] 권한 요청 플로우 구현

**Epic:** EPIC_SETTINGS  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-08  
**Due Date:** 2026-01-08  
**Dependencies:** iOS-003

---

## 목적 및 요약
- **목적**: 앱의 핵심 기능인 차단을 사용하기 위해 필수 권한을 유도한다.
- **요약**: `FamilyControls.requestAuthorization()`을 호출하여 Screen Time 권한을 요청하고, 거부 시 안내 UI를 표시한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-014, REQ-FUNC-022
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Request**: 앱 최초 실행 시 또는 기능 사용 시점에 권한 요청
- **Check**: `AuthorizationCenter.shared.authorizationStatus` 확인
- **UI**: 
  - 권한 거부 시 PermissionRequestView (FullScreenCover) 표시
  - "설정으로 이동" 버튼 제공 (`UIApplication.openSettingsURLString`)

## Definition of Done (DoD)

- [ ] **Authorization**: 권한 요청 팝업이 정상적으로 표시되어야 한다.
- [ ] **Error Handling**: 권한 거부 시 안내 화면이 표시되어야 한다.
- [ ] **Navigation**: "설정으로 이동" 버튼이 시스템 설정 앱을 열어야 한다.
- [ ] **State**: 권한 허용 후 앱으로 돌아오면 정상 화면이 표시되어야 한다.

## 구현 힌트

- PermissionManager 구현
- UI: PermissionView 구현
- 권한 상태 체크 로직

## 테스트

- **Manual**: 권한 거부 상태 테스트, 권한 허용 후 재진입 테스트

---

**Labels:** `ios`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

