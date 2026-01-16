package com.wombat.screenlock.unwind_be.api.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * 강제 종료 기록 요청 DTO
 * 
 * <p>iOS 앱에서 강제 종료 감지 시 서버로 전송하는 데이터입니다.
 * timestamp에서 날짜를 추출하여 해당 일자의 forceQuitCount를 증가시킵니다.</p>
 * 
 * @param timestamp 강제 종료 발생 시각 (ISO 8601 형식)
 */
@Schema(description = "강제 종료 기록 요청")
public record ForceQuitRequest(
    @Schema(description = "강제 종료 발생 시각 (ISO 8601)", example = "2026-02-12T14:30:00Z")
    @NotNull(message = "timestamp는 필수입니다")
    Instant timestamp
) {}
