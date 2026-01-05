# [iOS-011] 올인 모드 스케줄 완료 체크 및 진행률

**Epic:** EPIC_ALLIN_MODE  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-17  
**Due Date:** 2026-01-17  
**Dependencies:** iOS-010

---

## 목적 및 요약
- **목적**: 올인 모드 중 사용자가 스케줄을 하나씩 클리어하는 경험을 제공한다.
- **요약**: 사용자가 스케줄 목록에서 "완료" 체크를 할 때마다 진행률(n/m)을 업데이트하고, 남아있는 스케줄이 있는지 확인한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-011
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Interaction**: 리스트 아이템의 체크박스 탭 -> `isCompleted` 토글.
- **Check**: 모든 스케줄이 완료되었는지(`completedCount == totalCount`) 검사.
- **UI**: Progress Bar 또는 "3/5 완료" 텍스트 업데이트.

## Definition of Done (DoD)

- [ ] **Checkbox**: 체크박스 탭 시 완료 상태가 토글되어야 한다.
- [ ] **Progress**: 진행률이 즉시 반영되어야 한다.
- [ ] **Calculation**: 완료 개수가 정확해야 한다.

## 구현 힌트

- ViewModel: toggleCompletion() 메서드 구현
- UI: Checkbox Component 구현
- AllInModeManager: 진행률 계산 로직

## 테스트

- **Unit**: Progress Calculation
- **UI**: Check/Uncheck Interaction

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

