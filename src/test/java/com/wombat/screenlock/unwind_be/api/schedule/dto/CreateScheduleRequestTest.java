package com.wombat.screenlock.unwind_be.api.schedule.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CreateScheduleRequest DTO Validation 테스트
 * 
 * <p>스케줄 생성 요청 DTO의 유효성 검증 로직을 테스트합니다.</p>
 * 
 * <h3>테스트 범위</h3>
 * <ul>
 *   <li>clientId: UUID 형식 검증</li>
 *   <li>name: 필수 및 길이 검증</li>
 *   <li>duration: 범위 검증 (1~480분)</li>
 * </ul>
 */
@DisplayName("CreateScheduleRequest DTO Validation 테스트")
class CreateScheduleRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== 유효한 요청 테스트 ==========

    @Test
    @DisplayName("유효한 요청 - 모든 필드가 올바른 경우 검증 통과")
    void should_PassValidation_When_AllFieldsAreValid() {
        // Given
        CreateScheduleRequest request = new CreateScheduleRequest(
                "550e8400-e29b-41d4-a716-446655440000",
                "아침 공부",
                60
        );

        // When
        Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    // ========== clientId 검증 테스트 ==========

    @Nested
    @DisplayName("clientId 필드 검증")
    class ClientIdValidation {

        @Test
        @DisplayName("clientId가 null인 경우 검증 실패")
        void should_FailValidation_When_ClientIdIsNull() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    null,
                    "아침 공부",
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("클라이언트 ID는 필수입니다");
        }

        @Test
        @DisplayName("clientId가 빈 문자열인 경우 검증 실패")
        void should_FailValidation_When_ClientIdIsEmpty() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "",
                    "아침 공부",
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("clientId가 UUID 형식이 아닌 경우 검증 실패")
        void should_FailValidation_When_ClientIdIsNotUUID() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "invalid-uuid-format",
                    "아침 공부",
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("클라이언트 ID는 UUID 형식이어야 합니다");
        }

        @Test
        @DisplayName("clientId가 대문자 UUID인 경우 검증 통과")
        void should_PassValidation_When_ClientIdIsUppercaseUUID() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550E8400-E29B-41D4-A716-446655440000",
                    "아침 공부",
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    // ========== name 검증 테스트 ==========

    @Nested
    @DisplayName("name 필드 검증")
    class NameValidation {

        @Test
        @DisplayName("name이 null인 경우 검증 실패")
        void should_FailValidation_When_NameIsNull() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    null,
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("스케줄 이름은 필수입니다");
        }

        @Test
        @DisplayName("name이 빈 문자열인 경우 검증 실패")
        void should_FailValidation_When_NameIsEmpty() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "",
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("name이 100자를 초과하는 경우 검증 실패")
        void should_FailValidation_When_NameExceeds100Characters() {
            // Given
            String longName = "a".repeat(101);
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    longName,
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("스케줄 이름은 100자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("name이 정확히 100자인 경우 검증 통과")
        void should_PassValidation_When_NameIsExactly100Characters() {
            // Given
            String exactName = "a".repeat(100);
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    exactName,
                    60
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    // ========== duration 검증 테스트 ==========

    @Nested
    @DisplayName("duration 필드 검증")
    class DurationValidation {

        @Test
        @DisplayName("duration이 null인 경우 검증 실패")
        void should_FailValidation_When_DurationIsNull() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "아침 공부",
                    null
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("집중 시간은 필수입니다");
        }

        @Test
        @DisplayName("duration이 0인 경우 검증 실패")
        void should_FailValidation_When_DurationIsZero() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "아침 공부",
                    0
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("집중 시간은 최소 1분이어야 합니다");
        }

        @Test
        @DisplayName("duration이 음수인 경우 검증 실패")
        void should_FailValidation_When_DurationIsNegative() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "아침 공부",
                    -1
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("집중 시간은 최소 1분이어야 합니다");
        }

        @Test
        @DisplayName("duration이 480분을 초과하는 경우 검증 실패")
        void should_FailValidation_When_DurationExceeds480() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "아침 공부",
                    481
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("집중 시간은 최대 480분(8시간)을 초과할 수 없습니다");
        }

        @Test
        @DisplayName("duration이 1분인 경우 검증 통과 (최소값)")
        void should_PassValidation_When_DurationIsMinimum() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "아침 공부",
                    1
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("duration이 480분인 경우 검증 통과 (최대값)")
        void should_PassValidation_When_DurationIsMaximum() {
            // Given
            CreateScheduleRequest request = new CreateScheduleRequest(
                    "550e8400-e29b-41d4-a716-446655440000",
                    "아침 공부",
                    480
            );

            // When
            Set<ConstraintViolation<CreateScheduleRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }
}

