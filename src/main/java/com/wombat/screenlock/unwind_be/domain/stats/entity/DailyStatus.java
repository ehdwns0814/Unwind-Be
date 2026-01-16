package com.wombat.screenlock.unwind_be.domain.stats.entity;

/**
 * 일별 상태 Enum
 * 
 * <p>사용자의 일별 집중 상태를 나타냅니다.
 * DailyStatistics 엔티티의 status 필드에서 사용됩니다.</p>
 */
public enum DailyStatus {
    /**
     * 모든 스케줄 완료
     */
    SUCCESS,
    
    /**
     * 일부 스케줄만 완료
     */
    WARNING,
    
    /**
     * 강제 종료 또는 중단
     */
    FAILURE,
    
    /**
     * 스케줄 없음
     */
    NO_PLAN,
    
    /**
     * 진행 중
     */
    IN_PROGRESS
}
