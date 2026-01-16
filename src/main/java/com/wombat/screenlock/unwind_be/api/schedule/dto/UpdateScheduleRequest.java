package com.wombat.screenlock.unwind_be.api.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 스케줄 수정 요청 DTO
 * 
 * <p>스케줄 수정 시 전달되는 요청 데이터를 담는 클래스입니다.</p>
 * 
 * @see com.wombat.screenlock.unwind_be.api.schedule.controller.ScheduleController
 */
@Schema(description = "스케줄 수정 요청 DTO")
public record UpdateScheduleRequest(
    @Schema(
        description = "스케줄 이름",
        example = "수정된 아침 공부",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100
    )
    @NotBlank(message = "스케줄 이름은 필수입니다")
    @Size(max = 100, message = "스케줄 이름은 100자 이하여야 합니다")
    String name,

    @Schema(
        description = "집중 시간 (분 단위, 1~480)",
        example = "90",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "1",
        maximum = "480"
    )
    @NotNull(message = "집중 시간은 필수입니다")
    @Min(value = 1, message = "집중 시간은 최소 1분 이상이어야 합니다")
    @Max(value = 480, message = "집중 시간은 최대 480분(8시간)까지 가능합니다")
    Integer duration
) {
}
