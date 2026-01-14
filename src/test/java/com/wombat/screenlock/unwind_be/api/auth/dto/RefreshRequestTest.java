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
 * RefreshRequest Validation 테스트
 * 
 * <p>토큰 갱신 요청 DTO의 유효성 검증 로직을 테스트합니다.</p>
 */
@DisplayName("RefreshRequest Validation 테스트")
class RefreshRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 Refresh Token으로 검증 통과해야 함")
    void should_PassValidation_When_ValidToken() {
        // Given
        RefreshRequest request = new RefreshRequest("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        // When
        Set<ConstraintViolation<RefreshRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Refresh Token이 null이면 검증 실패해야 함")
    void should_FailValidation_When_TokenIsNull() {
        // Given
        RefreshRequest request = new RefreshRequest(null);

        // When
        Set<ConstraintViolation<RefreshRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("Refresh Token은 필수입니다");
    }

    @Test
    @DisplayName("Refresh Token이 빈 문자열이면 검증 실패해야 함")
    void should_FailValidation_When_TokenIsBlank() {
        // Given
        RefreshRequest request = new RefreshRequest("");

        // When
        Set<ConstraintViolation<RefreshRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("Refresh Token은 필수입니다");
    }

    @Test
    @DisplayName("Refresh Token이 공백만 있으면 검증 실패해야 함")
    void should_FailValidation_When_TokenIsWhitespace() {
        // Given
        RefreshRequest request = new RefreshRequest("   ");

        // When
        Set<ConstraintViolation<RefreshRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .contains("Refresh Token은 필수입니다");
    }
}


