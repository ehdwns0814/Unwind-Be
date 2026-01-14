package com.wombat.screenlock.unwind_be.domain.schedule.repository;

import com.wombat.screenlock.unwind_be.domain.schedule.entity.Schedule;
import com.wombat.screenlock.unwind_be.domain.user.entity.Role;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.config.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ScheduleRepository 통합 테스트
 * 
 * <p>@DataJpaTest를 사용하여 JPA 관련 컴포넌트만 로드합니다.
 * 테스트 프로파일에서는 H2 인메모리 DB를 사용합니다.</p>
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@Import(JpaAuditingConfig.class)
@DisplayName("ScheduleRepository 테스트")
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("스케줄 저장 및 ID 조회 성공")
    void should_SaveAndFindById_When_ValidSchedule() {
        // Given
        User user = createTestUser("test@example.com");
        Schedule schedule = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440000")
            .name("집중 스터디")
            .duration(60)
            .user(user)
            .build();

        // When
        Schedule savedSchedule = scheduleRepository.save(schedule);
        Optional<Schedule> foundSchedule = scheduleRepository.findById(savedSchedule.getId());

        // Then
        assertThat(savedSchedule.getId()).isNotNull();
        assertThat(foundSchedule).isPresent();
        assertThat(foundSchedule.get().getName()).isEqualTo("집중 스터디");
        assertThat(foundSchedule.get().getDuration()).isEqualTo(60);
        assertThat(foundSchedule.get().getClientId()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
    }

    @Test
    @DisplayName("클라이언트 ID로 스케줄 조회 성공")
    void should_FindByClientId_When_ScheduleExists() {
        // Given
        User user = createTestUser("findme@example.com");
        String clientId = "550e8400-e29b-41d4-a716-446655440001";
        Schedule schedule = Schedule.builder()
            .clientId(clientId)
            .name("아침 운동")
            .duration(30)
            .user(user)
            .build();
        scheduleRepository.save(schedule);

        // When
        Optional<Schedule> foundSchedule = scheduleRepository.findByClientId(clientId);

        // Then
        assertThat(foundSchedule).isPresent();
        assertThat(foundSchedule.get().getClientId()).isEqualTo(clientId);
        assertThat(foundSchedule.get().getName()).isEqualTo("아침 운동");
    }

    @Test
    @DisplayName("존재하지 않는 클라이언트 ID로 조회 시 빈 Optional 반환")
    void should_ReturnEmpty_When_ClientIdNotExists() {
        // When
        Optional<Schedule> foundSchedule = scheduleRepository.findByClientId("not-exists-uuid");

        // Then
        assertThat(foundSchedule).isEmpty();
    }

    @Test
    @DisplayName("클라이언트 ID 존재 여부 확인 - 존재하는 경우")
    void should_ReturnTrue_When_ClientIdExists() {
        // Given
        User user = createTestUser("exists@example.com");
        String clientId = "550e8400-e29b-41d4-a716-446655440002";
        Schedule schedule = Schedule.builder()
            .clientId(clientId)
            .name("저녁 독서")
            .duration(45)
            .user(user)
            .build();
        scheduleRepository.save(schedule);

        // When
        boolean exists = scheduleRepository.existsByClientId(clientId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("클라이언트 ID 존재 여부 확인 - 존재하지 않는 경우")
    void should_ReturnFalse_When_ClientIdNotExists() {
        // When
        boolean exists = scheduleRepository.existsByClientId("not-exists-uuid");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자 ID로 스케줄 목록 조회 성공")
    void should_FindByUserId_When_SchedulesExist() {
        // Given
        User user = createTestUser("list@example.com");
        Schedule schedule1 = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440003")
            .name("스케줄 1")
            .duration(30)
            .user(user)
            .build();
        Schedule schedule2 = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440004")
            .name("스케줄 2")
            .duration(60)
            .user(user)
            .build();
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        // When
        List<Schedule> schedules = scheduleRepository.findByUserId(user.getId());

        // Then
        assertThat(schedules).hasSize(2);
        assertThat(schedules).extracting(Schedule::getName)
            .containsExactlyInAnyOrder("스케줄 1", "스케줄 2");
    }

    @Test
    @DisplayName("사용자별 스케줄 목록 조회 (페이징)")
    void should_FindByUserIdWithPaging_When_SchedulesExist() {
        // Given
        User user = createTestUser("paging@example.com");
        for (int i = 1; i <= 5; i++) {
            Schedule schedule = Schedule.builder()
                .clientId("550e8400-e29b-41d4-a716-44665544000" + i)
                .name("스케줄 " + i)
                .duration(30)
                .user(user)
                .build();
            scheduleRepository.save(schedule);
        }

        // When
        Page<Schedule> page1 = scheduleRepository.findByUserId(user.getId(), PageRequest.of(0, 2));
        Page<Schedule> page2 = scheduleRepository.findByUserId(user.getId(), PageRequest.of(1, 2));

        // Then
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(5);
        assertThat(page1.getTotalPages()).isEqualTo(3);
        assertThat(page2.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("사용자의 스케줄 개수 조회")
    void should_CountByUserId_When_SchedulesExist() {
        // Given
        User user = createTestUser("count@example.com");
        Schedule schedule1 = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440005")
            .name("스케줄 1")
            .duration(30)
            .user(user)
            .build();
        Schedule schedule2 = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440006")
            .name("스케줄 2")
            .duration(60)
            .user(user)
            .build();
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        // When
        long count = scheduleRepository.countByUserId(user.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("여러 클라이언트 ID로 스케줄 일괄 조회")
    void should_FindByClientIdIn_When_ClientIdsExist() {
        // Given
        User user = createTestUser("bulk@example.com");
        String clientId1 = "550e8400-e29b-41d4-a716-446655440007";
        String clientId2 = "550e8400-e29b-41d4-a716-446655440008";
        String clientId3 = "550e8400-e29b-41d4-a716-446655440009";
        
        Schedule schedule1 = Schedule.builder()
            .clientId(clientId1)
            .name("스케줄 1")
            .duration(30)
            .user(user)
            .build();
        Schedule schedule2 = Schedule.builder()
            .clientId(clientId2)
            .name("스케줄 2")
            .duration(60)
            .user(user)
            .build();
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        // When
        List<Schedule> schedules = scheduleRepository.findByClientIdIn(
            List.of(clientId1, clientId2, clientId3)
        );

        // Then
        assertThat(schedules).hasSize(2);
        assertThat(schedules).extracting(Schedule::getClientId)
            .containsExactlyInAnyOrder(clientId1, clientId2);
    }

    @Test
    @DisplayName("사용자 ID로 스케줄 목록 조회 (User Fetch Join)")
    void should_FindByUserIdWithUser_When_SchedulesExist() {
        // Given
        User user = createTestUser("fetch@example.com");
        Schedule schedule = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440010")
            .name("Fetch Join 테스트")
            .duration(30)
            .user(user)
            .build();
        scheduleRepository.save(schedule);
        scheduleRepository.flush();

        // When
        List<Schedule> schedules = scheduleRepository.findByUserIdWithUser(user.getId());

        // Then
        assertThat(schedules).hasSize(1);
        assertThat(schedules.get(0).getUser()).isNotNull();
        assertThat(schedules.get(0).getUser().getEmail()).isEqualTo("fetch@example.com");
    }

    @Test
    @DisplayName("BaseTimeEntity 필드 자동 설정 검증")
    void should_SetCreatedAtAndUpdatedAt_When_Save() {
        // Given
        User user = createTestUser("timestamp@example.com");
        Schedule schedule = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440011")
            .name("타임스탬프 테스트")
            .duration(30)
            .user(user)
            .build();

        // When
        Schedule savedSchedule = scheduleRepository.save(schedule);
        scheduleRepository.flush();

        // Then
        assertThat(savedSchedule.getCreatedAt()).isNotNull();
        assertThat(savedSchedule.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("스케줄 정보 수정 메서드 동작 검증")
    void should_UpdateSchedule_When_UpdateCalled() {
        // Given
        User user = createTestUser("update@example.com");
        Schedule schedule = Schedule.builder()
            .clientId("550e8400-e29b-41d4-a716-446655440012")
            .name("원래 이름")
            .duration(30)
            .user(user)
            .build();
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // When
        savedSchedule.update("새 이름", 60);
        scheduleRepository.flush();

        // Then
        Optional<Schedule> updatedSchedule = scheduleRepository.findById(savedSchedule.getId());
        assertThat(updatedSchedule).isPresent();
        assertThat(updatedSchedule.get().getName()).isEqualTo("새 이름");
        assertThat(updatedSchedule.get().getDuration()).isEqualTo(60);
    }

    // Note: CASCADE DELETE 테스트는 H2에서 제대로 작동하지 않을 수 있습니다.
    // 실제 MySQL 환경에서 통합 테스트로 검증하는 것이 적절합니다.
    // FK 제약조건에 ON DELETE CASCADE가 설정되어 있으므로 DB 레벨에서 작동합니다.

    // ========== 헬퍼 메서드 ==========

    /**
     * 테스트용 User 생성
     */
    private User createTestUser(String email) {
        User user = User.builder()
            .email(email)
            .passwordHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
            .role(Role.USER)
            .build();
        return userRepository.save(user);
    }
}

