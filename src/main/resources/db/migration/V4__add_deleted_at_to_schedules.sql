-- =====================================================
-- V4: Add deleted_at column to schedules table for Soft Delete
-- BE-009: 스케줄 수정/삭제 API
-- =====================================================

-- Add deleted_at column for soft delete functionality
-- NULL means active/not deleted, timestamp means deleted at that time
ALTER TABLE schedules
    ADD COLUMN deleted_at DATETIME NULL DEFAULT NULL COMMENT 'Soft delete timestamp (NULL = active)';

-- Add index for efficient querying of active schedules
-- Most queries will filter by deleted_at IS NULL
CREATE INDEX idx_schedules_deleted_at ON schedules(deleted_at);
