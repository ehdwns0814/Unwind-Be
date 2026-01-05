# [iOS-014] 올인 모드 수동 포기

**Epic:** EPIC_ALLIN_MODE  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-21  
**Due Date:** 2026-01-21  
**Dependencies:** iOS-010

---

## 목적 및 요약
- **목적**: 사용자가 도저히 지속할 수 없을 때 중단할 수 있게 하되, 실패 기록을 남긴다.
- **요약**: "올인 모드 중단" 버튼 -> 강력한 경고 팝업 -> 확인 시 차단 해제 및 `DailyRecord`에 실패(`Failure`) 기록.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-014
- **Component**: iOS App

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Warning**: "중단하면 오늘은 실패로 기록됩니다." 경고.
- **Action**: 차단 해제, `Database`에 오늘 날짜 `status = .failure` 저장.

## Definition of Done (DoD)

- [ ] **Warning**: 중단 버튼 탭 시 강력한 경고가 표시되어야 한다.
- [ ] **State**: 포기 후 재진입 시 실패 상태가 유지되어야 한다.
- [ ] **Unblock**: 차단이 즉시 해제되어야 한다.

## 구현 힌트

- UI: Give Up Button & Alert
- Repository: updateDailyStatus(.failure)

## 테스트

- **Manual**: 포기 프로세스 확인

---

**Labels:** `ios`, `must`, `phase-3`  
**Milestone:** v1.0-MVP

