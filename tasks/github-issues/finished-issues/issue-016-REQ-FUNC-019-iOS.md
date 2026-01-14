# [iOS-016] 스트릭(Streak) 계산 및 표시

**Epic:** EPIC_STATS  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-19  
**Due Date:** 2026-01-20  
**Dependencies:** iOS-015

---

## 목적 및 요약
- **목적**: 사용자의 지속적인 참여를 유도한다.
- **요약**: 최근 며칠 연속으로 성공(`success`)했는지 계산하여 메인 화면 상단에 "🔥 5일 연속" 배지를 표시한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-019
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Calculation**: 어제부터 역순으로 탐색하며 `status == .success`인 날짜 카운트. (계획 없는 날은 스킵할지 정책 결정 필요 -> SRS상 유지)
- **UI**: 메인 헤더에 아이콘과 숫자 표시.

## Definition of Done (DoD)

- [ ] **Calculation**: 스트릭이 정확히 계산되어야 한다.
- [ ] **UI**: 메인 화면에 배지가 표시되어야 한다.
- [ ] **Update**: 날짜가 바뀌고 성공 시 스트릭이 증가해야 한다.

## 구현 힌트

- StreakCalculator 클래스 구현
- UI: HomeHeaderView 구현

## 테스트

- **Unit**: Streak Calculation Logic

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

