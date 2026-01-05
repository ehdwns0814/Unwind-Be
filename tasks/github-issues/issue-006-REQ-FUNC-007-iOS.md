# [iOS-006] 앱 차단 화면 (Shield) 커스터마이징

**Epic:** EPIC_FOCUS_MODE  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-01-11  
**Due Date:** 2026-01-12  
**Dependencies:** iOS-005

---

## 목적 및 요약
- **목적**: 차단된 앱 실행 시 사용자에게 동기를 부여하거나 현재 상황을 안내하는 커스텀 화면을 제공한다.
- **요약**: `ShieldConfigurationDataSource`를 구현하여 차단 화면의 아이콘, 문구, 배경색 등을 커스터마이징한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-007
- **Component**: iOS App Extension (ShieldConfiguration)

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **DataSource**: `ShieldConfigurationExtension` 타겟 생성.
- **Config**: 현재 실행 중인 스케줄 이름 및 남은 시간(가능하다면) 표시.
  *(iOS 제약상 Shield 내에서 실시간 타이머는 어려울 수 있으므로 정적 멘트 위주 구성)*
- **UI**: 친근하지만 단호한 톤의 메시지 ("지금은 집중할 시간이에요!").

## Definition of Done (DoD)

- [ ] **Extension**: ShieldConfigurationExtension 타겟이 생성되어야 한다.
- [ ] **Custom Shield**: 차단 앱 실행 시 기본 시스템 화면 대신 커스텀 화면이 떠야 한다.
- [ ] **Message**: 사용자 친화적인 메시지가 표시되어야 한다.

## 구현 힌트

- ShieldConfigurationExtension 타겟 추가
- ShieldConfigurationDataSource 프로토콜 구현
- 메시지 및 디자인 Asset 적용

## 테스트

- **Manual**: 실기기 테스트

---

**Labels:** `ios`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

