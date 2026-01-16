package com.wombat.screenlock.unwind_be.api.schedule.controller;

import com.wombat.screenlock.unwind_be.api.schedule.dto.CreateScheduleRequest;
import com.wombat.screenlock.unwind_be.api.schedule.dto.ScheduleResponse;
import com.wombat.screenlock.unwind_be.application.schedule.ScheduleService;
import com.wombat.screenlock.unwind_be.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 스케줄 API Controller
 * 
 * <p>스케줄 관리 관련 API를 제공합니다.
 * 스케줄 생성, 조회, 수정, 삭제 기능을 포함합니다.</p>
 * 
 * <h3>엔드포인트</h3>
 * <ul>
 *   <li>GET /api/schedules - 스케줄 목록 조회 (동기화)</li>
 *   <li>POST /api/schedules - 스케줄 생성</li>
 * </ul>
 * 
 * <h3>보안</h3>
 * <p>모든 엔드포인트는 JWT 인증이 필요합니다.
 * Authorization 헤더에 Bearer Token을 포함해야 합니다.</p>
 * 
 * @see CreateScheduleRequest
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
     * 스케줄 목록 조회 API (동기화)
     * 
     * <p>사용자의 스케줄 목록을 조회합니다. lastSyncTime 파라미터가 있으면
     * 해당 시간 이후 변경된 데이터만 반환합니다 (증분 동기화).</p>
     * 
     * @param lastSyncTime 마지막 동기화 시간 (선택)
     * @param userId 인증된 사용자 ID
     * @return 200 OK + 스케줄 목록
     */
    @Operation(
        summary = "스케줄 목록 조회 (동기화)",
        description = "사용자의 스케줄 목록을 조회합니다. lastSyncTime 파라미터로 증분 동기화가 가능합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "스케줄 목록 조회 성공",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 (JWT 토큰 없음/만료)",
            content = @Content(mediaType = "application/json")
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(
            @Parameter(description = "마지막 동기화 시간 (ISO-8601 형식, 예: 2026-02-09T10:00:00)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastSyncTime,
            @AuthenticationPrincipal Long userId) {
        
        List<ScheduleResponse> schedules;
        
        if (lastSyncTime != null) {
            log.info("스케줄 증분 동기화 요청 - userId: {}, lastSyncTime: {}", userId, lastSyncTime);
            schedules = scheduleService.getSchedulesSince(userId, lastSyncTime);
        } else {
            log.info("스케줄 전체 목록 조회 요청 - userId: {}", userId);
            schedules = scheduleService.getSchedules(userId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }

    /**
     * 스케줄 생성 API
     * 
     * <p>새 스케줄을 생성합니다. iOS 앱에서 전달한 clientId를 기반으로
     * 서버에서 스케줄을 저장하고 생성된 스케줄 정보를 반환합니다.</p>
     * 
     * <h3>요청</h3>
     * <ul>
     *   <li>clientId: iOS 앱에서 생성한 UUID (필수, 36자)</li>
     *   <li>name: 스케줄 이름 (필수, 최대 100자)</li>
     *   <li>duration: 집중 시간 (필수, 1~480분)</li>
     * </ul>
     * 
     * <h3>응답</h3>
     * <ul>
     *   <li>201 Created: 스케줄 생성 성공</li>
     *   <li>400 Bad Request: 유효성 검증 실패</li>
     *   <li>401 Unauthorized: 인증 실패 (JWT 토큰 없음/만료)</li>
     *   <li>409 Conflict: clientId 중복</li>
     * </ul>
     * 
     * @param request 스케줄 생성 요청 DTO
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
            description = "유효성 검증 실패 (입력값 오류)",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 (JWT 토큰 없음/만료)",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "clientId 중복",
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
}


