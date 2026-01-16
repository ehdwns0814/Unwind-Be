package com.wombat.screenlock.unwind_be.api.schedule.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 스케줄 생성 요청 DTO
 * 
 * <p>새 스케줄 생성 시 전달되는 요청 데이터를 담는 클래스입니다.
 * iOS 앱에서 생성한 클라이언트 ID, 스케줄 이름, 집중 시간 정보를 포함합니다.</p>
 * 
 * <h3>Validation 규칙</h3>
 * <ul>
 *   <li>clientId: 필수, UUID 형식 (36자, xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)</li>
 *   <li>name: 필수, 최대 100자</li>
 *   <li>duration: 필수, 1~480분 범위</li>
 * </ul>
 * 
 * <h3>사용 예시</h3>
 * <pre>
 * {
 *   "clientId": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "아침 공부",
 *   "duration": 60
 * }
 * </pre>
 * 
 * @see com.wombat.screenlock.unwind_be.api.schedule.controller.ScheduleController
 */
public record CreateScheduleRequest(
    /**
     * 클라이언트 동기화 ID
     * <p>iOS 앱에서 생성한 UUID (36자)</p>
     * <p>서버와 iOS 앱 간 동기화 식별자로 사용됩니다.</p>
     */
    @NotBlank(message = "클라이언트 ID는 필수입니다")
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "클라이언트 ID는 UUID 형식이어야 합니다"
    )
    String clientId,

    /**
     * 스케줄 이름
     * <p>사용자가 지정하는 스케줄 이름입니다.</p>
     */
    @NotBlank(message = "스케줄 이름은 필수입니다")
    @Size(max = 100, message = "스케줄 이름은 100자를 초과할 수 없습니다")
    String name,

    /**
     * 집중 시간 (분 단위)
     * <p>1분 ~ 480분 (8시간) 범위</p>
     */
    @NotNull(message = "집중 시간은 필수입니다")
    @Min(value = 1, message = "집중 시간은 최소 1분이어야 합니다")
    @Max(value = 480, message = "집중 시간은 최대 480분(8시간)을 초과할 수 없습니다")
    Integer duration
) {}


