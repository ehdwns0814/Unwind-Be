-- ===========================================
-- V1__create_users_table.sql
-- 사용자 테이블 생성 (User Entity)
-- ===========================================

CREATE TABLE users (
    -- Primary Key
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 ID',
    
    -- 사용자 정보
    email VARCHAR(255) NOT NULL COMMENT '이메일 (로그인 ID)',
    password_hash VARCHAR(60) NOT NULL COMMENT 'BCrypt 해시 비밀번호',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '사용자 권한 (USER, ADMIN)',
    
    -- Auditing 필드
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    -- Unique Constraint
    CONSTRAINT uk_users_email UNIQUE (email)
    
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='사용자 테이블';

-- 인덱스는 UNIQUE CONSTRAINT에 의해 자동 생성됨
-- uk_users_email: email 컬럼에 대한 Unique Index

