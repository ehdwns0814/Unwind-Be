package com.wombat.screenlock.unwind_be.api.stats.dto;

import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatistics;
import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * 일별 통계 요약 DTO
 * 
 * <p>DailyStatistics 엔티티를 API 응답용으로 변환합니다.</p>
 * 
 * @param date 통계 날짜
 * @param totalSchedules 전체 스케줄 수
 * @param completedSchedules 완료된 스케줄 수
 * @param totalFocusTime 총 집중 시간 (초)
 * @param completionRate 완료율 (0.0 ~ 1.0)
 * @param status 일별 상태
 */
@Schema(description = "일별 통계 정보")
public record DailyStatsDto(
    @Schema(description = "통계 날짜", example = "2026-02-12")
    LocalDate date,

    @Schema(description = "전체 스케줄 수", example = "5")
    int totalSchedules,

    @Schema(description = "완료된 스케줄 수", example = "3")
    int completedSchedules,

    @Schema(description = "총 집중 시간 (초)", example = "5400")
    int totalFocusTime,

    @Schema(description = "완료율 (0.0 ~ 1.0)", example = "0.6")
    double completionRate,

    @Schema(description = "일별 상태", example = "SUCCESS")
    DailyStatus status
) {
    /**
     * DailyStatistics 엔티티에서 DTO로 변환
     */
    public static DailyStatsDto from(DailyStatistics entity) {
        return new DailyStatsDto(
            entity.getDate(),
            entity.getTotalSchedules(),
            entity.getCompletedSchedules(),
            entity.getTotalFocusTime(),
            entity.getCompletionRate(),
            entity.getStatus()
        );
    }
}
