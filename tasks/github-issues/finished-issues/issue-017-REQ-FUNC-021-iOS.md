# [iOS-017] 성공률 통계 계산 및 표시

**Epic:** EPIC_STATS  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-21  
**Due Date:** 2026-01-22  
**Dependencies:** iOS-015

---

## 목적 및 요약
- **목적**: 사용자가 자신의 달성률을 파악한다.
- **요약**: 전체 스케줄 중 성공한 비율(%)을 계산하여 표시한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-021 (성공률)
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Calculation**: `성공 개수 / 전체 개수 * 100`
- **UI**: 통계 화면에 퍼센트 표시

## Definition of Done (DoD)

- [ ] **Calculation**: 성공률이 정확해야 한다.
- [ ] **UI**: 퍼센트가 표시되어야 한다.
- [ ] **Edge Case**: 전체가 0일 때 처리되어야 한다.

## 구현 힌트

- StatsCalculator에 성공률 로직 추가
- UI 업데이트

## 테스트

- **Unit**: Success Rate Calculation

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

