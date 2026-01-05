# [BE-005] Schedule Entity 데이터 모델링

**Epic:** EPIC_SCHEDULE_MGMT  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-02-05  
**Due Date:** 2026-02-05  
**Dependencies:** BE-001

---

## 목적 및 요약
- **목적**: 스케줄 데이터를 영속적으로 저장하기 위한 DB 구조를 정의한다.
- **요약**: `Schedule` 엔티티와 `User` 엔티티 간의 연관관계를 매핑하고, JPA Repository를 구현한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-001 (Data Layer)
- **Component**: Backend DB (MySQL/JPA)

## Sub-Tasks (구현 상세)

### 데이터 모델
- **Schema**: `schedules` 테이블 생성 DDL (인덱스 포함)
- **Entity**:
  - `id` (PK, Auto Inc), `clientId` (UUID, Unique), `name`, `duration` 필드 포함
  - `User`와 다대일(N:1) Lazy Loading 관계 설정
- **Repository**: `findByClientId` 등 기본 조회 메서드 작성

## Definition of Done (DoD)

- [ ] **Schema**: `schedules` 테이블 생성 DDL이 작성되어야 한다. (인덱스 포함)
- **Entity**:
  - [ ] `id` (PK, Auto Inc), `clientId` (UUID, Unique), `name`, `duration` 필드 포함.
  - [ ] `User`와 다대일(N:1) Lazy Loading 관계 설정.
- **Repository**:
  - [ ] `findByClientId` 등 기본 조회 메서드 작성.

## 구현 힌트

- Schedule Entity 작성
- ScheduleRepository 인터페이스 정의
- DDL 마이그레이션 스크립트

## 테스트

- **Unit**: Entity Mapping Test
- **Integration**: Repository Test

---

**Labels:** `backend`, `must`, `phase-2`  
**Milestone:** v1.0-MVP

