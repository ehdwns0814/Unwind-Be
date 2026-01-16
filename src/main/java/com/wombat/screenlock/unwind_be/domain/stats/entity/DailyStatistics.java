package com.wombat.screenlock.unwind_be.domain.stats.entity;

import com.wombat.screenlock.unwind_be.domain.common.BaseTimeEntity;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 일별 통계 엔티티
 * 
 * <p>사용자의 일별 집중 통계를 저장합니다.
 * userId + date 조합으로 Unique 제약이 적용됩니다.</p>
 */
@Entity
@Table(name = "daily_statistics", indexes = {
    @Index(name = "uk_daily_statistics_user_date", 
           columnList = "user_id, date", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStatistics extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "total_schedules", nullable = false)
    private Integer totalSchedules = 0;

    @Column(name = "completed_schedules", nullable = false)
    private Integer completedSchedules = 0;

    @Column(name = "total_focus_time", nullable = false)
    private Integer totalFocusTime = 0;

    @Column(name = "force_quit_count", nullable = false)
    private Integer forceQuitCount = 0;

    @Column(name = "all_in_mode_used", nullable = false)
    private Boolean allInModeUsed = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DailyStatus status = DailyStatus.IN_PROGRESS;

    @Builder
    public DailyStatistics(User user, LocalDate date) {
        this.user = user;
        this.date = date;
        this.totalSchedules = 0;
        this.completedSchedules = 0;
        this.totalFocusTime = 0;
        this.forceQuitCount = 0;
        this.allInModeUsed = false;
        this.status = DailyStatus.IN_PROGRESS;
    }

    /**
     * 스케줄 완료 기록 추가
     */
    public void recordCompletion(boolean completed, int focusTime, boolean allInMode) {
        this.totalSchedules++;
        if (completed) {
            this.completedSchedules++;
        }
        this.totalFocusTime += focusTime;
        if (allInMode) {
            this.allInModeUsed = true;
        }
        updateStatus();
    }

    /**
     * 강제 종료 카운트 증가
     */
    public void incrementForceQuit() {
        this.forceQuitCount++;
        this.status = DailyStatus.FAILURE;
    }

    /**
     * 상태 자동 계산
     */
    private void updateStatus() {
        if (this.forceQuitCount > 0) {
            this.status = DailyStatus.FAILURE;
        } else if (this.totalSchedules > 0 && 
                   this.completedSchedules.equals(this.totalSchedules)) {
            this.status = DailyStatus.SUCCESS;
        } else if (this.completedSchedules > 0) {
            this.status = DailyStatus.WARNING;
        }
    }

    /**
     * 완료율 계산
     */
    public double getCompletionRate() {
        if (totalSchedules == 0) return 0.0;
        return (double) completedSchedules / totalSchedules;
    }
}
