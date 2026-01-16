package com.wombat.screenlock.unwind_be.api.schedule.dto;

import com.wombat.screenlock.unwind_be.domain.schedule.entity.Schedule;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 스케줄 응답 DTO
 * 
 * <p>스케줄 생성/조회 시 반환되는 응답 데이터를 담는 클래스입니다.
 * Entity 정보를 클라이언트에게 전달하기 위한 DTO입니다.</p>
 * 
 * <h3>응답 필드</h3>
 * <ul>
 *   <li>id: 서버에서 생성한 스케줄 고유 ID</li>
 *   <li>clientId: iOS에서 전달한 클라이언트 동기화 ID</li>
 *   <li>name: 스케줄 이름</li>
 *   <li>duration: 집중 시간 (분)</li>
 *   <li>createdAt: 생성 일시 (ISO-8601 형식)</li>
 *   <li>updatedAt: 수정 일시 (ISO-8601 형식)</li>
 * </ul>
 * 
 * <h3>응답 예시</h3>
 * <pre>
 * {
 *   "id": 1,
 *   "clientId": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "아침 공부",
 *   "duration": 60,
 *   "createdAt": "2026-02-06T09:00:00",
 *   "updatedAt": "2026-02-06T09:00:00"
 * }
 * </pre>
 * 
 * @see Schedule
 * @see com.wombat.screenlock.unwind_be.api.schedule.controller.ScheduleController
 */
@Builder
public record ScheduleResponse(
    /**
     * 스케줄 고유 ID (서버에서 생성)
     */
    Long id,

    /**
     * 클라이언트 동기화 ID
     * <p>iOS 앱에서 생성한 UUID</p>
     */
    String clientId,

    /**
     * 스케줄 이름
     */
    String name,

    /**
     * 집중 시간 (분 단위)
     */
    Integer duration,

    /**
     * 생성 일시 (ISO-8601 형식)
     */
    LocalDateTime createdAt,

    /**
     * 수정 일시 (ISO-8601 형식)
     */
    LocalDateTime updatedAt
) {
    /**
     * Schedule Entity를 ScheduleResponse DTO로 변환
     * 
     * <p>Entity → DTO 변환을 위한 정적 팩토리 메서드입니다.
     * Controller 계층에서 Entity를 직접 반환하지 않기 위해 사용됩니다.</p>
     * 
     * @param schedule Schedule Entity
     * @return ScheduleResponse DTO
     */
    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .clientId(schedule.getClientId())
                .name(schedule.getName())
                .duration(schedule.getDuration())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}


