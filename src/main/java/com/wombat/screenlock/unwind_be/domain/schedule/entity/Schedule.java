package com.wombat.screenlock.unwind_be.domain.schedule.entity;

import com.wombat.screenlock.unwind_be.domain.common.BaseTimeEntity;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 스케줄 엔티티
 * 
 * <p>사용자의 집중 스케줄 정보를 저장합니다.
 * iOS 앱에서 생성한 clientId를 통해 동기화 식별자 역할을 합니다.</p>
 * 
 * <h3>테이블 정보</h3>
 * <ul>
 *   <li>테이블명: schedules</li>
 *   <li>PK: id (AUTO_INCREMENT)</li>
 *   <li>UK: client_id (Unique Index)</li>
 *   <li>FK: user_id → users.id</li>
 * </ul>
 * 
 * @see User
 * @see BaseTimeEntity
 */
@Entity
@Table(name = "schedules", indexes = {
    @Index(name = "uk_schedules_client_id", columnList = "client_id", unique = true),
    @Index(name = "idx_schedules_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseTimeEntity {

    /**
     * 스케줄 고유 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 클라이언트 동기화 ID
     * <p>iOS 앱에서 생성한 UUID (36자)</p>
     */
    @Column(name = "client_id", nullable = false, unique = true, length = 36)
    private String clientId;

    /**
     * 스케줄 이름
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 집중 시간 (분 단위)
     * <p>1분 ~ 480분 (8시간) 범위</p>
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * 소유 사용자
     * <p>N:1 관계, Lazy Loading 적용</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Schedule 엔티티 생성자
     * 
     * @param clientId iOS에서 생성한 UUID
     * @param name 스케줄 이름
     * @param duration 집중 시간 (분)
     * @param user 소유 사용자
     */
    @Builder
    public Schedule(String clientId, String name, Integer duration, User user) {
        this.clientId = clientId;
        this.name = name;
        this.duration = duration;
        this.user = user;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 스케줄 정보 수정
     * 
     * @param name 새 스케줄 이름
     * @param duration 새 집중 시간 (분)
     */
    public void update(String name, Integer duration) {
        this.name = name;
        this.duration = duration;
    }
}

