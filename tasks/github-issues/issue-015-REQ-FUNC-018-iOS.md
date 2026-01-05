# [iOS-015] 집중 시간 통계 계산 및 표시

**Epic:** EPIC_STATS  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-01-17  
**Due Date:** 2026-01-18  
**Dependencies:** iOS-001

---

## 목적 및 요약
- **목적**: 사용자가 얼마나 집중했는지 시각적으로 보여준다.
- **요약**: 오늘/주간/월간 총 집중 시간을 계산하여 화면에 표시한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-018 (통계 관련)
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Calculation**: 완료된 스케줄의 duration을 합산
- **UI**: 통계 화면에 시간 표시 (분 또는 시간 단위)

## Definition of Done (DoD)

- [ ] **Calculation**: 집중 시간 합계가 정확해야 한다.
- [ ] **UI**: 통계 화면에 시간이 표시되어야 한다.
- [ ] **Period**: 오늘/주간/월간 필터가 동작해야 한다.

## 구현 힌트

- StatsCalculator 구현
- UI: StatsView 구현

## 테스트

- **Unit**: Stats Calculation Logic

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

