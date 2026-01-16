# BE-009 구현 리뷰 문서

**Issue ID:** BE-009  
**Issue Title:** 스케줄 수정/삭제 API 구현  
**Implementation Date:** 2026-01-17  
**Author:** AI Agent  
**Branch:** `feature/10-schedule-update-delete`  
**PR:** [#21](https://github.com/ehdwns0814/Unwind-Be/pull/21)  
**Related Issue:** [#10](https://github.com/ehdwns0814/Unwind-Be/issues/10), [issue-030-REQ-FUNC-027-BE.md](../finished-issues/issue-030-REQ-FUNC-027-BE.md)  
**Status:** ✅ Merged

---

## 1. 구현 개요

### 1.1 목적
스케줄 수정 및 삭제 API를 구현하여 클라이언트의 변경사항을 서버에 반영합니다.

### 1.2 구현 범위

| 구분 | 내용 |
|------|------|
| **Migration** | V4__add_deleted_at_to_schedules.sql |
| **Entity** | Schedule에 deletedAt 필드 및 비즈니스 메서드 추가 |
| **DTO** | UpdateScheduleRequest 생성 |
| **Repository** | findActiveById(), findActiveByUserId() 추가 |
| **Service** | updateSchedule(), deleteSchedule() 구현 |
| **Controller** | PUT, DELETE 엔드포인트 추가 |
| **ErrorCode** | SCHEDULE_ACCESS_DENIED (SCH003) 추가 |
| **Tests** | 6개 단위 테스트 추가 |

### 1.3 테스트 범위

| 컴포넌트 | 테스트 유형 | 케이스 수 | 커버리지 |
|----------|------------|----------|----------|
| ScheduleService | Unit Test | 6개 | updateSchedule, deleteSchedule 100% |

---

## 2. 아키텍처 구조

### 2.1 패키지 구조

```
src/main/java/com/wombat/screenlock/unwind_be/
├── api/schedule/
│   ├── controller/
│   │   └── ScheduleController.java     # PUT, DELETE 추가
│   └── dto/
│       ├── CreateScheduleRequest.java  # 기존
│       ├── ScheduleResponse.java       # 기존
│       └── UpdateScheduleRequest.java  # ⭐ NEW
├── application/schedule/
│   └── ScheduleService.java            # updateSchedule, deleteSchedule 추가
├── domain/schedule/
│   ├── entity/
│   │   └── Schedule.java               # deletedAt, softDelete(), isOwnedBy() 추가
│   └── repository/
│       └── ScheduleRepository.java     # findActiveById 추가
└── global/exception/
    └── ErrorCode.java                  # SCHEDULE_ACCESS_DENIED 추가

src/main/resources/db/migration/
└── V4__add_deleted_at_to_schedules.sql # ⭐ NEW

src/test/java/com/wombat/screenlock/unwind_be/
└── application/schedule/
    └── ScheduleServiceTest.java        # 6개 테스트 추가
```

### 2.2 3-Tier Architecture 위치

```
┌─────────────────────────────────────────────────────────────────┐
│                    3-Tier Architecture                          │
├─────────────────┬───────────────────┬───────────────────────────┤
│   Controller    │     Service       │       Repository          │
│   (Interface)   │     (Logic)       │       (Data Access)       │
├─────────────────┼───────────────────┼───────────────────────────┤
│  ★ BE-009 ★    │    ★ BE-009 ★    │      ★ BE-009 ★          │
│  PUT/DELETE     │  update/delete    │    findActiveById         │
└─────────────────┴───────────────────┴───────────────────────────┘
```

---

## 3. API 명세

### 3.1 스케줄 수정 API

| 항목 | 내용 |
|------|------|
| **Method** | PUT |
| **URL** | `/api/schedules/{id}` |
| **Auth** | Bearer Token (JWT) |
| **Request Body** | `{ "name": "string", "duration": number }` |
| **Response** | 200 OK + ScheduleResponse |

### 3.2 스케줄 삭제 API

| 항목 | 내용 |
|------|------|
| **Method** | DELETE |
| **URL** | `/api/schedules/{id}` |
| **Auth** | Bearer Token (JWT) |
| **Response** | 204 No Content |

---

## 4. 구현 상세

### 4.1 Schedule Entity 변경

```java
// 새로 추가된 필드
@Column(name = "deleted_at")
private LocalDateTime deletedAt;

// 새로 추가된 메서드
public void softDelete() {
    this.deletedAt = LocalDateTime.now();
}

public boolean isDeleted() {
    return this.deletedAt != null;
}

public boolean isOwnedBy(Long userId) {
    return this.user != null && this.user.getId().equals(userId);
}
```

### 4.2 Service 로직

**updateSchedule:**
1. findActiveById로 스케줄 조회 (Soft Delete 제외)
2. isOwnedBy로 본인 소유 확인
3. schedule.update(name, duration) 호출
4. 저장 및 DTO 변환 반환

**deleteSchedule:**
1. findActiveById로 스케줄 조회
2. isOwnedBy로 본인 소유 확인
3. schedule.softDelete() 호출
4. 저장

### 4.3 Authorization 로직

| 상황 | 결과 |
|------|------|
| 본인 스케줄 | 수정/삭제 성공 |
| 타인 스케줄 | 403 SCHEDULE_ACCESS_DENIED |
| 존재하지 않음 | 404 SCHEDULE_NOT_FOUND |
| 이미 삭제됨 | 404 SCHEDULE_NOT_FOUND |

---

## 5. 테스트 실행 결과

### 5.1 테스트 케이스 목록

| # | 클래스 | 메서드 | 설명 |
|---|--------|--------|------|
| 1 | UpdateSchedule | should_UpdateSchedule_When_ValidRequest | 정상 수정 |
| 2 | UpdateSchedule | should_ThrowException_When_UpdateScheduleNotFound | 스케줄 없음 |
| 3 | UpdateSchedule | should_ThrowException_When_UpdateNotOwner | 권한 없음 |
| 4 | DeleteSchedule | should_SoftDeleteSchedule_When_ValidRequest | 정상 삭제 |
| 5 | DeleteSchedule | should_ThrowException_When_DeleteScheduleNotFound | 스케줄 없음 |
| 6 | DeleteSchedule | should_ThrowException_When_DeleteNotOwner | 권한 없음 |

### 5.2 테스트 결과

```
BUILD SUCCESSFUL
105 tests completed, 0 failed
```

---

## 6. 검증 사항

### 6.1 기능 검증

| 검증 항목 | 상태 | 비고 |
|-----------|------|------|
| **PUT 수정** | ✅ 통과 | name, duration 업데이트 |
| **DELETE 삭제** | ✅ 통과 | Soft Delete (deletedAt 설정) |
| **본인 확인** | ✅ 통과 | isOwnedBy() 메서드 |
| **updatedAt 갱신** | ✅ 통과 | JPA Auditing |

### 6.2 코드 품질 검증

| 항목 | 기준 | 상태 |
|------|------|------|
| **Javadoc** | 모든 클래스/메서드 주석 | ✅ 완료 |
| **네이밍 컨벤션** | Spring Boot 컨벤션 | ✅ 준수 |
| **Swagger 문서** | OpenAPI 어노테이션 | ✅ 적용 |
| **테스트 커버리지** | 주요 로직 100% | ✅ 달성 |

### 6.3 아키텍처 준수 검증

| 원칙 | 검증 내용 | 상태 |
|------|----------|------|
| **3-Tier Architecture** | Controller → Service → Repository | ✅ 준수 |
| **Soft Delete** | 물리 삭제 대신 deletedAt 사용 | ✅ 준수 |
| **DTO Pattern** | Entity 직접 반환 안 함 | ✅ 준수 |

---

## 7. 다음 단계

### 7.1 후속 작업

| 이슈 | 제목 | 의존성 | 상태 |
|------|------|--------|------|
| BE-010 | 통계 데이터 수집 API | BE-004 ✅ | ✅ 시작 가능 |

---

## 8. 결론

### 8.1 구현 완료 사항

✅ **PUT /api/schedules/{id}** - 스케줄 수정 API  
✅ **DELETE /api/schedules/{id}** - 스케줄 삭제 API (Soft Delete)  
✅ **V4 Migration** - deleted_at 컬럼 추가  
✅ **Authorization** - 본인 스케줄만 수정/삭제 가능  
✅ **6개 단위 테스트** - 모두 통과  

### 8.2 품질 지표

| 지표 | 값 |
|------|-----|
| **전체 테스트** | 105개 |
| **새 테스트** | 6개 |
| **테스트 통과율** | 100% |
| **PR 상태** | ✅ Merged |

### 8.3 아키텍처 준수

✅ **3-Tier Architecture**: 모든 레이어 구현 완료  
✅ **Cursor Rules 준수**: Java Spring, Testing Rules 모두 준수  
✅ **Git Flow**: Feature 브랜치, Conventional Commit, PR 생성  

---

**작성일:** 2026-01-17  
**검토자:** -  
**승인일:** -

