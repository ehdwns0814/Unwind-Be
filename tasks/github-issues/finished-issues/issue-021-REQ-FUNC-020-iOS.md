# [iOS-021] 통계 그래프 (Charts) 구현

**Epic:** EPIC_STATS  
**Priority:** Should  
**Effort:** M  
**Start Date:** 2026-01-25  
**Due Date:** 2026-01-26  
**Dependencies:** iOS-015, iOS-017

---

## 목적 및 요약
- **목적**: 사용자의 성취도를 시각적으로 보여준다.
- **요약**: `Charts` 프레임워크를 사용하여 지난 7일/30일간의 집중 시간 및 성공률을 막대/선 그래프로 표현한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-020
- **Component**: iOS App (Swift Charts)

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Query**: `Repository.getDailyRecords(range: ...)` 호출.
- **Transform**: `[Date: Record]` -> `[ChartDataEntry]` 변환.
- **UI**: 탭(주간/월간) 전환 시 그래프 애니메이션.

## Definition of Done (DoD)

- [ ] **Data Accuracy**: 지난 7일간의 집중 시간 합계가 DB에 저장된 실제 시간과 정확히 일치해야 한다.
- **UI Interaction**:
  - [ ] 주간/월간 탭 전환 시 그래프가 부드럽게 애니메이션되어야 한다.
  - [ ] 데이터가 없는 날짜는 0으로 표시되어야 한다 (누락되지 않음).
- **Visual**: X축(날짜)과 Y축(시간) 레이블이 가독성 있게 표시되어야 한다.

## 구현 힌트

- Swift Charts 라이브러리 활용
- StatsViewModel: 데이터 가공 로직
- StatsView UI 구현

## 테스트

- **UI**: Chart Rendering Check

---

**Labels:** `ios`, `should`, `phase-3`  
**Milestone:** v1.0-MVP

