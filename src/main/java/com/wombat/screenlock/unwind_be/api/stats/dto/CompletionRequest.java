package com.wombat.screenlock.unwind_be.api.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * 스케줄 완료 통계 기록 요청 DTO
 * 
 * <p>iOS 앱에서 스케줄 완료 시 서버로 전송하는 데이터입니다.
 * 일별 통계에 누적됩니다.</p>
 * 
 * @param scheduleId 완료한 스케줄의 clientId (UUID 형식)
 * @param completed 완료 여부 (true: 완료, false: 중단)
 * @param focusTime 실제 집중 시간 (초 단위)
 * @param allInMode 올인 모드 사용 여부
 * @param date 통계 날짜
 */
@Schema(description = "완료 통계 기록 요청")
public record CompletionRequest(
    @Schema(description = "스케줄 clientId (UUID 형식)", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotBlank(message = "scheduleId는 필수입니다")
    String scheduleId,

    @Schema(description = "완료 여부", example = "true")
    @NotNull(message = "completed는 필수입니다")
    Boolean completed,

    @Schema(description = "집중 시간 (초 단위)", example = "1800", minimum = "0")
    @NotNull(message = "focusTime은 필수입니다")
    @Min(value = 0, message = "focusTime은 0 이상이어야 합니다")
    Integer focusTime,

    @Schema(description = "올인 모드 사용 여부", example = "false")
    Boolean allInMode,

    @Schema(description = "통계 날짜 (yyyy-MM-dd)", example = "2026-02-12")
    @NotNull(message = "date는 필수입니다")
    LocalDate date
) {
    /**
     * allInMode가 null인 경우 기본값 false 반환
     */
    public boolean isAllInMode() {
        return allInMode != null && allInMode;
    }
}
