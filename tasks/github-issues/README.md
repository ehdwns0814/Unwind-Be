# GitHub Issues - Backend Only

## ⚠️ 중요 안내

**iOS 작업 (21개 이슈) 전체가 별도 프로젝트에서 완료되었습니다.**

본 디렉토리의 이슈 문서 중:
- **iOS 이슈 (issue-001 ~ issue-021)**: ✅ 완료됨, **GitHub에 생성 안 함** (참고용 보관)
- **Backend 이슈 (issue-022 ~ issue-031)**: 🔄 진행 예정, **GitHub에 생성해야 함**

---

## 📁 디렉토리 구조

```
tasks/github-issues/
├── README.md                              ← 이 파일
├── ISSUE_EXECUTION_GUIDE.md              ← 상세 실행 가이드
├── ISSUE_LIST.md                         ← 이슈 목록 및 빠른 참조
│
├── issue-001-REQ-FUNC-001-iOS.md         ✅ iOS (참고용, GitHub 생성 안 함)
├── issue-002-REQ-FUNC-003-iOS.md         ✅ iOS
├── ...
├── issue-021-REQ-FUNC-020-iOS.md         ✅ iOS
│
├── issue-022-TASK-DB-024.md              🔄 BE-001 (GitHub에 생성)
├── issue-023-TASK-API-024.md             🔄 BE-002
├── issue-024-TASK-LOGIC-024.md           🔄 BE-003
├── issue-025-REQ-FUNC-025-BE.md          🔄 BE-004
├── issue-026-TASK-DB-001.md              🔄 BE-005
├── issue-027-TASK-API-001.md             🔄 BE-006
├── issue-028-TASK-LOGIC-001.md           🔄 BE-007
├── issue-029-REQ-FUNC-026-BE.md          🔄 BE-008
├── issue-030-REQ-FUNC-027-BE.md          🔄 BE-009
└── issue-031-REQ-FUNC-029-BE.md          🔄 BE-010
```

---

## 🚀 빠른 시작

### 1. Backend 이슈 생성 (필수)

```bash
# Label 생성
gh label create "backend" --color "FF5722"
gh label create "must" --color "D93F0B"
gh label create "phase-1" --color "BFDADC"
gh label create "phase-2" --color "C5DEF5"
gh label create "phase-3" --color "D4C5F9"

# Milestone 생성
gh milestone create "v1.0-MVP" --due-date 2026-02-20

# Backend 이슈 생성 (예시)
gh issue create \
  --title "[BE-001] User Entity 데이터 모델링" \
  --body-file tasks/github-issues/issue-022-TASK-DB-024.md \
  --label "backend,must,phase-1" \
  --milestone "v1.0-MVP"

# ... BE-002 ~ BE-010도 동일하게 생성
```

전체 이슈 생성 스크립트는 `ISSUE_LIST.md`의 "다음 액션" 섹션을 참고하세요.

---

## 📊 Backend 이슈 개요

| Phase | 이슈 | 제목 | 소요 | 시작일 | 의존성 |
|-------|------|------|------|--------|--------|
| 1 | BE-001 | User Entity | S | Jan 30 | None |
| 1 | BE-002 | Auth API 명세 | S | Jan 31 | None |
| 1 | BE-003 | 인증 로직 | L | Feb 1 | BE-001, BE-002 |
| 1 | BE-004 | 로그인/토큰 | M | Feb 3 | BE-003 |
| 2 | BE-005 | Schedule Entity | S | Feb 5 | BE-001 |
| 2 | BE-006 | Schedule API | S | Feb 6 | None |
| 2 | BE-007 | Schedule Service | M | Feb 7 | BE-005, BE-006 |
| 3 | BE-008 | 동기화 API | M | Feb 9 | BE-007 |
| 3 | BE-009 | 수정/삭제 API | S | Feb 11 | BE-007 |
| 3 | BE-010 | 통계 API | M | Feb 12 | BE-004 |

**총 소요 시간:** 약 13-15일

---

## 📖 문서 가이드

### [ISSUE_EXECUTION_GUIDE.md](./ISSUE_EXECUTION_GUIDE.md)
- **내용**: 상세한 실행 순서, 병렬 개발 전략, 코드 충돌 방지
- **대상**: 프로젝트 매니저, 개발 리드
- **사용**: 전체 개발 전략 수립 시

### [ISSUE_LIST.md](./ISSUE_LIST.md)
- **내용**: 이슈 목록, 빠른 참조, 이슈 생성 명령어
- **대상**: 모든 개발자
- **사용**: 이슈 생성 및 일상적인 참조

### 개별 이슈 파일 (issue-NNN-*.md)
- **내용**: 각 이슈의 상세 명세 (DoD, 구현 힌트, 테스트 등)
- **대상**: 해당 이슈 담당 개발자
- **사용**: 이슈 작업 시 참고

---

## ✅ 체크리스트

Backend 개발 시작 전 확인사항:

- [ ] iOS 앱이 완료되었고 API 명세를 확인했는가?
- [ ] Backend 개발 환경이 설정되었는가? (JDK, Gradle, DB)
- [ ] GitHub Label과 Milestone을 생성했는가?
- [ ] Backend 이슈 10개를 GitHub에 생성했는가?
- [ ] 의존관계를 확인하고 시작 순서를 파악했는가?

---

## 🔗 관련 문서

- [TASK_EXECUTION_DAG.md](../../docs/TASK_EXECUTION_DAG.md) - 전체 프로젝트 실행 DAG
- [SRS.md](../../docs/SRS.md) - 소프트웨어 요구사항 명세
- [PRD.md](../../docs/PRD.md) - 제품 요구사항 문서

---

## 💡 Tips

### Backend 개발 우선순위
1. **Phase 1 (Auth)**: BE-001 → BE-002 → BE-003 → BE-004
   - 인증은 모든 API의 기반이므로 최우선
2. **Phase 2 (CRUD)**: BE-005 → BE-006 → BE-007
   - 스케줄 CRUD는 핵심 기능
3. **Phase 3 (Sync & Stats)**: BE-008 → BE-009 → BE-010
   - 동기화와 통계는 부가 기능

### 병렬 개발 가능
- BE-001과 BE-002는 동시 시작 가능
- BE-005와 BE-006도 동시 시작 가능 (BE-001 완료 후)
- BE-010은 BE-004 완료 후 독립적으로 진행 가능

### iOS 연동 주의
- Backend API 개발 시 **iOS가 기대하는 명세를 정확히 준수**
- Request/Response DTO 구조 확인 필수
- 날짜 포맷, 에러 응답 형식 등 세부사항 체크

---

**최종 수정:** 2026-01-05  
**작성자:** Development Team

