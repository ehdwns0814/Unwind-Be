package com.wombat.screenlock.unwind_be.api.stats.controller;

import com.wombat.screenlock.unwind_be.api.stats.dto.*;
import com.wombat.screenlock.unwind_be.application.stats.StatsService;
import com.wombat.screenlock.unwind_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 통계 API Controller
 * 
 * <p>사용자의 집중 통계 데이터 수집 및 조회 API를 제공합니다.</p>
 * 
 * <h3>엔드포인트</h3>
 * <ul>
 *   <li>POST /api/stats/completion - 완료 통계 기록</li>
 *   <li>POST /api/stats/force-quit - 강제 종료 기록</li>
 *   <li>GET /api/stats/summary - 통계 요약 조회</li>
 * </ul>
 * 
 * <h3>보안</h3>
 * <p>모든 엔드포인트는 JWT 인증이 필요합니다.
 * Authorization 헤더에 Bearer Token을 포함해야 합니다.</p>
 * 
 * @see CompletionRequest
 * @see ForceQuitRequest
 * @see StatsSummaryResponse
 */
@Tag(name = "Stats", description = "통계 수집 및 조회 API")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    /**
     * 완료 통계 기록 API
     * 
     * <p>스케줄 완료 시 통계를 기록합니다.
     * 동일 날짜에 여러 번 호출 시 누적됩니다.</p>
     * 
     * @param request 완료 통계 요청 DTO
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return 200 OK + CompletionResponse
     */
    @Operation(
        summary = "완료 통계 기록",
        description = "스케줄 완료 시 통계를 기록합니다. 동일 날짜에 여러 번 호출 시 누적됩니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "통계 기록 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CompletionResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/completion")
    public ResponseEntity<ApiResponse<CompletionResponse>> recordCompletion(
            @Valid @RequestBody CompletionRequest request,
            @AuthenticationPrincipal Long userId) {
        
        log.info("완료 통계 기록 요청 - userId: {}, date: {}", userId, request.date());
        
        CompletionResponse response = statsService.recordCompletion(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 강제 종료 기록 API
     * 
     * <p>앱 강제 종료 감지 시 기록합니다.
     * timestamp에서 날짜를 추출하여 해당 일자의 카운트를 증가시킵니다.</p>
     * 
     * @param request 강제 종료 요청 DTO
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return 200 OK + ForceQuitResponse
     */
    @Operation(
        summary = "강제 종료 기록",
        description = "앱 강제 종료 감지 시 기록합니다. timestamp에서 날짜를 추출하여 해당 일자의 카운트를 증가시킵니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "기록 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ForceQuitResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/force-quit")
    public ResponseEntity<ApiResponse<ForceQuitResponse>> recordForceQuit(
            @Valid @RequestBody ForceQuitRequest request,
            @AuthenticationPrincipal Long userId) {
        
        log.info("강제 종료 기록 요청 - userId: {}, timestamp: {}", userId, request.timestamp());
        
        ForceQuitResponse response = statsService.recordForceQuit(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 통계 요약 조회 API
     * 
     * <p>사용자의 스트릭, 완료율, 집중 시간 등 요약 정보를 조회합니다.</p>
     * 
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return 200 OK + StatsSummaryResponse
     */
    @Operation(
        summary = "통계 요약 조회",
        description = "사용자의 스트릭, 완료율, 집중 시간 등 요약 정보를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StatsSummaryResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<StatsSummaryResponse>> getSummary(
            @AuthenticationPrincipal Long userId) {
        
        log.info("통계 요약 조회 요청 - userId: {}", userId);
        
        StatsSummaryResponse response = statsService.getSummary(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
