# [iOS-012] 올인 모드 Shield UI (진행률 표시)

**Epic:** EPIC_ALLIN_MODE  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-01-18  
**Due Date:** 2026-01-19  
**Dependencies:** iOS-006, iOS-010

---

## 목적 및 요약
- **목적**: 사용자가 현재 올인 모드 중임을 인지하고, 얼마나 남았는지 Shield 화면에서 확인하게 한다.
- **요약**: `ShieldConfigurationDataSource`에서 현재 모드가 "올인 모드"인지 확인하고, 그렇다면 "올인 모드 진행 중 (3/5)"와 같은 문구를 표시한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-012
- **Component**: iOS App (ShieldExtension)

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Shared Storage**: Main App과 Extension 간 데이터 공유 (`App Group` 활용 필요).
- **Display**: `UserDefaults(suiteName: ...)`에서 진행률 읽어와 Shield에 표시.

## Definition of Done (DoD)

- [ ] **App Group**: App Group 설정이 완료되어야 한다.
- [ ] **Shared Data**: Extension에서 진행률을 읽을 수 있어야 한다.
- [ ] **Custom Message**: 올인 모드 중에는 차단 화면 멘트가 달라져야 한다.

## 구현 힌트

- App Group 설정 (Capabilities)
- SharedUserDefaultsManager 구현
- ShieldDataSource: 모드에 따른 분기 처리

## 테스트

- **Manual**: 올인 모드 진입 후 차단 앱 실행 확인

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

