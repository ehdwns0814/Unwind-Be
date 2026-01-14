package com.wombat.screenlock.unwind_be.domain.schedule.repository;

import com.wombat.screenlock.unwind_be.domain.schedule.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Schedule 엔티티 Repository
 * 
 * <p>JPA 기반 데이터 접근 계층으로, Spring Data JPA Query Method를 활용합니다.</p>
 * 
 * <h3>제공 기능</h3>
 * <ul>
 *   <li>기본 CRUD (JpaRepository 상속)</li>
 *   <li>클라이언트 ID로 스케줄 조회</li>
 *   <li>사용자별 스케줄 목록 조회</li>
 * </ul>
 * 
 * @see Schedule
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // ========== 클라이언트 ID 기반 조회 (iOS 동기화) ==========

    /**
     * 클라이언트 ID로 스케줄 조회
     * 
     * <p>iOS 앱 동기화 시 사용</p>
     * 
     * @param clientId iOS에서 생성한 UUID
     * @return 스케줄 Optional (존재하지 않으면 empty)
     */
    Optional<Schedule> findByClientId(String clientId);

    /**
     * 클라이언트 ID 존재 여부 확인
     * 
     * <p>스케줄 생성 시 중복 체크에 사용</p>
     * 
     * @param clientId 확인할 클라이언트 ID
     * @return 존재 여부 (true: 존재, false: 미존재)
     */
    boolean existsByClientId(String clientId);

    // ========== 사용자 기반 조회 ==========

    /**
     * 사용자 ID로 스케줄 목록 조회
     * 
     * @param userId 사용자 ID
     * @return 스케줄 목록
     */
    List<Schedule> findByUserId(Long userId);

    /**
     * 사용자별 스케줄 목록 조회 (페이징)
     * 
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 스케줄 목록
     */
    Page<Schedule> findByUserId(Long userId, Pageable pageable);

    /**
     * 사용자의 스케줄 개수 조회
     * 
     * @param userId 사용자 ID
     * @return 스케줄 개수
     */
    long countByUserId(Long userId);

    // ========== 벌크 조회 (동기화용) ==========

    /**
     * 여러 클라이언트 ID로 스케줄 일괄 조회
     * 
     * <p>iOS 앱 일괄 동기화 시 사용</p>
     * 
     * @param clientIds 클라이언트 ID 목록
     * @return 스케줄 목록
     */
    @Query("SELECT s FROM Schedule s WHERE s.clientId IN :clientIds")
    List<Schedule> findByClientIdIn(@Param("clientIds") List<String> clientIds);

    // ========== 사용자 + User 조인 조회 (N+1 방지) ==========

    /**
     * 사용자 ID로 스케줄 목록 조회 (User Fetch Join)
     * 
     * <p>N+1 문제 방지를 위해 User를 함께 조회</p>
     * 
     * @param userId 사용자 ID
     * @return 스케줄 목록 (User 포함)
     */
    @Query("SELECT s FROM Schedule s JOIN FETCH s.user WHERE s.user.id = :userId")
    List<Schedule> findByUserIdWithUser(@Param("userId") Long userId);
}

