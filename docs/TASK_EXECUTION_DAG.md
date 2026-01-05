# Task Execution Strategy & Dependency DAG

**문서 버전:** 1.1  
**작성일:** 2026-01-05  
**프로젝트:** Unwind - Backend & iOS Application

---

## 1. 개요

### 1.1 목적
본 문서는 Unwind 프로젝트의 전체 작업(Task)을 두 개의 병렬 실행 흐름으로 분리하여, 효율적인 개발 전략과 의존성 구조를 정의합니다.

### 1.2 실행 전략 (Overview)

```
┌─────────────────────────────────────────────────────────────────┐
│                    Unwind 프로젝트 실행 전략                      │
└─────────────────────────────────────────────────────────────────┘

   Flow 1: Frontend UI PoC              Flow 2: Backend Core & AI
   ═════════════════════════           ═══════════════════════════
   📱 iOS Client (Jan 3-26)             🖥️  Spring Boot (Jan 30-Feb 13)
   
   ┌─────────────────────┐              ┌─────────────────────┐
   │ Phase 1: Foundation │              │ Phase 1: Auth       │
   │   (Jan 3-5)         │              │   (Jan 30-Feb 4)    │
   └─────────────────────┘              └─────────────────────┘
           ↓                                      ↓
   ┌─────────────────────┐              ┌─────────────────────┐
   │ Phase 2: Focus Mode │              │ Phase 2: CRUD       │
   │   (Jan 6-16)        │              │   (Feb 5-8)         │
   └─────────────────────┘              └─────────────────────┘
           ↓                                      ↓
   ┌─────────────────────┐              ┌─────────────────────┐
   │ Phase 3: Stats      │              │ Phase 3: Sync       │
   │   (Jan 17-26)       │              │   (Feb 9-11)        │
   └─────────────────────┘              └─────────────────────┘
           ↓                                      ↓
           └──────────────┐    ┌─────────────────┘
                          ↓    ↓
                  ┌───────────────────┐
                  │ Integration Test  │
                  │   (Feb 14-20)     │
                  └───────────────────┘
```

### 1.3 병렬 실행 원칙
- **Flow 1 (iOS):** 로컬 저장소(UserDefaults) 기반으로 UI/UX 및 핵심 로직(타이머, 차단) 선행 개발.
- **Flow 2 (Backend):** 데이터 모델링 및 API 개발 후반 투입.
- 각 플로우 완료 후 통합 테스트 수행.

---

## 2. Flow 1: Frontend UI PoC (iOS)

### 2.1 목표
- **기간:** 2026-01-03 ~ 2026-01-26 (24일)
- **목적:** 사용자 인터페이스 PoC 완성 및 로컬 기능 검증

### 2.2 Epic 분류
```yaml
EPIC_SCHEDULE_MGMT:  # 스케줄 생성, 조회, 수정, 삭제
  - REQ-FUNC-001-iOS, 002, 003, 004, 005
EPIC_FOCUS_MODE:     # 집중 모드 실행 및 앱 차단
  - REQ-FUNC-006-iOS ~ 012
EPIC_STATS:          # 통계 및 성과 추적
  - REQ-FUNC-018-iOS ~ 023
EPIC_SETTINGS:       # 앱 설정 및 사용자 경험
  - REQ-FUNC-013-iOS ~ 017
```

### 2.3 일정 및 의존성

#### 2.3.1 일정 (Gantt Chart)
```mermaid
gantt
    title iOS Development Timeline
    dateFormat YYYY-MM-DD
    section Phase 1
    스케줄 생성 UI           :a1, 2026-01-03, 2d
    날짜 탭 내비게이션        :a2, after a1, 1d
    section Phase 2
    차단 앱 설정            :b1, 2026-01-06, 2d
    권한 요청 플로우         :b2, after b1, 1d
    개별 스케줄 실행         :b3, after a2, 2d
    올인 모드               :b4, after b3, 2d
    수동 중단 처리          :b5, after b3, 1d
    타이머 UI               :b6, after b4 b5, 2d
    백그라운드 알림          :b7, after b6, 1d
    section Phase 3
    집중 시간 통계          :c1, 2026-01-17, 2d
    스트릭 추적             :c2, after c1, 2d
    성공률 통계             :c3, after c1, 2d
    권한 해제 패널티        :c4, after c2, 2d
    통계 그래프             :c5, after c2 c3, 2d
```

#### 2.3.2 의존성 그래프 (DAG)
```mermaid
graph TD
    %% === Phase 1: Foundation (Day 1-5) ===
    A[REQ-FUNC-001-iOS<br/>스케줄 생성 UI<br/>Jan 3-4] --> B[REQ-FUNC-002-iOS<br/>최근 항목 빠른 추가<br/>Not Scheduled]
    A --> C[REQ-FUNC-003-iOS<br/>날짜 탭 내비게이션<br/>Jan 5]
    A --> D[REQ-FUNC-004-iOS<br/>스케줄 수정 UI<br/>Not Scheduled]
    A --> E[REQ-FUNC-005-iOS<br/>스케줄 삭제 UI<br/>Not Scheduled]
    
    %% === Phase 2: Focus Mode (Day 6-14) ===
    C --> F[REQ-FUNC-006-iOS<br/>개별 스케줄 실행<br/>Jan 9-10]
    F --> G[REQ-FUNC-007-iOS<br/>올인 모드<br/>Jan 11-12]
    F --> H[REQ-FUNC-008-iOS<br/>수동 중단 처리<br/>Jan 13]
    G --> I[REQ-FUNC-009-iOS<br/>타이머 UI<br/>Jan 14-15]
    H --> I
    
    I --> J[REQ-FUNC-010-iOS<br/>백그라운드 알림<br/>Jan 16]
    
    %% === Phase 3: Settings (Day 7-8, Parallel) ===
    A --> K[REQ-FUNC-013-iOS<br/>차단 앱 설정<br/>Jan 6-7]
    K --> L[REQ-FUNC-014-iOS<br/>권한 요청 플로우<br/>Jan 8]
    K --> M[REQ-FUNC-015-iOS<br/>완료 축하 메시지<br/>Not Scheduled]
    
    %% === Phase 4: Stats & Streak (Day 15-24) ===
    A --> N[REQ-FUNC-018-iOS<br/>집중 시간 통계<br/>Jan 17-18]
    N --> O[REQ-FUNC-019-iOS<br/>스트릭 추적<br/>Jan 19-20]
    O --> P[REQ-FUNC-020-iOS<br/>통계 그래프<br/>Jan 25-26]
    
    N --> Q[REQ-FUNC-021-iOS<br/>성공률 통계<br/>Jan 21-22]
    Q --> P
    
    O --> R[REQ-FUNC-022-iOS<br/>권한 해제 패널티<br/>Jan 23-24]
    R --> S[REQ-FUNC-023-iOS<br/>스트릭 리셋 로직<br/>Not Scheduled]
    
    %% === Additional Features (Parallel) ===
    L --> T[REQ-FUNC-016-iOS<br/>Shield 설정<br/>Not Scheduled]
    M --> U[REQ-FUNC-017-iOS<br/>완료 후 휴식 모드<br/>Not Scheduled]
    
    %% === Phase Groupings ===
    classDef phase1 fill:#e1f5ff,stroke:#0066cc,stroke-width:2px
    classDef phase2 fill:#fff4e1,stroke:#ff9800,stroke-width:2px
    classDef phase3 fill:#f3e5f5,stroke:#9c27b0,stroke-width:2px
    classDef phase4 fill:#e8f5e9,stroke:#4caf50,stroke-width:2px
    classDef optional fill:#f5f5f5,stroke:#9e9e9e,stroke-width:1px,stroke-dasharray: 5 5
    
    class A,B,C,D,E phase1
    class F,G,H,I,J phase2
    class K,L,M,T phase3
    class N,O,P,Q,R,S,U phase4
    class B,D,E,M,S,T,U optional
```

### 2.4 실행 순서 (Critical Path)
*세부 Task 목록은 생략 (Gantt Chart 및 DAG 참조)*

---

## 3. Flow 2: Backend Core & AI Implementation

### 3.1 목표
- **기간:** 2026-01-30 ~ 2026-02-13 (15일)
- **목적:** RESTful API 구현 및 데이터 영속성 확보

### 3.2 Epic 분류
```yaml
EPIC_AUTH:          # 사용자 인증 (DB-024, API-024, LOGIC-024, FUNC-025-BE)
EPIC_SCHEDULE_MGMT: # 스케줄 관리 (DB-001, API-001, LOGIC-001)
EPIC_SYNC:          # 데이터 동기화 (FUNC-026-BE, FUNC-027-BE)
EPIC_STATS:         # 통계 수집 (FUNC-029-BE)
```

### 3.3 일정 및 의존성

#### 3.3.1 일정 (Gantt Chart)
```mermaid
gantt
    title Backend Development Timeline
    dateFormat YYYY-MM-DD
    section Phase 1
    User Entity             :d1, 2026-01-30, 1d
    Auth API 명세           :d2, after d1, 1d
    인증 로직               :d3, after d2, 2d
    로그인 & 토큰 갱신      :d4, after d3, 2d
    section Phase 2
    Schedule Entity         :e1, after d1, 1d
    Schedule API 명세       :e2, after e1, 1d
    스케줄 생성 로직        :e3, after e2, 2d
    section Phase 3
    스케줄 동기화 API       :f1, after e3, 2d
    스케줄 수정삭제 API     :f2, after e3, 1d
    section Phase 4
    통계 데이터 수집        :g1, after d4, 2d
```

#### 3.3.2 의존성 그래프 (DAG)
```mermaid
graph TD
    %% === Phase 1: Auth System (Jan 30 - Feb 4) ===
    A[TASK-DB-024<br/>User Entity<br/>Jan 30] --> B[TASK-API-024<br/>Auth API 명세<br/>Jan 31]
    B --> C[TASK-LOGIC-024<br/>인증 로직<br/>Feb 1-2]
    C --> D[REQ-FUNC-025-BE<br/>로그인 & 토큰 갱신<br/>Feb 3-4]
    
    %% === Phase 2: Schedule CRUD (Feb 5 - Feb 8) ===
    A --> E[TASK-DB-001<br/>Schedule Entity<br/>Feb 5]
    E --> F[TASK-API-001<br/>Schedule API 명세<br/>Feb 6]
    F --> G[TASK-LOGIC-001<br/>스케줄 생성 로직<br/>Feb 7-8]
    
    %% === Phase 3: Sync APIs (Feb 9 - Feb 11) ===
    G --> H[REQ-FUNC-026-BE<br/>스케줄 동기화 API<br/>Feb 9-10]
    G --> I[REQ-FUNC-027-BE<br/>스케줄 수정/삭제 API<br/>Feb 11]
    
    %% === Phase 4: Stats (Feb 12 - Feb 13) ===
    D --> J[REQ-FUNC-029-BE<br/>통계 데이터 수집 API<br/>Feb 12-13]
    
    %% === Cross-Epic Dependencies ===
    D -.Auth Required.-> H
    D -.Auth Required.-> I
    D -.Auth Required.-> J
    
    %% === Phase Groupings ===
    classDef phase1 fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef phase2 fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef phase3 fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef phase4 fill:#e8f5e9,stroke:#388e3c,stroke-width:2px
    
    class A,B,C,D phase1
    class E,F,G phase2
    class H,I phase3
    class J phase4
```

### 3.4 실행 순서 (Critical Path)
*세부 Task 목록은 생략 (Gantt Chart 및 DAG 참조)*

---

## 4. 통합 시나리오 및 리스크

### 4.1 Frontend-Backend 연동
- **회원가입/로그인:** Feb 4 이후 (REQ-FUNC-025-BE 완료 시점)
- **스케줄 동기화:** Feb 10 이후 (REQ-FUNC-026-BE 완료 시점)
- **통계 전송:** Feb 13 이후 (REQ-FUNC-029-BE 완료 시점)

### 4.2 주요 리스크
- **iOS:** Screen Time API 권한 거부 (→ 안내 메시지 강화)
- **Backend:** Delta Sync 충돌 (→ LWW 전략)
- **통합:** 일정 지연 시 Optional 기능 제외하고 Must 기능에 집중

---

## 5. 다음 단계 (Next Actions)

### 5.1 즉시 실행 (Ready to Start)
1. **Flow 1:** REQ-FUNC-001-iOS (Jan 3 시작)
2. **Flow 2:** TASK-DB-024 (Jan 30 시작)

### 5.2 이슈 생성 (참고)
```bash
# iOS 이슈
gh issue create --title "[iOS] REQ-FUNC-001: 스케줄 생성" --body "..." --label "ios,must" --milestone "v1.0-MVP"
# Backend 이슈
gh issue create --title "[Backend] TASK-DB-024: User Entity" --body "..." --label "backend,must" --milestone "v1.0-MVP"
```

---

**참조 문서:** [SRS](./SRS.md), [PRD](./PRD.md)
