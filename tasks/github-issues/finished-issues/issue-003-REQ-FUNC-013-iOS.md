# [iOS-003] 차단 앱 관리 (FamilyActivityPicker)

**Epic:** EPIC_SETTINGS  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-01-06  
**Due Date:** 2026-01-07  
**Dependencies:** None (병렬 실행 가능)

---

## 목적 및 요약
- **목적**: 사용자가 집중 시 차단할 방해 요소들을 직접 고를 수 있게 한다.
- **요약**: `FamilyActivityPicker`를 호출하여 앱, 카테고리, 웹사이트를 선택하고 선택된 토큰을 `UserDefaults`에 저장한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-015
- **Component**: iOS App (FamilyControls)

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Picker**: `FamilyActivityPicker(selection: $model.selection)` 표시.
- **Save**: 선택된 `FamilyActivitySelection` 객체를 인코딩하여 저장.

## Definition of Done (DoD)

- [ ] **UI**: 설정 화면에서 "차단 앱 선택" 버튼이 동작해야 한다.
- [ ] **Picker**: FamilyActivityPicker가 정상적으로 열려야 한다.
- [ ] **Persistence**: 선택한 앱 정보가 저장되고 재실행 시 유지되어야 한다.
- [ ] **Integration**: 선택한 앱들이 차단 로직(iOS-004)에 반영되어야 한다.

## 구현 힌트

- ShieldSettingsViewModel 구현 (Selection 상태 관리)
- ShieldSettingsView 구현 (Picker 호출)
- Selection Encoder/Decoder 구현 (UserDefaults 저장용)

## 테스트

- **Manual**: Picker 열기 및 선택 저장 테스트

---

**Labels:** `ios`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

