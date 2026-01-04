# TASK-DB-024: 사용자 도메인 데이터 모델링 (Backend)

## 1. 목적 및 요약
- **목적**: 사용자 및 인증 정보를 저장하기 위한 DB 구조를 정의한다.
- **요약**: `User` 엔티티와 `RefreshToken` 저장소(Redis 또는 DB)를 설계한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-FUNC-024 (Data Layer)
- **Component**: Backend DB

## 3. Definition of Done (DoD)
- [ ] **Schema**: `users` 테이블 생성 DDL. (email unique index 필수)
- **Entity**:
  - [ ] `email`, `passwordHash`, `role` 필드 포함.
  - [ ] `BaseTimeEntity`(createdAt, updatedAt) 상속.

## 4. 메타데이터 (YAML)

```yaml
task_id: "TASK-DB-024"
title: "사용자 데이터 모델링"
type: "functional"
epic: "EPIC_AUTH"
req_ids: ["REQ-FUNC-024"]
component: ["backend", "db"]

inputs: []
outputs:
  success: { data: "User/Token Entity" }

dependencies: []
priority: "Must"
start_date: "2026-01-30"
due_date: "2026-01-30"
agent_profile: ["backend-developer"]
```
