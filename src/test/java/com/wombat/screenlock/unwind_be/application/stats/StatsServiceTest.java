package com.wombat.screenlock.unwind_be.application.stats;

import com.wombat.screenlock.unwind_be.api.stats.dto.*;
import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatistics;
import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatus;
import com.wombat.screenlock.unwind_be.domain.stats.repository.DailyStatisticsRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * StatsService 단위 테스트
 * 
 * <p>통계 기록 및 조회 비즈니스 로직 테스트</p>
 * 
 * <h3>테스트 전략</h3>
 * <ul>
 *   <li>Given-When-Then 패턴</li>
 *   <li>Mockito를 사용한 의존성 모킹</li>
 *   <li>AssertJ를 사용한 가독성 높은 Assertion</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StatsService 단위 테스트")
class StatsServiceTest {

    @Mock
    private DailyStatisticsRepository dailyStatisticsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StatsService statsService;

    private static final Long VALID_USER_ID = 1L;
    private static final String SCHEDULE_CLIENT_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final LocalDate TEST_DATE = LocalDate.of(2026, 2, 12);

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .build();
        // 테스트용 User ID 설정 (리플렉션)
        ReflectionTestUtils.setField(testUser, "id", VALID_USER_ID);
    }

    // ========== recordCompletion 테스트 ==========

    @Nested
    @DisplayName("recordCompletion 메서드")
    class RecordCompletion {

        @Test
        @DisplayName("신규 생성 - 해당 날짜에 기록이 없을 때 새로 생성")
        void should_CreateNewRecord_When_NoExistingRecord() {
            // Given
            CompletionRequest request = new CompletionRequest(
                    SCHEDULE_CLIENT_ID,
                    true,
                    1800,
                    false,
                    TEST_DATE
            );

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(dailyStatisticsRepository.findByUserIdAndDate(VALID_USER_ID, TEST_DATE))
                    .willReturn(Optional.empty());
            given(dailyStatisticsRepository.save(any(DailyStatistics.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            CompletionResponse response = statsService.recordCompletion(request, VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.recorded()).isTrue();
            assertThat(response.dailyStats().date()).isEqualTo(TEST_DATE);
            assertThat(response.dailyStats().totalSchedules()).isEqualTo(1);
            assertThat(response.dailyStats().completedSchedules()).isEqualTo(1);
            assertThat(response.dailyStats().totalFocusTime()).isEqualTo(1800);

            verify(userRepository).findById(VALID_USER_ID);
            verify(dailyStatisticsRepository).findByUserIdAndDate(VALID_USER_ID, TEST_DATE);
            verify(dailyStatisticsRepository).save(any(DailyStatistics.class));
        }

        @Test
        @DisplayName("누적 업데이트 - 기존 기록에 추가")
        void should_AccumulateRecord_When_ExistingRecord() {
            // Given
            CompletionRequest request = new CompletionRequest(
                    SCHEDULE_CLIENT_ID,
                    true,
                    1800,
                    false,
                    TEST_DATE
            );

            DailyStatistics existingStats = DailyStatistics.builder()
                    .user(testUser)
                    .date(TEST_DATE)
                    .build();
            // 기존 기록 설정
            existingStats.recordCompletion(true, 1200, false);

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(dailyStatisticsRepository.findByUserIdAndDate(VALID_USER_ID, TEST_DATE))
                    .willReturn(Optional.of(existingStats));
            given(dailyStatisticsRepository.save(any(DailyStatistics.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            CompletionResponse response = statsService.recordCompletion(request, VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.dailyStats().totalSchedules()).isEqualTo(2);
            assertThat(response.dailyStats().completedSchedules()).isEqualTo(2);
            assertThat(response.dailyStats().totalFocusTime()).isEqualTo(3000); // 1200 + 1800

            verify(dailyStatisticsRepository).save(existingStats);
        }

        @Test
        @DisplayName("실패 기록 - completed=false인 경우")
        void should_RecordIncomplete_When_NotCompleted() {
            // Given
            CompletionRequest request = new CompletionRequest(
                    SCHEDULE_CLIENT_ID,
                    false, // 완료 안됨
                    600,
                    false,
                    TEST_DATE
            );

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(dailyStatisticsRepository.findByUserIdAndDate(VALID_USER_ID, TEST_DATE))
                    .willReturn(Optional.empty());
            given(dailyStatisticsRepository.save(any(DailyStatistics.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            CompletionResponse response = statsService.recordCompletion(request, VALID_USER_ID);

            // Then
            assertThat(response.dailyStats().totalSchedules()).isEqualTo(1);
            assertThat(response.dailyStats().completedSchedules()).isEqualTo(0); // 완료 안됨
            assertThat(response.dailyStats().totalFocusTime()).isEqualTo(600);
        }

        @Test
        @DisplayName("올인 모드 기록 - allInMode=true인 경우")
        void should_RecordAllInMode_When_AllInModeTrue() {
            // Given
            CompletionRequest request = new CompletionRequest(
                    SCHEDULE_CLIENT_ID,
                    true,
                    3600,
                    true, // 올인 모드
                    TEST_DATE
            );

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(dailyStatisticsRepository.findByUserIdAndDate(VALID_USER_ID, TEST_DATE))
                    .willReturn(Optional.empty());
            given(dailyStatisticsRepository.save(any(DailyStatistics.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            CompletionResponse response = statsService.recordCompletion(request, VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.recorded()).isTrue();
            // 올인 모드 사용이 기록됨 (DailyStatistics 내부 상태)
        }

        @Test
        @DisplayName("USER_NOT_FOUND - 사용자를 찾을 수 없음")
        void should_ThrowException_When_UserNotFound() {
            // Given
            CompletionRequest request = new CompletionRequest(
                    SCHEDULE_CLIENT_ID,
                    true,
                    1800,
                    false,
                    TEST_DATE
            );

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> statsService.recordCompletion(request, VALID_USER_ID))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException businessException = (BusinessException) ex;
                        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                    });

            verify(dailyStatisticsRepository, never()).save(any());
        }
    }

    // ========== recordForceQuit 테스트 ==========

    @Nested
    @DisplayName("recordForceQuit 메서드")
    class RecordForceQuit {

        @Test
        @DisplayName("강제 종료 기록 - 신규 생성")
        void should_CreateNewRecordWithForceQuit_When_NoExistingRecord() {
            // Given
            // 2026-02-12 14:30:00 KST
            Instant timestamp = Instant.parse("2026-02-12T05:30:00Z");
            ForceQuitRequest request = new ForceQuitRequest(timestamp);

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(dailyStatisticsRepository.findByUserIdAndDate(eq(VALID_USER_ID), any(LocalDate.class)))
                    .willReturn(Optional.empty());
            given(dailyStatisticsRepository.save(any(DailyStatistics.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            ForceQuitResponse response = statsService.recordForceQuit(request, VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.recorded()).isTrue();
            assertThat(response.forceQuitCount()).isEqualTo(1);

            verify(dailyStatisticsRepository).save(any(DailyStatistics.class));
        }

        @Test
        @DisplayName("강제 종료 누적 - 기존 기록에 추가")
        void should_IncrementForceQuitCount_When_ExistingRecord() {
            // Given
            Instant timestamp = Instant.parse("2026-02-12T05:30:00Z");
            ForceQuitRequest request = new ForceQuitRequest(timestamp);

            DailyStatistics existingStats = DailyStatistics.builder()
                    .user(testUser)
                    .date(TEST_DATE)
                    .build();
            existingStats.incrementForceQuit(); // 기존 1회

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.of(testUser));
            given(dailyStatisticsRepository.findByUserIdAndDate(eq(VALID_USER_ID), any(LocalDate.class)))
                    .willReturn(Optional.of(existingStats));
            given(dailyStatisticsRepository.save(any(DailyStatistics.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            ForceQuitResponse response = statsService.recordForceQuit(request, VALID_USER_ID);

            // Then
            assertThat(response.forceQuitCount()).isEqualTo(2); // 1 + 1
            assertThat(existingStats.getStatus()).isEqualTo(DailyStatus.FAILURE);
        }

        @Test
        @DisplayName("USER_NOT_FOUND - 사용자를 찾을 수 없음")
        void should_ThrowException_When_UserNotFound() {
            // Given
            ForceQuitRequest request = new ForceQuitRequest(Instant.now());

            given(userRepository.findById(VALID_USER_ID))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> statsService.recordForceQuit(request, VALID_USER_ID))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException businessException = (BusinessException) ex;
                        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                    });

            verify(dailyStatisticsRepository, never()).save(any());
        }
    }

    // ========== getSummary 테스트 ==========

    @Nested
    @DisplayName("getSummary 메서드")
    class GetSummary {

        @Test
        @DisplayName("빈 통계 - 기록이 없는 경우")
        void should_ReturnZeroStats_When_NoRecords() {
            // Given
            given(dailyStatisticsRepository.findRecentByUserIdOrderByDateDesc(eq(VALID_USER_ID), any(LocalDate.class)))
                    .willReturn(Collections.emptyList());
            given(dailyStatisticsRepository.findByUserIdAndDateBetweenOrderByDateDesc(eq(VALID_USER_ID), any(LocalDate.class), any(LocalDate.class)))
                    .willReturn(Collections.emptyList());

            // When
            StatsSummaryResponse response = statsService.getSummary(VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.currentStreak()).isZero();
            assertThat(response.longestStreak()).isZero();
            assertThat(response.weeklyCompletionRate()).isZero();
            assertThat(response.monthlyCompletionRate()).isZero();
            assertThat(response.totalFocusTimeThisWeek()).isZero();
            assertThat(response.totalFocusTimeThisMonth()).isZero();
            assertThat(response.recentDays()).isEmpty();
        }

        @Test
        @DisplayName("통계 요약 - 정상 조회")
        void should_ReturnCorrectStats_When_HasRecords() {
            // Given
            LocalDate today = LocalDate.now();
            
            DailyStatistics stat1 = createDailyStatistics(today, 3, 3, 5400, DailyStatus.SUCCESS);
            DailyStatistics stat2 = createDailyStatistics(today.minusDays(1), 4, 3, 4200, DailyStatus.WARNING);

            given(dailyStatisticsRepository.findRecentByUserIdOrderByDateDesc(eq(VALID_USER_ID), any(LocalDate.class)))
                    .willReturn(List.of(stat1, stat2));
            given(dailyStatisticsRepository.findByUserIdAndDateBetweenOrderByDateDesc(eq(VALID_USER_ID), any(LocalDate.class), any(LocalDate.class)))
                    .willReturn(List.of(stat1, stat2));

            // When
            StatsSummaryResponse response = statsService.getSummary(VALID_USER_ID);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.totalFocusTimeThisWeek()).isEqualTo(9600); // 5400 + 4200
            assertThat(response.recentDays()).hasSize(2);
            
            // 완료율 계산: (3 + 3) / (3 + 4) = 6/7 ≈ 0.857
            assertThat(response.weeklyCompletionRate()).isCloseTo(0.857, within(0.01));
        }

        @Test
        @DisplayName("스트릭 계산 - 연속 성공")
        void should_CalculateStreak_When_ConsecutiveSuccess() {
            // Given
            LocalDate today = LocalDate.now();
            
            DailyStatistics stat1 = createDailyStatistics(today, 2, 2, 3600, DailyStatus.SUCCESS);
            DailyStatistics stat2 = createDailyStatistics(today.minusDays(1), 2, 2, 3600, DailyStatus.SUCCESS);
            DailyStatistics stat3 = createDailyStatistics(today.minusDays(2), 2, 2, 3600, DailyStatus.SUCCESS);

            List<DailyStatistics> stats = List.of(stat1, stat2, stat3);

            given(dailyStatisticsRepository.findRecentByUserIdOrderByDateDesc(eq(VALID_USER_ID), any(LocalDate.class)))
                    .willReturn(stats);
            given(dailyStatisticsRepository.findByUserIdAndDateBetweenOrderByDateDesc(eq(VALID_USER_ID), any(LocalDate.class), any(LocalDate.class)))
                    .willReturn(stats);

            // When
            StatsSummaryResponse response = statsService.getSummary(VALID_USER_ID);

            // Then
            assertThat(response.currentStreak()).isEqualTo(3);
            assertThat(response.longestStreak()).isEqualTo(3);
        }

        /**
         * 테스트용 DailyStatistics 생성 헬퍼 메서드
         */
        private DailyStatistics createDailyStatistics(
                LocalDate date, 
                int totalSchedules, 
                int completedSchedules, 
                int totalFocusTime,
                DailyStatus status) {
            
            DailyStatistics stats = DailyStatistics.builder()
                    .user(testUser)
                    .date(date)
                    .build();
            
            // 리플렉션으로 필드 설정 (테스트용)
            ReflectionTestUtils.setField(stats, "totalSchedules", totalSchedules);
            ReflectionTestUtils.setField(stats, "completedSchedules", completedSchedules);
            ReflectionTestUtils.setField(stats, "totalFocusTime", totalFocusTime);
            ReflectionTestUtils.setField(stats, "status", status);
            
            return stats;
        }
    }
}
