package com.wombat.screenlock.unwind_be.api.schedule.controller;

import com.wombat.screenlock.unwind_be.api.schedule.dto.CreateScheduleRequest;
import com.wombat.screenlock.unwind_be.api.schedule.dto.ScheduleResponse;
import com.wombat.screenlock.unwind_be.api.schedule.dto.UpdateScheduleRequest;
import com.wombat.screenlock.unwind_be.application.schedule.ScheduleService;
import com.wombat.screenlock.unwind_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 스케줄 API Controller
 * 
 * <p>스케줄 관리 관련 API를 제공합니다.
 * 스케줄 생성, 조회, 수정, 삭제 기능을 포함합니다.</p>
 * 
 * <h3>엔드포인트</h3>
 * <ul>
 *   <li>POST /api/schedules - 스케줄 생성</li>
 *   <li>PUT /api/schedules/{id} - 스케줄 수정</li>
 *   <li>DELETE /api/schedules/{id} - 스케줄 삭제 (Soft Delete)</li>
 * </ul>
 * 
 * <h3>보안</h3>
 * <p>모든 엔드포인트는 JWT 인증이 필요합니다.
 * Authorization 헤더에 Bearer Token을 포함해야 합니다.</p>
 * 
 * @see CreateScheduleRequest
 * @see UpdateScheduleRequest
 * @see ScheduleResponse
 */
@Tag(name = "Schedule", description = "스케줄 관리 API")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 스케줄 생성 API
     * 
     * @param request 스케줄 생성 요청 DTO
     * @param userId 인증된 사용자 ID
     * @return 201 Created + ScheduleResponse
     */
    @Operation(
        summary = "스케줄 생성",
        description = "새 스케줄을 생성합니다. iOS 앱에서 전달한 clientId를 기반으로 동기화됩니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "스케줄 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
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
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @Valid @RequestBody CreateScheduleRequest request,
            @AuthenticationPrincipal Long userId) {
        
        log.info("스케줄 생성 요청 - clientId: {}, userId: {}", request.clientId(), userId);
        
        ScheduleResponse response = scheduleService.createSchedule(request, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // ========== BE-009: 스케줄 수정/삭제 ==========

    /**
     * 스케줄 수정 API
     * 
     * @param id 스케줄 ID
     * @param request 스케줄 수정 요청 DTO
     * @param userId 인증된 사용자 ID
     * @return 200 OK + ScheduleResponse
     */
    @Operation(
        summary = "스케줄 수정",
        description = "스케줄의 이름과 집중 시간을 수정합니다. 본인 소유의 스케줄만 수정 가능합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "스케줄 수정 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
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
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (타인의 스케줄)",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "스케줄을 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @Parameter(description = "스케줄 ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateScheduleRequest request,
            @AuthenticationPrincipal Long userId) {
        
        log.info("스케줄 수정 요청 - scheduleId: {}, userId: {}", id, userId);
        
        ScheduleResponse response = scheduleService.updateSchedule(id, request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 스케줄 삭제 API (Soft Delete)
     * 
     * @param id 스케줄 ID
     * @param userId 인증된 사용자 ID
     * @return 204 No Content
     */
    @Operation(
        summary = "스케줄 삭제",
        description = "스케줄을 삭제합니다 (Soft Delete). 본인 소유의 스케줄만 삭제 가능합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "스케줄 삭제 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "권한 없음 (타인의 스케줄)",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "스케줄을 찾을 수 없음",
            content = @Content(mediaType = "application/json")
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @Parameter(description = "스케줄 ID", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        
        log.info("스케줄 삭제 요청 - scheduleId: {}, userId: {}", id, userId);
        
        scheduleService.deleteSchedule(id, userId);
        
        return ResponseEntity.noContent().build();
    }
}
