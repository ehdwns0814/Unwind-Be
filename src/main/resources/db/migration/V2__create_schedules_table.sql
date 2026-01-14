-- ===========================================
-- V2__create_schedules_table.sql
-- 스케줄 테이블 생성 (Schedule Entity)
-- ===========================================

CREATE TABLE schedules (
    -- Primary Key
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '스케줄 고유 ID',
    
    -- 클라이언트 동기화 ID
    client_id VARCHAR(36) NOT NULL COMMENT 'iOS 앱에서 생성한 UUID',
    
    -- 외래키
    user_id BIGINT NOT NULL COMMENT '소유 사용자 ID',
    
    -- 스케줄 정보
    name VARCHAR(100) NOT NULL COMMENT '스케줄 이름',
    duration INT NOT NULL COMMENT '집중 시간 (분 단위)',
    
    -- Auditing 필드
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    -- 제약조건
    CONSTRAINT uk_schedules_client_id UNIQUE (client_id),
    CONSTRAINT fk_schedules_user_id FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
        
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='스케줄 테이블';

-- 인덱스 생성
CREATE INDEX idx_schedules_user_id ON schedules(user_id);

