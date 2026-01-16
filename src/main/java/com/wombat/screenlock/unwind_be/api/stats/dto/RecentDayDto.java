package com.wombat.screenlock.unwind_be.api.stats.dto;

import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatistics;
import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * 최근 일별 통계 DTO (Summary 응답용)
 * 
 * @param date 통계 날짜
 * @param status 일별 상태 (소문자로 변환하여 반환)
 * @param completedSchedules 완료된 스케줄 수
 * @param totalSchedules 전체 스케줄 수
 * @param focusTime 집중 시간 (초)
 */
@Schema(description = "최근 일별 통계")
public record RecentDayDto(
    @Schema(description = "통계 날짜", example = "2026-02-12")
    LocalDate date,

    @Schema(description = "일별 상태", example = "success")
    String status,

    @Schema(description = "완료된 스케줄 수", example = "3")
    int completedSchedules,

    @Schema(description = "전체 스케줄 수", example = "4")
    int totalSchedules,

    @Schema(description = "집중 시간 (초)", example = "5400")
    int focusTime
) {
    /**
     * DailyStatistics 엔티티에서 DTO로 변환
     */
    public static RecentDayDto from(DailyStatistics entity) {
        return new RecentDayDto(
            entity.getDate(),
            entity.getStatus().name().toLowerCase(),
            entity.getCompletedSchedules(),
            entity.getTotalSchedules(),
            entity.getTotalFocusTime()
        );
    }
}

