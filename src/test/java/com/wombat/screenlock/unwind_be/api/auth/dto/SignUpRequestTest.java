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
 * SignUpRequest Validation 테스트
 * 
 * <p>회원가입 요청 DTO의 유효성 검증 로직을 테스트합니다.</p>
 */
@DisplayName("SignUpRequest Validation 테스트")
class SignUpRequestTest {

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
        SignUpRequest request = new SignUpRequest("test@example.com", "password123");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이메일이 null이면 검증 실패해야 함")
    void should_FailValidation_When_EmailIsNull() {
        // Given
        SignUpRequest request = new SignUpRequest(null, "password123");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("이메일은 필수입니다");
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 검증 실패해야 함")
    void should_FailValidation_When_EmailIsBlank() {
        // Given
        SignUpRequest request = new SignUpRequest("", "password123");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("이메일은 필수입니다");
    }

    @Test
    @DisplayName("이메일 형식이 잘못되면 검증 실패해야 함")
    void should_FailValidation_When_InvalidEmail() {
        // Given
        SignUpRequest request = new SignUpRequest("invalid-email", "password123");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("유효한 이메일 형식이 아닙니다");
    }

    @Test
    @DisplayName("이메일이 255자를 초과하면 검증 실패해야 함")
    void should_FailValidation_When_EmailExceedsMaxLength() {
        // Given
        // 255자 초과 이메일 (유효한 형식이지만 길이 초과)
        String longEmail = "a".repeat(244) + "@example.com"; // 244 + 12 = 256자
        SignUpRequest request = new SignUpRequest(longEmail, "password123");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        // @Email 검증이 먼저 실행되거나 @Size 검증이 실행될 수 있음
        assertThat(violations).isNotEmpty();
        // 길이 검증 또는 이메일 형식 검증 중 하나는 실패해야 함
        boolean hasLengthViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("255자를 초과"));
        boolean hasEmailViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("이메일 형식"));
        assertThat(hasLengthViolation || hasEmailViolation).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 null이면 검증 실패해야 함")
    void should_FailValidation_When_PasswordIsNull() {
        // Given
        SignUpRequest request = new SignUpRequest("test@example.com", null);

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("비밀번호는 필수입니다");
    }

    @Test
    @DisplayName("비밀번호가 빈 문자열이면 검증 실패해야 함")
    void should_FailValidation_When_PasswordIsBlank() {
        // Given
        SignUpRequest request = new SignUpRequest("test@example.com", "");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        // @NotBlank와 @Size(min=8) 둘 다 위반될 수 있음
        assertThat(violations).isNotEmpty();
        boolean hasBlankViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("비밀번호는 필수입니다"));
        boolean hasSizeViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("8~50자"));
        assertThat(hasBlankViolation || hasSizeViolation).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 7자 이하면 검증 실패해야 함")
    void should_FailValidation_When_PasswordTooShort() {
        // Given
        SignUpRequest request = new SignUpRequest("test@example.com", "short");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("비밀번호는 8~50자여야 합니다");
    }

    @Test
    @DisplayName("비밀번호가 51자 이상이면 검증 실패해야 함")
    void should_FailValidation_When_PasswordTooLong() {
        // Given
        String longPassword = "a".repeat(51);
        SignUpRequest request = new SignUpRequest("test@example.com", longPassword);

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("비밀번호는 8~50자여야 합니다");
    }

    @Test
    @DisplayName("비밀번호가 정확히 8자이면 검증 통과해야 함")
    void should_PassValidation_When_PasswordExactly8Chars() {
        // Given
        SignUpRequest request = new SignUpRequest("test@example.com", "12345678");

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("비밀번호가 정확히 50자이면 검증 통과해야 함")
    void should_PassValidation_When_PasswordExactly50Chars() {
        // Given
        String password = "a".repeat(50);
        SignUpRequest request = new SignUpRequest("test@example.com", password);

        // When
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }
}


