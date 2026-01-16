# [BE-010] 통계 데이터 수집 API

**Epic:** EPIC_STATS  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-02-12  
**Due Date:** 2026-02-13  
**Dependencies:** BE-004 (로그인 및 토큰 갱신)  
**Difficulty:** 중 (UPSERT 로직 구현 필요)

---

## 목적 및 요약

- **목적**: 사용자의 집중 데이터를 수집하여 분석 기반을 마련한다.
- **요약**: `POST /api/stats/completion`, `POST /api/stats/force-quit`, `GET /api/stats/summary` API를 통해 완료 여부, 집중 시간, 강제 종료 횟수 등을 수집하고 `DailyStatistics` 테이블을 갱신한다.

## 관련 스펙 (SRS)

- **ID**: REQ-FUNC-029, REQ-FUNC-030
- **Component**: Backend API
- **인수 기준**:
  - REQ-FUNC-029: 스케줄 완료 시 사용자 ID, 달성 여부, 집중 시간을 서버에 전송
  - REQ-FUNC-030: 앱 강제 종료 횟수를 카운트하고 서버에 전송

---

## API 명세

### 1. POST /api/stats/completion - 완료 통계 전송

| 항목 | 값 |
|------|----|
| **Method** | POST |
| **URL** | `/api/stats/completion` |
| **Auth** | Required (Bearer Token) |
| **Content-Type** | application/json |

#### Request Body

```json
{
  "scheduleId": "123e4567-e89b-12d3-a456-426614174000",
  "completed": true,
  "focusTime": 1800,
  "allInMode": false,
  "date": "2026-02-12"
}
```

#### Response Body (Success - 200 OK)

```json
{
  "success": true,
  "data": {
    "recorded": true,
    "dailyStats": {
      "date": "2026-02-12",
      "totalSchedules": 5,
      "completedSchedules": 3,
      "totalFocusTime": 5400,
      "completionRate": 0.6
    }
  },
  "error": null
}
```

---

### 2. POST /api/stats/force-quit - 강제 종료 카운트

| 항목 | 값 |
|------|----|
| **Method** | POST |
| **URL** | `/api/stats/force-quit` |
| **Auth** | Required (Bearer Token) |

#### Request Body

```json
{
  "timestamp": "2026-02-12T14:30:00Z"
}
```

#### Response Body (Success - 200 OK)

```json
{
  "success": true,
  "data": {
    "recorded": true,
    "forceQuitCount": 2
  },
  "error": null
}
```

---

### 3. GET /api/stats/summary - 사용자 통계 조회

| 항목 | 값 |
|------|----|
| **Method** | GET |
| **URL** | `/api/stats/summary` |
| **Auth** | Required (Bearer Token) |

#### Response Body (Success - 200 OK)

```json
{
  "success": true,
  "data": {
    "currentStreak": 7,
    "longestStreak": 14,
    "weeklyCompletionRate": 0.75,
    "monthlyCompletionRate": 0.68,
    "totalFocusTimeThisWeek": 18000,
    "totalFocusTimeThisMonth": 72000,
    "recentDays": [...]
  },
  "error": null
}
```

---

## 파일 생성/수정 목록

### 신규 생성 (이번 구현)
1. `api/stats/controller/StatsController.java`
2. `api/stats/dto/CompletionRequest.java`
3. `api/stats/dto/CompletionResponse.java`
4. `api/stats/dto/ForceQuitRequest.java`
5. `api/stats/dto/ForceQuitResponse.java`
6. `api/stats/dto/DailyStatsDto.java`
7. `api/stats/dto/RecentDayDto.java`
8. `api/stats/dto/StatsSummaryResponse.java`
9. `application/stats/StatsService.java`

### 기존 구현 (이전 마이그레이션)
1. `domain/stats/entity/DailyStatistics.java`
2. `domain/stats/entity/DailyStatus.java`
3. `domain/stats/repository/DailyStatisticsRepository.java`
4. `resources/db/migration/V3__create_daily_statistics_table.sql`

---

## Definition of Done (DoD)

- [x] **Entity**: DailyStatistics Entity가 생성되어야 한다.
- [x] **Migration**: Flyway V3 마이그레이션 스크립트 작성
- [x] **Repository**: DailyStatisticsRepository 구현
- [x] **Service**: StatsService UPSERT 로직 구현
- [x] **Controller**: StatsController 3개 엔드포인트 구현
- [x] **DTO**: Request/Response DTO 생성 및 Validation
- [x] **UPSERT**: 동일 날짜 통계가 누적되어야 한다.
- [x] **API**: POST /api/stats/completion 정상 동작
- [x] **API**: POST /api/stats/force-quit 정상 동작
- [x] **API**: GET /api/stats/summary 정상 동작
- [x] **Test**: 단위 테스트 작성
- [x] **Build**: ./gradlew build 성공

---

**Labels:** `backend`, `must`, `phase-3`  
**Milestone:** v1.0-MVP
**PR:** #22
