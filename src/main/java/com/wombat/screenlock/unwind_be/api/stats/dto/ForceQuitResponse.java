package com.wombat.screenlock.unwind_be.api.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 강제 종료 기록 응답 DTO
 * 
 * @param recorded 기록 성공 여부
 * @param forceQuitCount 해당 날짜의 누적 강제 종료 횟수
 */
@Schema(description = "강제 종료 기록 응답")
public record ForceQuitResponse(
    @Schema(description = "기록 성공 여부", example = "true")
    boolean recorded,

    @Schema(description = "해당 날짜의 누적 강제 종료 횟수", example = "2")
    int forceQuitCount
) {
    /**
     * 성공 응답 생성
     */
    public static ForceQuitResponse success(int forceQuitCount) {
        return new ForceQuitResponse(true, forceQuitCount);
    }
}
