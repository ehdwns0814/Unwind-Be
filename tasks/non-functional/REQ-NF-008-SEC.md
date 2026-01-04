# REQ-NF-008: 데이터 보안 요구사항 (iOS/Backend)

## 1. 목적 및 요약
- **목적**: 사용자 데이터를 안전하게 보호한다.
- **요약**: 로컬 저장은 `UserDefaults`(일반 데이터)와 `Keychain`(민감 데이터)을 구분하여 사용하고, 전송 구간은 HTTPS를 강제한다.

## 2. 관련 스펙 (SRS)
- **ID**: REQ-NF-008, REQ-NF-009
- **Component**: iOS App, Backend API

## 3. Definition of Done (DoD)
- [ ] **Local Storage**:
  - 비밀번호, 액세스 토큰 등 민감 정보는 절대로 `UserDefaults`에 평문 저장하지 않는다.
  - `Keychain` 서비스를 사용하여 저장해야 한다.
- **Network**:
  - 모든 API 호출은 HTTPS(TLS 1.2+)를 통해서만 이루어져야 한다. (ATS 설정 확인)
- **Logging**:
  - 로그에 개인정보(이메일, 비밀번호)가 남지 않도록 마스킹 처리해야 한다.

## 4. 메타데이터 (YAML)

```yaml
task_id: "REQ-NF-008-SEC"
title: "데이터 보안 (Keychain, TLS, Logging)"
type: "non-functional"
epic: "EPIC_NFR"
req_ids: ["REQ-NF-008"]
component: ["ios-app", "backend", "security"]

steps_hint:
  - "KeychainHelper 유틸리티 구현"
  - "NetworkInterceptor: Logging 시 Body Masking"

tests:
  manual: ["Charles Proxy로 HTTPS 암호화 확인"]
  automated: ["Keychain 저장/로드 단위 테스트"]

priority: "Must"
agent_profile: ["ios-developer", "backend-developer"]
```
