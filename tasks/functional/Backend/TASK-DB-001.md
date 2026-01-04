# TASK-DB-001: 스케줄 도메인 데이터 모델링 (Backend)

## 1. 목적 및 요약
- **목적**: 스케줄 데이터를 영속적으로 저장하기 위한 DB 구조를 정의한다.
- **요약**: `Schedule` 엔티티와 `User` 엔티티 간의 연관관계를 매핑하고, JPA Repository를 구현한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-001 (Data Layer)
- **Component**: Backend DB (MySQL/JPA)

## 3. Definition of Done (DoD)
- [ ] **Schema**: `schedules` 테이블 생성 DDL이 작성되어야 한다. (인덱스 포함)
- **Entity**:
  - [ ] `id` (PK, Auto Inc), `clientId` (UUID, Unique), `name`, `duration` 필드 포함.
  - [ ] `User`와 다대일(N:1) Lazy Loading 관계 설정.
- **Repository**:
  - [ ] `findByClientId` 등 기본 조회 메서드 작성.

## 4. 메타데이터 (YAML)

```yaml
task_id: "TASK-DB-001"
title: "스케줄 데이터 모델링 (Entity/Repo)"
type: "functional"
epic: "EPIC_SCHEDULE_MGMT"
req_ids: ["REQ-FUNC-001"]
component: ["backend", "db"]

inputs:
  fields: []

outputs:
  success: { data: "Entity Mapped" }

dependencies: ["TASK-DB-024"] # User Entity 선행 필요
priority: "Must"
start_date: "2026-02-05"
due_date: "2026-02-05"
agent_profile: ["backend-developer"]
```
