package com.wombat.screenlock.unwind_be.api.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 완료 통계 기록 응답 DTO
 * 
 * @param recorded 기록 성공 여부
 * @param dailyStats 해당 날짜의 누적 통계
 */
@Schema(description = "완료 통계 기록 응답")
public record CompletionResponse(
    @Schema(description = "기록 성공 여부", example = "true")
    boolean recorded,

    @Schema(description = "해당 날짜의 누적 통계")
    DailyStatsDto dailyStats
) {
    /**
     * 성공 응답 생성
     */
    public static CompletionResponse success(DailyStatsDto dailyStats) {
        return new CompletionResponse(true, dailyStats);
    }
}
