package com.wombat.screenlock.unwind_be.domain.stats.repository;

import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 일별 통계 Repository
 */
public interface DailyStatisticsRepository extends JpaRepository<DailyStatistics, Long> {

    /**
     * 사용자 ID와 날짜로 통계 조회 (UPSERT용)
     */
    Optional<DailyStatistics> findByUserIdAndDate(Long userId, LocalDate date);

    /**
     * 기간별 통계 조회 (주간/월간 요약용)
     */
    List<DailyStatistics> findByUserIdAndDateBetweenOrderByDateDesc(
        Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 스트릭 계산용 - 최근 연속 성공 일수 조회
     */
    @Query("""
        SELECT ds FROM DailyStatistics ds 
        WHERE ds.user.id = :userId 
        AND ds.date <= :today 
        ORDER BY ds.date DESC
        """)
    List<DailyStatistics> findRecentByUserIdOrderByDateDesc(
        @Param("userId") Long userId, 
        @Param("today") LocalDate today);
}
