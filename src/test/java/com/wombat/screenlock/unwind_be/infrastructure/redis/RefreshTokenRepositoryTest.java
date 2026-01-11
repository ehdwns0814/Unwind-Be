package com.wombat.screenlock.unwind_be.infrastructure.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * RefreshTokenRepository 단위 테스트
 * 
 * <p>Mockito를 사용하여 Redis 의존성을 Mocking하고
 * Repository 메서드의 동작을 검증합니다.</p>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RefreshTokenRepository 테스트")
class RefreshTokenRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RefreshTokenRepository refreshTokenRepository;

    private static final Long USER_ID = 1L;
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.refresh.token";
    private static final String EXPECTED_KEY = "refresh_token:1";
    private static final long TTL_SECONDS = 604800L;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("RefreshToken 저장 성공")
    void should_Save_When_ValidInput() {
        // Given & When
        refreshTokenRepository.save(USER_ID, TOKEN);

        // Then
        verify(valueOperations).set(
            eq(EXPECTED_KEY),
            eq(TOKEN),
            eq(TTL_SECONDS),
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("RefreshToken 조회 성공 - 존재하는 경우")
    void should_FindByUserId_When_TokenExists() {
        // Given
        given(valueOperations.get(EXPECTED_KEY)).willReturn(TOKEN);

        // When
        Optional<String> result = refreshTokenRepository.findByUserId(USER_ID);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TOKEN);
    }

    @Test
    @DisplayName("RefreshToken 조회 - 존재하지 않는 경우 빈 Optional 반환")
    void should_ReturnEmpty_When_TokenNotExists() {
        // Given
        given(valueOperations.get(EXPECTED_KEY)).willReturn(null);

        // When
        Optional<String> result = refreshTokenRepository.findByUserId(USER_ID);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("RefreshToken 삭제 성공")
    void should_Delete_When_Called() {
        // Given
        given(redisTemplate.delete(EXPECTED_KEY)).willReturn(true);

        // When
        refreshTokenRepository.delete(USER_ID);

        // Then
        verify(redisTemplate).delete(EXPECTED_KEY);
    }

    @Test
    @DisplayName("RefreshToken 존재 여부 확인 - 존재하는 경우")
    void should_ReturnTrue_When_TokenExists() {
        // Given
        given(redisTemplate.hasKey(EXPECTED_KEY)).willReturn(true);

        // When
        boolean exists = refreshTokenRepository.exists(USER_ID);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("RefreshToken 존재 여부 확인 - 존재하지 않는 경우")
    void should_ReturnFalse_When_TokenNotExists() {
        // Given
        given(redisTemplate.hasKey(EXPECTED_KEY)).willReturn(false);

        // When
        boolean exists = refreshTokenRepository.exists(USER_ID);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("RefreshToken TTL 갱신 성공")
    void should_RefreshTtl_When_TokenExists() {
        // Given
        given(redisTemplate.expire(eq(EXPECTED_KEY), eq(TTL_SECONDS), eq(TimeUnit.SECONDS)))
            .willReturn(true);

        // When
        boolean result = refreshTokenRepository.refreshTtl(USER_ID);

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate).expire(EXPECTED_KEY, TTL_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("RefreshToken TTL 갱신 실패 - 토큰이 존재하지 않는 경우")
    void should_ReturnFalse_When_RefreshTtlFails() {
        // Given
        given(redisTemplate.expire(eq(EXPECTED_KEY), eq(TTL_SECONDS), eq(TimeUnit.SECONDS)))
            .willReturn(false);

        // When
        boolean result = refreshTokenRepository.refreshTtl(USER_ID);

        // Then
        assertThat(result).isFalse();
    }
}

