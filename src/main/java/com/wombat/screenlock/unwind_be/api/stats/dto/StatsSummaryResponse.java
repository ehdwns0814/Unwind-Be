package com.wombat.screenlock.unwind_be.api.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 사용자 통계 요약 응답 DTO
 * 
 * <p>주간/월간 통계 요약 정보와 최근 일별 상세 데이터를 포함합니다.</p>
 * 
 * @param currentStreak 현재 연속 성공 일수
 * @param longestStreak 최장 연속 성공 일수
 * @param weeklyCompletionRate 주간 완료율
 * @param monthlyCompletionRate 월간 완료율
 * @param totalFocusTimeThisWeek 이번 주 총 집중 시간 (초)
 * @param totalFocusTimeThisMonth 이번 달 총 집중 시간 (초)
 * @param recentDays 최근 일별 통계 목록
 */
@Schema(description = "사용자 통계 요약")
public record StatsSummaryResponse(
    @Schema(description = "현재 연속 성공 일수", example = "7")
    int currentStreak,

    @Schema(description = "최장 연속 성공 일수", example = "14")
    int longestStreak,

    @Schema(description = "주간 완료율 (0.0 ~ 1.0)", example = "0.75")
    double weeklyCompletionRate,

    @Schema(description = "월간 완료율 (0.0 ~ 1.0)", example = "0.68")
    double monthlyCompletionRate,

    @Schema(description = "이번 주 총 집중 시간 (초)", example = "18000")
    int totalFocusTimeThisWeek,

    @Schema(description = "이번 달 총 집중 시간 (초)", example = "72000")
    int totalFocusTimeThisMonth,

    @Schema(description = "최근 일별 통계")
    List<RecentDayDto> recentDays
) {
    /**
     * Builder 패턴 지원을 위한 정적 빌더 클래스
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int currentStreak;
        private int longestStreak;
        private double weeklyCompletionRate;
        private double monthlyCompletionRate;
        private int totalFocusTimeThisWeek;
        private int totalFocusTimeThisMonth;
        private List<RecentDayDto> recentDays;

        public Builder currentStreak(int currentStreak) {
            this.currentStreak = currentStreak;
            return this;
        }

        public Builder longestStreak(int longestStreak) {
            this.longestStreak = longestStreak;
            return this;
        }

        public Builder weeklyCompletionRate(double weeklyCompletionRate) {
            this.weeklyCompletionRate = weeklyCompletionRate;
            return this;
        }

        public Builder monthlyCompletionRate(double monthlyCompletionRate) {
            this.monthlyCompletionRate = monthlyCompletionRate;
            return this;
        }

        public Builder totalFocusTimeThisWeek(int totalFocusTimeThisWeek) {
            this.totalFocusTimeThisWeek = totalFocusTimeThisWeek;
            return this;
        }

        public Builder totalFocusTimeThisMonth(int totalFocusTimeThisMonth) {
            this.totalFocusTimeThisMonth = totalFocusTimeThisMonth;
            return this;
        }

        public Builder recentDays(List<RecentDayDto> recentDays) {
            this.recentDays = recentDays;
            return this;
        }

        public StatsSummaryResponse build() {
            return new StatsSummaryResponse(
                currentStreak,
                longestStreak,
                weeklyCompletionRate,
                monthlyCompletionRate,
                totalFocusTimeThisWeek,
                totalFocusTimeThisMonth,
                recentDays
            );
        }
    }
}
