package com.wombat.screenlock.unwind_be.domain.user.repository;

import com.wombat.screenlock.unwind_be.domain.user.entity.Role;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 단위 테스트
 * 
 * <p>@DataJpaTest를 사용하여 JPA 관련 컴포넌트만 로드합니다.
 * 테스트 프로파일에서는 H2 인메모리 DB를 사용합니다.</p>
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 ID 조회 성공")
    void should_SaveAndFindById_When_ValidUser() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .passwordHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
            .role(Role.USER)
            .build();

        // When
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void should_FindByEmail_When_UserExists() {
        // Given
        String email = "findme@example.com";
        User user = User.builder()
            .email(email)
            .passwordHash("$2a$10$hashedPassword")
            .role(Role.USER)
            .build();
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional 반환")
    void should_ReturnEmpty_When_EmailNotExists() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("notexists@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하는 경우")
    void should_ReturnTrue_When_EmailExists() {
        // Given
        String email = "exists@example.com";
        User user = User.builder()
            .email(email)
            .passwordHash("$2a$10$hashedPassword")
            .build();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않는 경우")
    void should_ReturnFalse_When_EmailNotExists() {
        // When
        boolean exists = userRepository.existsByEmail("notexists@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자 Role 기본값 검증 - null인 경우 USER로 설정")
    void should_SetDefaultRole_When_RoleIsNull() {
        // Given
        User user = User.builder()
            .email("defaultrole@example.com")
            .passwordHash("$2a$10$hashedPassword")
            .role(null)  // null 전달
            .build();

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("BaseTimeEntity 필드 자동 설정 검증")
    void should_SetCreatedAtAndUpdatedAt_When_Save() {
        // Given
        User user = User.builder()
            .email("timestamp@example.com")
            .passwordHash("$2a$10$hashedPassword")
            .build();

        // When
        User savedUser = userRepository.save(user);
        userRepository.flush();  // 즉시 DB 반영

        // Then
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("비밀번호 변경 메서드 동작 검증")
    void should_UpdatePassword_When_Called() {
        // Given
        User user = User.builder()
            .email("pwchange@example.com")
            .passwordHash("$2a$10$oldPassword")
            .build();
        User savedUser = userRepository.save(user);

        // When
        String newPasswordHash = "$2a$10$newPasswordHash";
        savedUser.updatePassword(newPasswordHash);
        userRepository.flush();

        // Then
        Optional<User> updatedUser = userRepository.findById(savedUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getPasswordHash()).isEqualTo(newPasswordHash);
    }

    @Test
    @DisplayName("권한 변경 메서드 동작 검증")
    void should_ChangeRole_When_Called() {
        // Given
        User user = User.builder()
            .email("rolechange@example.com")
            .passwordHash("$2a$10$hashedPassword")
            .role(Role.USER)
            .build();
        User savedUser = userRepository.save(user);

        // When
        savedUser.changeRole(Role.ADMIN);
        userRepository.flush();

        // Then
        Optional<User> updatedUser = userRepository.findById(savedUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getRole()).isEqualTo(Role.ADMIN);
    }
}

