package com.wombat.screenlock.unwind_be.api.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginRequest Validation 테스트
 * 
 * <p>로그인 요청 DTO의 유효성 검증 로직을 테스트합니다.</p>
 */
@DisplayName("LoginRequest Validation 테스트")
class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 이메일과 비밀번호로 검증 통과해야 함")
    void should_PassValidation_When_ValidInput() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이메일이 null이면 검증 실패해야 함")
    void should_FailValidation_When_EmailIsNull() {
        // Given
        LoginRequest request = new LoginRequest(null, "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("이메일은 필수입니다");
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 검증 실패해야 함")
    void should_FailValidation_When_EmailIsBlank() {
        // Given
        LoginRequest request = new LoginRequest("", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("이메일은 필수입니다");
    }

    @Test
    @DisplayName("이메일 형식이 잘못되면 검증 실패해야 함")
    void should_FailValidation_When_InvalidEmail() {
        // Given
        LoginRequest request = new LoginRequest("invalid-email", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("유효한 이메일 형식이 아닙니다");
    }

    @Test
    @DisplayName("비밀번호가 null이면 검증 실패해야 함")
    void should_FailValidation_When_PasswordIsNull() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", null);

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("비밀번호는 필수입니다");
    }

    @Test
    @DisplayName("비밀번호가 빈 문자열이면 검증 실패해야 함")
    void should_FailValidation_When_PasswordIsBlank() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("비밀번호는 필수입니다");
    }
}


