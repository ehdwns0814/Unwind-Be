-- ===========================================
-- V3__create_daily_statistics_table.sql
-- 일별 통계 테이블 생성 (DailyStatistics Entity)
-- ===========================================

CREATE TABLE daily_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '통계 고유 ID',
    user_id BIGINT NOT NULL COMMENT '소유 사용자 ID',
    date DATE NOT NULL COMMENT '통계 날짜',
    total_schedules INT NOT NULL DEFAULT 0 COMMENT '전체 스케줄 수',
    completed_schedules INT NOT NULL DEFAULT 0 COMMENT '완료된 스케줄 수',
    total_focus_time INT NOT NULL DEFAULT 0 COMMENT '총 집중 시간 (초 단위)',
    force_quit_count INT NOT NULL DEFAULT 0 COMMENT '강제 종료 횟수',
    all_in_mode_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '올인 모드 사용 여부',
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '일별 상태',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    CONSTRAINT uk_daily_statistics_user_date UNIQUE (user_id, date),
    CONSTRAINT fk_daily_statistics_user_id FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='일별 통계 테이블';

CREATE INDEX idx_daily_statistics_user_date_desc ON daily_statistics(user_id, date DESC);
