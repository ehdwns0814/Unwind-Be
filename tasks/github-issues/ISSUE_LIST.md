# GitHub Issue 목록 및 빠른 참조

**생성일:** 2026-01-05  
**최종 수정:** 2026-01-05 (iOS 완료 반영)  
**총 이슈 수:** 31개 (iOS: 21개 ✅ 완료, Backend: 10개 🔄 진행 예정)

---

## ⚠️ 중요 안내

**iOS 이슈 (iOS-001 ~ iOS-021) 전체가 별도 프로젝트에서 이미 완료되었습니다.**

- **GitHub 이슈 생성 시 Backend 이슈 (BE-001 ~ BE-010)만 생성하세요.**
- iOS 이슈 파일들은 참고용으로 보관됩니다.
- iOS API 연동을 위해 Backend 개발 시 iOS 명세를 확인하세요.

---

## iOS 이슈 목록 (21개) ✅ **완료됨 - 참고용**

> 이 섹션의 이슈들은 GitHub에 생성하지 마세요.

| Issue # | Title | Epic | Priority | Effort | Start Date | Dependencies |
|---------|-------|------|----------|--------|------------|--------------|
| iOS-001 | 스케줄 생성 UI 및 로컬 저장 구현 | SCHEDULE_MGMT | Must | M | 2026-01-03 | None |
| iOS-002 | 날짜 선택 및 스케줄 목록 필터링 | SCHEDULE_MGMT | Must | S | 2026-01-05 | iOS-001 |
| iOS-003 | 차단 앱 관리 (FamilyActivityPicker) | SETTINGS | Must | M | 2026-01-06 | None |
| iOS-004 | 권한 요청 플로우 구현 | SETTINGS | Must | S | 2026-01-08 | iOS-003 |
| iOS-005 | 개별 스케줄 실행 및 앱 차단 연동 | FOCUS_MODE | Must | L | 2026-01-09 | iOS-002 |
| iOS-006 | 앱 차단 화면 커스터마이징 | FOCUS_MODE | Must | M | 2026-01-11 | iOS-005 |
| iOS-007 | 스케줄 수동 포기 및 차단 해제 | FOCUS_MODE | Must | S | 2026-01-13 | iOS-005 |
| iOS-008 | 타이머 종료 및 성공 처리 | FOCUS_MODE | Must | S | 2026-01-14 | iOS-005 |
| iOS-009 | 타이머 UI 및 백그라운드 알림 | FOCUS_MODE | Must | M | 2026-01-15 | iOS-007, iOS-008 |
| iOS-010 | 올인 모드 진입 및 상태 관리 | ALLIN_MODE | Must | M | 2026-01-15 | iOS-005 |
| iOS-011 | 올인 모드 진행률 체크 | ALLIN_MODE | Must | S | 2026-01-17 | iOS-010 |
| iOS-012 | 올인 모드 Shield UI | ALLIN_MODE | Must | M | 2026-01-18 | iOS-006, iOS-010 |
| iOS-013 | 올인 모드 완료 및 자동 차단 해제 | ALLIN_MODE | Must | S | 2026-01-20 | iOS-011 |
| iOS-014 | 올인 모드 수동 포기 | ALLIN_MODE | Must | S | 2026-01-21 | iOS-010 |
| iOS-015 | 집중 시간 통계 계산 및 표시 | STATS | Must | M | 2026-01-17 | iOS-001 |
| iOS-016 | 스트릭 계산 및 표시 | STATS | Must | S | 2026-01-19 | iOS-015 |
| iOS-017 | 성공률 통계 계산 및 표시 | STATS | Must | S | 2026-01-21 | iOS-015 |
| iOS-018 | 스케줄 수정 기능 | SCHEDULE_MGMT | Must | S | 2026-01-22 | iOS-001 |
| iOS-019 | 스케줄 삭제 기능 | SCHEDULE_MGMT | Must | S | 2026-01-23 | iOS-001 |
| iOS-020 | 권한 해제 패널티 | STATS | Must | M | 2026-01-23 | iOS-016 |
| iOS-021 | 통계 그래프 구현 | STATS | Should | M | 2026-01-25 | iOS-015, iOS-017 |

---

## Backend 이슈 목록 (10개) 🔄 **진행 예정 - GitHub에 생성할 이슈들**

> **중요**: 이 섹션의 이슈들만 GitHub에 생성하세요.

| Issue # | Title | Epic | Priority | Effort | Start Date | Dependencies |
|---------|-------|------|----------|--------|------------|--------------|
| BE-001 | User Entity 데이터 모델링 | AUTH | Must | S | 2026-01-30 | None |
| BE-002 | Auth API 명세 (DTO/Controller) | AUTH | Must | S | 2026-01-31 | None |
| BE-003 | 인증 로직 및 보안 설정 | AUTH | Must | L | 2026-02-01 | BE-001, BE-002 |
| BE-004 | 로그인 및 토큰 갱신 | AUTH | Must | M | 2026-02-03 | BE-003 |
| BE-005 | Schedule Entity 데이터 모델링 | SCHEDULE_MGMT | Must | S | 2026-02-05 | BE-001 |
| BE-006 | 스케줄 생성 API 명세 | SCHEDULE_MGMT | Must | S | 2026-02-06 | None |
| BE-007 | 스케줄 생성 서비스 로직 | SCHEDULE_MGMT | Must | M | 2026-02-07 | BE-005, BE-006 |
| BE-008 | 스케줄 동기화 API | SYNC | Must | M | 2026-02-09 | BE-007 |
| BE-009 | 스케줄 수정/삭제 API | SYNC | Must | S | 2026-02-11 | BE-007 |
| BE-010 | 통계 데이터 수집 API | STATS | Must | M | 2026-02-12 | BE-004 |

---

## Epic별 분류

### iOS Epics ✅ (완료됨 - 참고용)

**EPIC_SCHEDULE_MGMT (스케줄 관리): 4개**
- iOS-001, iOS-002, iOS-018, iOS-019

**EPIC_FOCUS_MODE (집중 모드): 5개**
- iOS-005, iOS-006, iOS-007, iOS-008, iOS-009

**EPIC_ALLIN_MODE (올인 모드): 5개**
- iOS-010, iOS-011, iOS-012, iOS-013, iOS-014

**EPIC_STATS (통계): 5개**
- iOS-015, iOS-016, iOS-017, iOS-020, iOS-021

**EPIC_SETTINGS (설정): 2개**
- iOS-003, iOS-004

---

### Backend Epics 🔄 (진행 예정)

**EPIC_AUTH (인증): 4개**
- BE-001, BE-002, BE-003, BE-004

**EPIC_SCHEDULE_MGMT (스케줄 관리): 3개**
- BE-005, BE-006, BE-007

**EPIC_SYNC (동기화): 2개**
- BE-008, BE-009

**EPIC_STATS (통계): 1개**
- BE-010

---

## 병렬 실행 가능 그룹

### Backend 🔄 (진행 예정)

**Group 1 (독립 시작):**
- BE-001, BE-002 (병렬 가능)

**Group 2 (BE-003 완료 후):**
- BE-004 → BE-010 (순차)

**Group 3 (BE-007 완료 후):**
- BE-008 → BE-009 (순차)
- BE-010 (독립 병렬)

---

## 이슈 파일 위치

모든 이슈 문서는 `tasks/github-issues/` 디렉토리에 저장되어 있습니다.

**iOS 이슈 (참고용, GitHub에 생성 안 함):**
```
tasks/github-issues/issue-001-REQ-FUNC-001-iOS.md
tasks/github-issues/issue-002-REQ-FUNC-003-iOS.md
...
tasks/github-issues/issue-021-REQ-FUNC-020-iOS.md
```

**Backend 이슈 (GitHub에 생성할 이슈):**
```
tasks/github-issues/issue-022-TASK-DB-024.md        → BE-001
tasks/github-issues/issue-023-TASK-API-024.md       → BE-002
tasks/github-issues/issue-024-TASK-LOGIC-024.md     → BE-003
tasks/github-issues/issue-025-REQ-FUNC-025-BE.md    → BE-004
tasks/github-issues/issue-026-TASK-DB-001.md        → BE-005
tasks/github-issues/issue-027-TASK-API-001.md       → BE-006
tasks/github-issues/issue-028-TASK-LOGIC-001.md     → BE-007
tasks/github-issues/issue-029-REQ-FUNC-026-BE.md    → BE-008
tasks/github-issues/issue-030-REQ-FUNC-027-BE.md    → BE-009
tasks/github-issues/issue-031-REQ-FUNC-029-BE.md    → BE-010
```

---

## 이슈 생성 명령어 템플릿

### ⚠️ Backend 이슈만 생성하세요 (iOS 이슈는 생성 안 함)

**이슈 생성 방법 1: body-file 사용 (권장)**
```bash
# BE-001
gh issue create \
  --title "[BE-001] User Entity 데이터 모델링" \
  --body-file tasks/github-issues/issue-022-TASK-DB-024.md \
  --label "backend,must,phase-1" \
  --milestone "v1.0-MVP"

# BE-002
gh issue create \
  --title "[BE-002] Auth API 명세 (DTO/Controller)" \
  --body-file tasks/github-issues/issue-023-TASK-API-024.md \
  --label "backend,must,phase-1" \
  --milestone "v1.0-MVP"

# ... BE-003 ~ BE-010도 동일 패턴
```

**이슈 생성 방법 2: body 내용 직접 전달**
```bash
gh issue create \
  --title "[BE-001] User Entity 데이터 모델링" \
  --body "$(cat tasks/github-issues/issue-022-TASK-DB-024.md)" \
  --label "backend,must,phase-1" \
  --milestone "v1.0-MVP"
```

---

## Critical Path (최단 경로)

### Backend Critical Path 🔄
```
BE-001/002 → BE-003 → BE-004 → BE-005/006 → BE-007 → BE-008 → BE-009
```
**소요 시간:** 약 13일

**병렬 실행 시:**
- BE-001과 BE-002 동시 시작
- BE-005와 BE-006 동시 시작
- BE-010은 BE-004 완료 후 독립 실행 가능

---

## 다음 액션

### 1. Label 생성 (필요 시)
```bash
gh label create "backend" --color "FF5722" --description "Backend 관련 이슈"
gh label create "must" --color "D93F0B" --description "필수 구현 기능"
gh label create "phase-1" --color "BFDADC" --description "Phase 1: Auth"
gh label create "phase-2" --color "C5DEF5" --description "Phase 2: CRUD"
gh label create "phase-3" --color "D4C5F9" --description "Phase 3: Sync & Stats"
```

### 2. Milestone 생성
```bash
gh milestone create "v1.0-MVP" --due-date 2026-02-20 --description "Backend MVP 완성"
```

### 3. Backend 이슈 일괄 생성 스크립트

```bash
#!/bin/bash

# BE-001 ~ BE-004 (Phase 1: Auth)
gh issue create --title "[BE-001] User Entity 데이터 모델링" \
  --body-file tasks/github-issues/issue-022-TASK-DB-024.md \
  --label "backend,must,phase-1" --milestone "v1.0-MVP"

gh issue create --title "[BE-002] Auth API 명세 (DTO/Controller)" \
  --body-file tasks/github-issues/issue-023-TASK-API-024.md \
  --label "backend,must,phase-1" --milestone "v1.0-MVP"

gh issue create --title "[BE-003] 인증 로직 및 보안 설정" \
  --body-file tasks/github-issues/issue-024-TASK-LOGIC-024.md \
  --label "backend,must,phase-1" --milestone "v1.0-MVP"

gh issue create --title "[BE-004] 로그인 및 토큰 갱신" \
  --body-file tasks/github-issues/issue-025-REQ-FUNC-025-BE.md \
  --label "backend,must,phase-1" --milestone "v1.0-MVP"

# BE-005 ~ BE-007 (Phase 2: CRUD)
gh issue create --title "[BE-005] Schedule Entity 데이터 모델링" \
  --body-file tasks/github-issues/issue-026-TASK-DB-001.md \
  --label "backend,must,phase-2" --milestone "v1.0-MVP"

gh issue create --title "[BE-006] 스케줄 생성 API 명세" \
  --body-file tasks/github-issues/issue-027-TASK-API-001.md \
  --label "backend,must,phase-2" --milestone "v1.0-MVP"

gh issue create --title "[BE-007] 스케줄 생성 서비스 로직" \
  --body-file tasks/github-issues/issue-028-TASK-LOGIC-001.md \
  --label "backend,must,phase-2" --milestone "v1.0-MVP"

# BE-008 ~ BE-010 (Phase 3: Sync & Stats)
gh issue create --title "[BE-008] 스케줄 동기화 API" \
  --body-file tasks/github-issues/issue-029-REQ-FUNC-026-BE.md \
  --label "backend,must,phase-3" --milestone "v1.0-MVP"

gh issue create --title "[BE-009] 스케줄 수정/삭제 API" \
  --body-file tasks/github-issues/issue-030-REQ-FUNC-027-BE.md \
  --label "backend,must,phase-3" --milestone "v1.0-MVP"

gh issue create --title "[BE-010] 통계 데이터 수집 API" \
  --body-file tasks/github-issues/issue-031-REQ-FUNC-029-BE.md \
  --label "backend,must,phase-3" --milestone "v1.0-MVP"

echo "✅ Backend 이슈 10개 생성 완료!"
```

---

**참고 문서:**
- [ISSUE_EXECUTION_GUIDE.md](./ISSUE_EXECUTION_GUIDE.md) - 상세 실행 가이드
- [TASK_EXECUTION_DAG.md](../../docs/TASK_EXECUTION_DAG.md) - 원본 DAG

---

## 요약

### ✅ 완료된 작업
- iOS 이슈 21개 전체 완료 (별도 프로젝트)
- iOS 앱 개발 완료

### 🔄 진행할 작업
- **Backend 이슈 10개만 GitHub에 생성**
- Backend API 개발 (약 13-15일 소요)
- iOS 앱과 연동 테스트

### ⚠️ 주의사항
1. **iOS 이슈는 GitHub에 생성하지 마세요** (이미 완료됨)
2. **Backend 이슈만 생성**하세요 (BE-001 ~ BE-010)
3. iOS API 명세를 확인하며 개발하세요
4. 의존관계를 지키며 순차적으로 진행하세요

