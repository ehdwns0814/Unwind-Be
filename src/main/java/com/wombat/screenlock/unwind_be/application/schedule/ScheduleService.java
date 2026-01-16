package com.wombat.screenlock.unwind_be.application.schedule;

import com.wombat.screenlock.unwind_be.api.schedule.dto.CreateScheduleRequest;
import com.wombat.screenlock.unwind_be.api.schedule.dto.ScheduleResponse;
import com.wombat.screenlock.unwind_be.api.schedule.dto.UpdateScheduleRequest;
import com.wombat.screenlock.unwind_be.domain.schedule.entity.Schedule;
import com.wombat.screenlock.unwind_be.domain.schedule.repository.ScheduleRepository;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.global.exception.BusinessException;
import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 스케줄 서비스
 * 
 * <p>스케줄 생성, 조회, 수정, 삭제 비즈니스 로직을 담당합니다.</p>
 * 
 * <h3>트랜잭션 관리</h3>
 * <ul>
 *   <li>클래스 레벨: @Transactional(readOnly = true) - 읽기 전용 기본값</li>
 *   <li>데이터 변경 메서드: @Transactional로 오버라이드</li>
 * </ul>
 * 
 * <h3>Idempotency 처리</h3>
 * <p>동일한 clientId로 요청이 오면 기존 데이터를 반환합니다.
 * 이는 iOS 앱의 동기화 로직에서 중복 요청을 안전하게 처리하기 위함입니다.</p>
 * 
 * @see com.wombat.screenlock.unwind_be.domain.schedule.repository.ScheduleRepository
 * @see com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    /**
     * 스케줄 생성
     * 
     * <h3>비즈니스 로직 순서</h3>
     * <ol>
     *   <li>clientId 중복 체크 (Idempotency)</li>
     *   <li>userId로 User 엔티티 조회</li>
     *   <li>Schedule 엔티티 생성 및 저장</li>
     *   <li>ScheduleResponse DTO 반환</li>
     * </ol>
     * 
     * <h3>Idempotency</h3>
     * <p>동일한 clientId로 요청이 오면 기존 Schedule을 반환합니다.
     * 이는 에러가 아닌 정상 응답으로 처리됩니다.</p>
     * 
     * @param request 스케줄 생성 요청 DTO
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return ScheduleResponse 생성된 스케줄 정보
     * @throws BusinessException USER_NOT_FOUND - 사용자를 찾을 수 없음
     */
    @Transactional
    public ScheduleResponse createSchedule(CreateScheduleRequest request, Long userId) {
        // 1. clientId 중복 체크 (Idempotency)
        Optional<Schedule> existing = scheduleRepository.findByClientId(request.clientId());
        if (existing.isPresent()) {
            log.info("Idempotency: 기존 스케줄 반환 - clientId={}, scheduleId={}", 
                    request.clientId(), existing.get().getId());
            return ScheduleResponse.from(existing.get());
        }

        // 2. userId로 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("스케줄 생성 실패: 사용자를 찾을 수 없음 - userId={}", userId);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        // 3. Schedule 엔티티 생성
        Schedule schedule = Schedule.builder()
                .clientId(request.clientId())
                .name(request.name())
                .duration(request.duration())
                .user(user)
                .build();

        // 4. Schedule 저장
        Schedule savedSchedule = scheduleRepository.save(schedule);
        
        log.info("스케줄 생성 완료 - scheduleId={}, clientId={}, userId={}", 
                savedSchedule.getId(), savedSchedule.getClientId(), userId);

        // 5. DTO 변환 및 반환
        return ScheduleResponse.from(savedSchedule);
    }

    /**
     * 사용자의 전체 스케줄 목록 조회
     * 
     * <p>초기 동기화 시 사용됩니다. 사용자의 모든 스케줄을 반환합니다.</p>
     * 
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return 스케줄 목록 (ScheduleResponse DTO 리스트)
     */
    public List<ScheduleResponse> getSchedules(Long userId) {
        log.info("스케줄 목록 조회 - userId={}", userId);
        
        List<Schedule> schedules = scheduleRepository.findByUserId(userId);
        
        log.debug("스케줄 목록 조회 완료 - userId={}, count={}", userId, schedules.size());
        
        return schedules.stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    /**
     * 마지막 동기화 시간 이후 변경된 스케줄 조회
     * 
     * <p>증분 동기화 시 사용됩니다. lastSyncTime 이후에 생성되거나 
     * 수정된 스케줄만 반환합니다.</p>
     * 
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @param lastSyncTime 마지막 동기화 시간 (ISO-8601 형식)
     * @return 변경된 스케줄 목록 (ScheduleResponse DTO 리스트)
     */
    public List<ScheduleResponse> getSchedulesSince(Long userId, LocalDateTime lastSyncTime) {
        log.info("스케줄 증분 동기화 - userId={}, lastSyncTime={}", userId, lastSyncTime);
        
        List<Schedule> schedules = scheduleRepository.findByUserIdAndUpdatedAtAfter(userId, lastSyncTime);
        
        log.debug("스케줄 증분 동기화 완료 - userId={}, count={}", userId, schedules.size());
        
        return schedules.stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    // ========== BE-009: 스케줄 수정/삭제 ==========

    /**
     * 스케줄 수정
     * 
     * <h3>비즈니스 로직 순서</h3>
     * <ol>
     *   <li>스케줄 조회 (활성 상태만, Soft Delete 제외)</li>
     *   <li>본인 소유 확인</li>
     *   <li>필드 업데이트 (name, duration)</li>
     *   <li>저장 및 응답 반환</li>
     * </ol>
     * 
     * @param scheduleId 수정할 스케줄 ID
     * @param request 수정 요청 DTO
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return ScheduleResponse 수정된 스케줄 정보
     * @throws BusinessException SCHEDULE_NOT_FOUND - 스케줄을 찾을 수 없음
     * @throws BusinessException SCHEDULE_ACCESS_DENIED - 본인 스케줄이 아님
     */
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, UpdateScheduleRequest request, Long userId) {
        log.info("스케줄 수정 요청 - scheduleId={}, userId={}", scheduleId, userId);

        // 1. 스케줄 조회 (활성 상태만)
        Schedule schedule = scheduleRepository.findActiveById(scheduleId)
                .orElseThrow(() -> {
                    log.warn("스케줄 수정 실패: 스케줄을 찾을 수 없음 - scheduleId={}", scheduleId);
                    return new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
                });

        // 2. 본인 소유 확인
        if (!schedule.isOwnedBy(userId)) {
            log.warn("스케줄 수정 실패: 권한 없음 - scheduleId={}, ownerId={}, requesterId={}", 
                    scheduleId, schedule.getUser().getId(), userId);
            throw new BusinessException(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }

        // 3. 필드 업데이트
        schedule.update(request.name(), request.duration());

        // 4. 저장 (JPA 변경 감지로 자동 저장, updatedAt 자동 갱신)
        Schedule updatedSchedule = scheduleRepository.save(schedule);

        log.info("스케줄 수정 완료 - scheduleId={}, name={}, duration={}", 
                scheduleId, request.name(), request.duration());

        return ScheduleResponse.from(updatedSchedule);
    }

    /**
     * 스케줄 삭제 (Soft Delete)
     * 
     * <h3>비즈니스 로직 순서</h3>
     * <ol>
     *   <li>스케줄 조회 (활성 상태만, Soft Delete 제외)</li>
     *   <li>본인 소유 확인</li>
     *   <li>Soft Delete 처리 (deletedAt 설정)</li>
     * </ol>
     * 
     * @param scheduleId 삭제할 스케줄 ID
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @throws BusinessException SCHEDULE_NOT_FOUND - 스케줄을 찾을 수 없음
     * @throws BusinessException SCHEDULE_ACCESS_DENIED - 본인 스케줄이 아님
     */
    @Transactional
    public void deleteSchedule(Long scheduleId, Long userId) {
        log.info("스케줄 삭제 요청 - scheduleId={}, userId={}", scheduleId, userId);

        // 1. 스케줄 조회 (활성 상태만)
        Schedule schedule = scheduleRepository.findActiveById(scheduleId)
                .orElseThrow(() -> {
                    log.warn("스케줄 삭제 실패: 스케줄을 찾을 수 없음 - scheduleId={}", scheduleId);
                    return new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
                });

        // 2. 본인 소유 확인
        if (!schedule.isOwnedBy(userId)) {
            log.warn("스케줄 삭제 실패: 권한 없음 - scheduleId={}, ownerId={}, requesterId={}", 
                    scheduleId, schedule.getUser().getId(), userId);
            throw new BusinessException(ErrorCode.SCHEDULE_ACCESS_DENIED);
        }

        // 3. Soft Delete 처리
        schedule.softDelete();
        scheduleRepository.save(schedule);

        log.info("스케줄 삭제 완료 (Soft Delete) - scheduleId={}, userId={}", scheduleId, userId);
    }
}

