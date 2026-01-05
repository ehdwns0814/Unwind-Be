# [BE-001] User Entity 데이터 모델링

**Epic:** EPIC_AUTH  
**Priority:** Must  
**Effort:** S  
**Start Date:** 2026-01-30  
**Due Date:** 2026-01-30  
**Dependencies:** None (Backend 시작 작업)

---

## 목적 및 요약
- **목적**: 사용자 및 인증 정보를 저장하기 위한 DB 구조를 정의한다.
- **요약**: `User` 엔티티와 `RefreshToken` 저장소(Redis 또는 DB)를 설계한다.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-024 (Data Layer)
- **Component**: Backend DB

## Sub-Tasks (구현 상세)

### 데이터 모델
- **Schema**: `users` 테이블 생성 DDL
- **Entity**: 
  - `email`, `passwordHash`, `role` 필드 포함
  - `BaseTimeEntity`(createdAt, updatedAt) 상속
- **Index**: email unique index 필수

## Definition of Done (DoD)

- [ ] **Schema**: `users` 테이블 생성 DDL이 작성되어야 한다. (email unique index 필수)
- **Entity**:
  - [ ] `email`, `passwordHash`, `role` 필드 포함.
  - [ ] `BaseTimeEntity`(createdAt, updatedAt) 상속.
- [ ] **Migration**: Flyway/Liquibase 마이그레이션 스크립트 작성.

## 구현 힌트

- JPA Entity 정의
- BaseTimeEntity 구현 (@EntityListeners(AuditingEntityListener.class))
- DDL 스크립트 작성

## 테스트

- **Unit**: Entity Mapping Test
- **Integration**: DB 생성 확인

---

**Labels:** `backend`, `must`, `phase-1`  
**Milestone:** v1.0-MVP

