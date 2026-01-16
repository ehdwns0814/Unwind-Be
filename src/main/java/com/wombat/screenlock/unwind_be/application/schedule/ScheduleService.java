package com.wombat.screenlock.unwind_be.application.schedule;

import com.wombat.screenlock.unwind_be.api.schedule.dto.CreateScheduleRequest;
import com.wombat.screenlock.unwind_be.api.schedule.dto.ScheduleResponse;
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
}

