package com.wombat.screenlock.unwind_be.application.stats;

import com.wombat.screenlock.unwind_be.api.stats.dto.*;
import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatistics;
import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatus;
import com.wombat.screenlock.unwind_be.domain.stats.repository.DailyStatisticsRepository;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.global.exception.BusinessException;
import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 통계 서비스
 * 
 * <p>사용자의 집중 통계 데이터를 수집하고 조회하는 비즈니스 로직을 담당합니다.</p>
 * 
 * <h3>주요 기능</h3>
 * <ul>
 *   <li>완료 통계 기록 (UPSERT)</li>
 *   <li>강제 종료 카운트 기록</li>
 *   <li>통계 요약 조회 (스트릭, 완료율, 집중 시간)</li>
 * </ul>
 * 
 * <h3>UPSERT 로직</h3>
 * <p>userId + date 조합으로 기존 레코드가 있으면 누적하고,
 * 없으면 새 레코드를 생성합니다.</p>
 * 
 * @see DailyStatisticsRepository
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StatsService {

    private final DailyStatisticsRepository dailyStatisticsRepository;
    private final UserRepository userRepository;

    /**
     * 완료 통계 기록 (UPSERT)
     * 
     * <h3>비즈니스 로직 순서</h3>
     * <ol>
     *   <li>userId로 User 엔티티 조회</li>
     *   <li>userId + date로 기존 DailyStatistics 조회</li>
     *   <li>존재하면 누적 업데이트, 없으면 신규 생성</li>
     *   <li>저장 후 응답 반환</li>
     * </ol>
     * 
     * @param request 완료 통계 요청 DTO
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return CompletionResponse 기록 결과
     * @throws BusinessException USER_NOT_FOUND - 사용자를 찾을 수 없음
     */
    @Transactional
    public CompletionResponse recordCompletion(CompletionRequest request, Long userId) {
        log.info("완료 통계 기록 - userId={}, date={}, completed={}, focusTime={}",
                userId, request.date(), request.completed(), request.focusTime());

        // 1. User 엔티티 조회
        User user = findUserById(userId);

        // 2. 기존 레코드 조회 또는 신규 생성 (UPSERT)
        DailyStatistics dailyStats = dailyStatisticsRepository
                .findByUserIdAndDate(userId, request.date())
                .orElseGet(() -> createNewDailyStatistics(user, request.date()));

        // 3. 통계 누적
        dailyStats.recordCompletion(
                request.completed(),
                request.focusTime(),
                request.isAllInMode()
        );

        // 4. 저장
        DailyStatistics saved = dailyStatisticsRepository.save(dailyStats);

        log.info("완료 통계 기록 완료 - userId={}, date={}, totalSchedules={}, completedSchedules={}",
                userId, saved.getDate(), saved.getTotalSchedules(), saved.getCompletedSchedules());

        return CompletionResponse.success(DailyStatsDto.from(saved));
    }

    /**
     * 강제 종료 카운트 기록
     * 
     * <h3>비즈니스 로직 순서</h3>
     * <ol>
     *   <li>timestamp에서 날짜 추출 (KST 기준)</li>
     *   <li>userId + date로 기존 DailyStatistics 조회</li>
     *   <li>존재하면 forceQuitCount 증가, 없으면 신규 생성</li>
     *   <li>저장 후 응답 반환</li>
     * </ol>
     * 
     * @param request 강제 종료 요청 DTO
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return ForceQuitResponse 기록 결과
     * @throws BusinessException USER_NOT_FOUND - 사용자를 찾을 수 없음
     */
    @Transactional
    public ForceQuitResponse recordForceQuit(ForceQuitRequest request, Long userId) {
        // 1. timestamp에서 날짜 추출 (KST 기준)
        LocalDate date = request.timestamp()
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDate();

        log.info("강제 종료 기록 - userId={}, date={}, timestamp={}", userId, date, request.timestamp());

        // 2. User 엔티티 조회
        User user = findUserById(userId);

        // 3. 기존 레코드 조회 또는 신규 생성 (UPSERT)
        DailyStatistics dailyStats = dailyStatisticsRepository
                .findByUserIdAndDate(userId, date)
                .orElseGet(() -> createNewDailyStatistics(user, date));

        // 4. 강제 종료 카운트 증가
        dailyStats.incrementForceQuit();

        // 5. 저장
        DailyStatistics saved = dailyStatisticsRepository.save(dailyStats);

        log.info("강제 종료 기록 완료 - userId={}, date={}, forceQuitCount={}",
                userId, saved.getDate(), saved.getForceQuitCount());

        return ForceQuitResponse.success(saved.getForceQuitCount());
    }

    /**
     * 사용자 통계 요약 조회
     * 
     * <h3>계산 항목</h3>
     * <ul>
     *   <li>현재/최장 스트릭 (연속 성공 일수)</li>
     *   <li>주간/월간 완료율</li>
     *   <li>주간/월간 총 집중 시간</li>
     *   <li>최근 7일 일별 통계</li>
     * </ul>
     * 
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return StatsSummaryResponse 통계 요약
     */
    public StatsSummaryResponse getSummary(Long userId) {
        log.info("통계 요약 조회 - userId={}", userId);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6); // 최근 7일
        LocalDate monthStart = today.minusDays(29); // 최근 30일

        // 1. 최근 통계 조회 (스트릭 계산용)
        List<DailyStatistics> recentStats = dailyStatisticsRepository
                .findRecentByUserIdOrderByDateDesc(userId, today);

        // 2. 스트릭 계산
        int currentStreak = calculateCurrentStreak(recentStats, today);
        int longestStreak = calculateLongestStreak(recentStats);

        // 3. 주간 통계
        List<DailyStatistics> weeklyStats = dailyStatisticsRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, weekStart, today);
        double weeklyCompletionRate = calculateCompletionRate(weeklyStats);
        int totalFocusTimeThisWeek = calculateTotalFocusTime(weeklyStats);

        // 4. 월간 통계
        List<DailyStatistics> monthlyStats = dailyStatisticsRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, monthStart, today);
        double monthlyCompletionRate = calculateCompletionRate(monthlyStats);
        int totalFocusTimeThisMonth = calculateTotalFocusTime(monthlyStats);

        // 5. 최근 7일 상세 (이미 weeklyStats에 포함)
        List<RecentDayDto> recentDays = weeklyStats.stream()
                .map(RecentDayDto::from)
                .toList();

        log.debug("통계 요약 조회 완료 - userId={}, currentStreak={}, weeklyRate={}",
                userId, currentStreak, weeklyCompletionRate);

        return StatsSummaryResponse.builder()
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .weeklyCompletionRate(weeklyCompletionRate)
                .monthlyCompletionRate(monthlyCompletionRate)
                .totalFocusTimeThisWeek(totalFocusTimeThisWeek)
                .totalFocusTimeThisMonth(totalFocusTimeThisMonth)
                .recentDays(recentDays)
                .build();
    }

    // ========== Private Helper Methods ==========

    /**
     * 사용자 ID로 User 엔티티 조회
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("통계 기록 실패: 사용자를 찾을 수 없음 - userId={}", userId);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
    }

    /**
     * 새로운 DailyStatistics 엔티티 생성
     */
    private DailyStatistics createNewDailyStatistics(User user, LocalDate date) {
        log.debug("새 일별 통계 생성 - userId={}, date={}", user.getId(), date);
        return DailyStatistics.builder()
                .user(user)
                .date(date)
                .build();
    }

    /**
     * 현재 스트릭 계산 (오늘부터 연속 성공 일수)
     * 
     * <p>SUCCESS 상태인 날이 연속으로 몇 일인지 계산합니다.
     * 오늘이 SUCCESS가 아니면 0을 반환합니다.</p>
     */
    private int calculateCurrentStreak(List<DailyStatistics> stats, LocalDate today) {
        if (stats.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate expectedDate = today;

        for (DailyStatistics stat : stats) {
            // 날짜가 연속적이지 않으면 종료
            if (!stat.getDate().equals(expectedDate)) {
                // 오늘이 아직 기록되지 않았으면 어제부터 확인
                if (streak == 0 && expectedDate.equals(today)) {
                    expectedDate = today.minusDays(1);
                    if (!stat.getDate().equals(expectedDate)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            // SUCCESS 상태만 스트릭에 포함
            if (stat.getStatus() == DailyStatus.SUCCESS) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * 최장 스트릭 계산
     * 
     * <p>전체 기록에서 가장 긴 연속 성공 일수를 계산합니다.</p>
     */
    private int calculateLongestStreak(List<DailyStatistics> stats) {
        if (stats.isEmpty()) {
            return 0;
        }

        int longest = 0;
        int current = 0;
        LocalDate previousDate = null;

        for (DailyStatistics stat : stats) {
            boolean isSuccess = stat.getStatus() == DailyStatus.SUCCESS;
            boolean isConsecutive = previousDate != null && 
                    stat.getDate().equals(previousDate.minusDays(1));

            if (isSuccess && (previousDate == null || isConsecutive)) {
                current++;
            } else if (isSuccess) {
                // 연속이 끊겼지만 새로운 시작
                current = 1;
            } else {
                current = 0;
            }

            longest = Math.max(longest, current);
            previousDate = stat.getDate();
        }

        return longest;
    }

    /**
     * 완료율 계산
     */
    private double calculateCompletionRate(List<DailyStatistics> stats) {
        if (stats.isEmpty()) {
            return 0.0;
        }

        int totalSchedules = stats.stream()
                .mapToInt(DailyStatistics::getTotalSchedules)
                .sum();
        int completedSchedules = stats.stream()
                .mapToInt(DailyStatistics::getCompletedSchedules)
                .sum();

        if (totalSchedules == 0) {
            return 0.0;
        }

        return (double) completedSchedules / totalSchedules;
    }

    /**
     * 총 집중 시간 계산
     */
    private int calculateTotalFocusTime(List<DailyStatistics> stats) {
        return stats.stream()
                .mapToInt(DailyStatistics::getTotalFocusTime)
                .sum();
    }
}
