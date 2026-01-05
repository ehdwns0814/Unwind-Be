# [BE-004] 로그인 및 토큰 갱신(Refresh)

**Epic:** EPIC_AUTH  
**Priority:** Must  
**Effort:** M  
**Start Date:** 2026-02-03  
**Due Date:** 2026-02-04  
**Dependencies:** BE-003

---

## 목적 및 요약
- **목적**: 기존 사용자의 재진입을 처리한다.
- **요약**: `POST /api/auth/login` 처리 및 Refresh Token을 이용한 `POST /api/auth/refresh` 기능 구현.

## 관련 스펙 (SRS)
- **ID**: REQ-FUNC-025
- **Component**: Backend API

## Sub-Tasks (구현 상세)

### 처리 (Process)
- **Login**: 이메일/비번 검증 -> 토큰 발급.
- **Refresh**: 만료된 AccessToken 갱신. (Redis 등에 RefreshToken 저장 권장).

## Definition of Done (DoD)

- [ ] **Login**: 유효한 이메일/비번으로 로그인 시 토큰이 발급되어야 한다.
- [ ] **Refresh**: 유효한 RefreshToken으로 새 AccessToken을 받아야 한다.
- [ ] **Error**: 잘못된 인증 정보 시 401 에러가 반환되어야 한다.
- [ ] **Storage**: RefreshToken이 Redis/DB에 저장되어야 한다.

## 구현 힌트

- Login 로직 구현
- RefreshToken 저장소 (Redis/DB) 구현
- TokenReissueService 구현

## 테스트

- **Unit**: Login Service
- **Integration**: Refresh Flow

---

**Labels:** `backend`, `must`, `phase-1`  
**Milestone:** v1.0-MVP

