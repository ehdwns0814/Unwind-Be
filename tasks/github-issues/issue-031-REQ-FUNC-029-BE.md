# [BE-010] 통계 데이터 수집 API

**Epic:** EPIC_STATS  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-02-12  
**Due Date:** 2026-02-13  
**Dependencies:** BE-004

---

## 목적 및 요약
- **목적**: 사용자의 집중 데이터를 수집하여 분석 기반을 마련한다.
- **요약**: `POST /api/stats/completion` 등을 통해 완료 여부, 집중 시간 등을 수신하고 `DailyStatistics` 테이블을 갱신한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-029, REQ-FUNC-030
- **Component**: Backend API

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Aggregation**: 날짜별 `DailyStatistics` 레코드 생성 또는 업데이트.
- **Increment**: `totalFocusTime += duration`, `completedSchedules += 1`.
- **ForceQuit**: 강제 종료 이벤트 수신 시 `forceQuitCount += 1`.

## Definition of Done (DoD)

- [ ] **Entity**: DailyStatistics Entity가 생성되어야 한다.
- [ ] **UPSERT**: 동일 날짜 통계가 누적되어야 한다.
- [ ] **API**: POST /api/stats/completion이 정상 동작해야 한다.
- [ ] **Data**: 사용자의 일별 통계가 누적되어야 한다.

## 구현 힌트

- DailyStatistics Entity 및 Repo 생성
- StatsService 구현 (UPSERT 로직)
- API Endpoint 구현

## 테스트

- **Integration**: Stats Accumulation

---

**Labels:** `backend`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

