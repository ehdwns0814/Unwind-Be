# [iOS-002] 날짜 선택 및 스케줄 목록 필터링

**Epic:** EPIC_SCHEDULE_MGMT  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-05  
**Due Date:** 2026-01-05  
**Dependencies:** iOS-001

---

## 목적 및 요약
- **목적**: 사용자가 오늘 외의 다른 날짜(과거/미래)의 스케줄을 관리할 수 있게 한다.
- **요약**: 메인 화면 상단에 7일간의 날짜 탭(좌우 스와이프)을 제공하고, 선택 시 해당 날짜의 스케줄 목록을 불러온다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-003
- **Component**: iOS App (HomeView)

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **UI**: 오늘을 기준으로 `[-3, +3]`일 또는 주간 캘린더 뷰 제공.
- **Interaction**: 날짜 선택 시 `ViewModel.selectedDate` 변경 및 데이터 리로드.
- **Data**: `Repository.getSchedules(for: Date)` 호출.

### 설정
- 표시 범위: 기본적으로 "오늘"이 중심, 필요 시 무한 스크롤 또는 달력 모달 확장 가능.

## Definition of Done (DoD)

- [ ] **UI**: 날짜 탭이 가로로 스와이프 가능해야 한다.
- [ ] **Filtering**: 날짜 변경 시 해당 일자의 데이터만 표시되어야 한다.
- [ ] **Performance**: 날짜 전환이 즉시(0.3초 이내) 반영되어야 한다.

## 구현 힌트

- DateHelper 유틸리티 (날짜 생성/포맷팅) 구현
- UI: DateStripView (Horizontal Scroll) 구현
- ViewModel: selectedDate 상태 관리 및 필터링 로직

## 테스트

- **Unit**: Date Filtering Logic Test
- **UI**: Swipe/Tap Date -> List Refresh

---

**Labels:** `ios`, `must`, `phase-1`  
**Milestone:** v1.0-MVP

