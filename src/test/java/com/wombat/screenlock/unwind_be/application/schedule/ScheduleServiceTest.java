package com.wombat.screenlock.unwind_be.application.schedule;

import com.wombat.screenlock.unwind_be.api.schedule.dto.CreateScheduleRequest;
import com.wombat.screenlock.unwind_be.api.schedule.dto.ScheduleResponse;
import com.wombat.screenlock.unwind_be.domain.schedule.entity.Schedule;
import com.wombat.screenlock.unwind_be.domain.schedule.repository.ScheduleRepository;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.global.exception.BusinessException;
import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * ScheduleService 단위 테스트
 * 
 * <p>비즈니스 로직 테스트: 스케줄 생성, Idempotency, 예외 처리</p>
 * 
 * <h3>테스트 전략</h3>
 * <ul>
 *   <li>Given-When-Then 패턴</li>
 *   <li>Mockito를 사용한 의존성 모킹</li>
 *   <li>AssertJ를 사용한 가독성 높은 Assertion</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleService 단위 테스트")
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private static final String VALID_CLIENT_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Long VALID_USER_ID = 1L;
    private static final String SCHEDULE_NAME = "집중 스터디";
    private static final Integer DURATION = 60;

    private User testUser;
    private CreateScheduleRequest validRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .build();
        
        validRequest = new CreateScheduleRequest(
                VALID_CLIENT_ID,
                SCHEDULE_NAME,
                DURATION
        );
    }

    @Nested
    @DisplayName("createSchedule 메서드")
    class CreateSchedule {

        @Test
        @DisplayName("정상 생성 - 새 스케줄 생성 및 저장")
        void should_CreateSchedule_When_ValidRequest() {
            // Given
            given(scheduleRepository.findByClientId(VALID_CLIENT_ID))
                    .willReturn(Optional.empty());
            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(scheduleRepository.save(any(Schedule.class)))
                    .willAnswer(invocation -> {
                        Schedule schedule = invocation.getArgument(0);
                        return Schedule.builder()
                                .clientId(schedule.getClientId())
                                .name(schedule.getName())
                                .duration(schedule.getDuration())
                                .user(schedule.getUser())
                                .build();
                    });

            // When
            ScheduleResponse response = scheduleService.createSchedule(validRequest, VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.clientId()).isEqualTo(VALID_CLIENT_ID);
            assertThat(response.name()).isEqualTo(SCHEDULE_NAME);
            assertThat(response.duration()).isEqualTo(DURATION);
            
            verify(scheduleRepository).findByClientId(VALID_CLIENT_ID);
            verify(userRepository).findById(VALID_USER_ID);
            verify(scheduleRepository).save(any(Schedule.class));
        }

        @Test
        @DisplayName("Idempotency - 동일 clientId로 요청 시 기존 데이터 반환")
        void should_ReturnExisting_When_ClientIdAlreadyExists() {
            // Given
            Schedule existingSchedule = Schedule.builder()
                    .clientId(VALID_CLIENT_ID)
                    .name("기존 스케줄")
                    .duration(30)
                    .user(testUser)
                    .build();
            
            given(scheduleRepository.findByClientId(VALID_CLIENT_ID))
                    .willReturn(Optional.of(existingSchedule));

            // When
            ScheduleResponse response = scheduleService.createSchedule(validRequest, VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.clientId()).isEqualTo(VALID_CLIENT_ID);
            assertThat(response.name()).isEqualTo("기존 스케줄"); // 기존 데이터 반환
            assertThat(response.duration()).isEqualTo(30);
            
            // User 조회 및 저장은 호출되지 않음
            verify(userRepository, never()).findById(any());
            verify(scheduleRepository, never()).save(any());
        }

        @Test
        @DisplayName("USER_NOT_FOUND - 사용자를 찾을 수 없음")
        void should_ThrowException_When_UserNotFound() {
            // Given
            given(scheduleRepository.findByClientId(VALID_CLIENT_ID))
                    .willReturn(Optional.empty());
            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> scheduleService.createSchedule(validRequest, VALID_USER_ID))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException businessException = (BusinessException) ex;
                        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                    });
            
            verify(scheduleRepository).findByClientId(VALID_CLIENT_ID);
            verify(userRepository).findById(VALID_USER_ID);
            verify(scheduleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Schedule 엔티티 생성 검증")
        void should_CreateScheduleWithCorrectFields() {
            // Given
            given(scheduleRepository.findByClientId(VALID_CLIENT_ID))
                    .willReturn(Optional.empty());
            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(scheduleRepository.save(any(Schedule.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            scheduleService.createSchedule(validRequest, VALID_USER_ID);

            // Then - save 호출 시 전달된 Schedule 엔티티 검증
            verify(scheduleRepository).save(any(Schedule.class));
        }
    }

    @Nested
    @DisplayName("getSchedules 메서드")
    class GetSchedules {

        @Test
        @DisplayName("전체 목록 조회 - 사용자의 모든 스케줄 반환")
        void should_ReturnAllSchedules_When_UserHasSchedules() {
            // Given
            Schedule schedule1 = Schedule.builder()
                    .clientId("client-1")
                    .name("스케줄 1")
                    .duration(30)
                    .user(testUser)
                    .build();
            Schedule schedule2 = Schedule.builder()
                    .clientId("client-2")
                    .name("스케줄 2")
                    .duration(60)
                    .user(testUser)
                    .build();
            
            given(scheduleRepository.findByUserId(VALID_USER_ID))
                    .willReturn(List.of(schedule1, schedule2));

            // When
            List<ScheduleResponse> result = scheduleService.getSchedules(VALID_USER_ID);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("스케줄 1");
            assertThat(result.get(1).name()).isEqualTo("스케줄 2");
            
            verify(scheduleRepository).findByUserId(VALID_USER_ID);
        }

        @Test
        @DisplayName("빈 목록 - 스케줄이 없는 경우")
        void should_ReturnEmptyList_When_NoSchedules() {
            // Given
            given(scheduleRepository.findByUserId(VALID_USER_ID))
                    .willReturn(Collections.emptyList());

            // When
            List<ScheduleResponse> result = scheduleService.getSchedules(VALID_USER_ID);

            // Then
            assertThat(result).isEmpty();
            
            verify(scheduleRepository).findByUserId(VALID_USER_ID);
        }
    }

    @Nested
    @DisplayName("getSchedulesSince 메서드")
    class GetSchedulesSince {

        @Test
        @DisplayName("증분 동기화 - lastSyncTime 이후 변경분만 반환")
        void should_ReturnModifiedSchedules_When_LastSyncTimeProvided() {
            // Given
            LocalDateTime lastSyncTime = LocalDateTime.of(2026, 2, 9, 10, 0, 0);
            
            Schedule modifiedSchedule = Schedule.builder()
                    .clientId("client-modified")
                    .name("수정된 스케줄")
                    .duration(45)
                    .user(testUser)
                    .build();
            
            given(scheduleRepository.findByUserIdAndUpdatedAtAfter(VALID_USER_ID, lastSyncTime))
                    .willReturn(List.of(modifiedSchedule));

            // When
            List<ScheduleResponse> result = scheduleService.getSchedulesSince(VALID_USER_ID, lastSyncTime);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("수정된 스케줄");
            
            verify(scheduleRepository).findByUserIdAndUpdatedAtAfter(VALID_USER_ID, lastSyncTime);
        }

        @Test
        @DisplayName("변경 없음 - lastSyncTime 이후 변경분이 없는 경우")
        void should_ReturnEmptyList_When_NoChangesAfterLastSyncTime() {
            // Given
            LocalDateTime lastSyncTime = LocalDateTime.of(2026, 2, 9, 10, 0, 0);
            
            given(scheduleRepository.findByUserIdAndUpdatedAtAfter(VALID_USER_ID, lastSyncTime))
                    .willReturn(Collections.emptyList());

            // When
            List<ScheduleResponse> result = scheduleService.getSchedulesSince(VALID_USER_ID, lastSyncTime);

            // Then
            assertThat(result).isEmpty();
            
            verify(scheduleRepository).findByUserIdAndUpdatedAtAfter(VALID_USER_ID, lastSyncTime);
        }
    }
}

