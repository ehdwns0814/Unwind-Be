# [iOS-009] 타이머 UI 및 백그라운드 알림

**Epic:** EPIC_FOCUS_MODE  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-01-15  
**Due Date:** 2026-01-16  
**Dependencies:** iOS-007, iOS-008

---

## 목적 및 요약
- **목적**: 타이머 화면 UI를 구현하고 백그라운드에서도 진행 상황을 알린다.
- **요약**: 타이머 진행 화면과 백그라운드 로컬 알림을 구현한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-010 (타이머 UI 부분)
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Timer UI**: 원형 프로그레스 바, 남은 시간 표시
- **Background**: 백그라운드 진입 시 로컬 알림 스케줄링
- **Foreground**: 포어그라운드 복귀 시 알림 취소 및 UI 동기화

## Definition of Done (DoD)

- [ ] **UI**: 타이머가 시각적으로 명확하게 표시되어야 한다.
- [ ] **Notification**: 백그라운드에서 완료 시 알림이 와야 한다.
- [ ] **Sync**: 백그라운드 진입 후 복귀 시 시간이 정확해야 한다.

## 구현 힌트

- TimerView UI 구현 (SwiftUI)
- UNUserNotificationCenter 설정
- Background Task 처리

## 테스트

- **UI**: Timer Display
- **Manual**: 백그라운드 테스트

---

**Labels:** `ios`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

