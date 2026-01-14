package com.wombat.screenlock.unwind_be.application.auth;

import com.wombat.screenlock.unwind_be.api.auth.dto.LoginRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.RefreshRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.TokenResponse;
import com.wombat.screenlock.unwind_be.domain.user.entity.Role;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.global.exception.AuthException;
import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtProvider;
import com.wombat.screenlock.unwind_be.infrastructure.redis.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * AuthService 단위 테스트
 * 
 * <p>Mockito를 사용하여 의존성을 Mocking하고
 * AuthService의 비즈니스 로직을 검증합니다.</p>
 * 
 * <h3>테스트 범위</h3>
 * <ul>
 *   <li>로그인: 성공, 사용자 없음, 비밀번호 불일치</li>
 *   <li>토큰 갱신: 성공, 토큰 무효, Redis 불일치</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    // 테스트 픽스처
    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String PASSWORD_HASH = "$2a$10$hashedpassword";
    private static final String ACCESS_TOKEN = "access.token.jwt";
    private static final String REFRESH_TOKEN = "refresh.token.jwt";
    private static final String NEW_REFRESH_TOKEN = "new.refresh.token.jwt";
    private static final long EXPIRES_IN = 1800L;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email(EMAIL)
                .passwordHash(PASSWORD_HASH)
                .role(Role.USER)
                .build();
        // Reflection으로 ID 설정 (테스트 전용)
        setUserId(testUser, USER_ID);
    }

    /**
     * 테스트용 User ID 설정 (Reflection)
     */
    private void setUserId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("User ID 설정 실패", e);
        }
    }

    // ========== 로그인 테스트 ==========

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("유효한 인증 정보로 로그인 시 토큰 반환")
        void should_ReturnToken_When_ValidCredentials() {
            // Given
            LoginRequest request = new LoginRequest(EMAIL, PASSWORD);
            
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(PASSWORD, PASSWORD_HASH)).willReturn(true);
            given(jwtProvider.generateAccessToken(USER_ID)).willReturn(ACCESS_TOKEN);
            given(jwtProvider.generateRefreshToken(USER_ID)).willReturn(REFRESH_TOKEN);
            given(jwtProvider.getAccessTokenExpirationSeconds()).willReturn(EXPIRES_IN);

            // When
            TokenResponse response = authService.login(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN);
            assertThat(response.expiresIn()).isEqualTo(EXPIRES_IN);
            
            verify(refreshTokenRepository).save(USER_ID, REFRESH_TOKEN);
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시 A001 예외")
        void should_ThrowA001_When_UserNotFound() {
            // Given
            LoginRequest request = new LoginRequest("nonexistent@example.com", PASSWORD);
            
            given(userRepository.findByEmail("nonexistent@example.com")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> {
                        AuthException authException = (AuthException) ex;
                        assertThat(authException.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                    });

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtProvider, never()).generateAccessToken(anyLong());
            verify(refreshTokenRepository, never()).save(anyLong(), anyString());
        }

        @Test
        @DisplayName("비밀번호 불일치 시 A001 예외")
        void should_ThrowA001_When_PasswordMismatch() {
            // Given
            LoginRequest request = new LoginRequest(EMAIL, "wrongpassword");
            
            given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches("wrongpassword", PASSWORD_HASH)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> {
                        AuthException authException = (AuthException) ex;
                        assertThat(authException.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
                    });

            verify(jwtProvider, never()).generateAccessToken(anyLong());
            verify(refreshTokenRepository, never()).save(anyLong(), anyString());
        }
    }

    // ========== 토큰 갱신 테스트 ==========

    @Nested
    @DisplayName("토큰 갱신")
    class Refresh {

        @Test
        @DisplayName("유효한 Refresh Token으로 새 토큰 반환")
        void should_ReturnNewToken_When_ValidRefreshToken() {
            // Given
            RefreshRequest request = new RefreshRequest(REFRESH_TOKEN);
            
            given(jwtProvider.validateToken(REFRESH_TOKEN)).willReturn(true);
            given(jwtProvider.getUserIdFromToken(REFRESH_TOKEN)).willReturn(USER_ID);
            given(refreshTokenRepository.findByUserId(USER_ID)).willReturn(Optional.of(REFRESH_TOKEN));
            given(jwtProvider.generateAccessToken(USER_ID)).willReturn(ACCESS_TOKEN);
            given(jwtProvider.generateRefreshToken(USER_ID)).willReturn(NEW_REFRESH_TOKEN);
            given(jwtProvider.getAccessTokenExpirationSeconds()).willReturn(EXPIRES_IN);

            // When
            TokenResponse response = authService.refresh(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(response.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
            assertThat(response.expiresIn()).isEqualTo(EXPIRES_IN);
            
            verify(refreshTokenRepository).save(USER_ID, NEW_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 갱신 시 A003 예외")
        void should_ThrowA003_When_TokenInvalid() {
            // Given
            String invalidToken = "invalid.token.jwt";
            RefreshRequest request = new RefreshRequest(invalidToken);
            
            given(jwtProvider.validateToken(invalidToken)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> authService.refresh(request))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> {
                        AuthException authException = (AuthException) ex;
                        assertThat(authException.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
                    });

            verify(jwtProvider, never()).getUserIdFromToken(anyString());
            verify(refreshTokenRepository, never()).findByUserId(anyLong());
            verify(refreshTokenRepository, never()).save(anyLong(), anyString());
        }

        @Test
        @DisplayName("Redis에 없는 토큰으로 갱신 시 A003 예외")
        void should_ThrowA003_When_TokenNotInRedis() {
            // Given
            RefreshRequest request = new RefreshRequest(REFRESH_TOKEN);
            
            given(jwtProvider.validateToken(REFRESH_TOKEN)).willReturn(true);
            given(jwtProvider.getUserIdFromToken(REFRESH_TOKEN)).willReturn(USER_ID);
            given(refreshTokenRepository.findByUserId(USER_ID)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> authService.refresh(request))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> {
                        AuthException authException = (AuthException) ex;
                        assertThat(authException.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
                    });

            verify(jwtProvider, never()).generateAccessToken(anyLong());
            verify(refreshTokenRepository, never()).save(anyLong(), anyString());
        }

        @Test
        @DisplayName("Redis 토큰과 불일치 시 A003 예외")
        void should_ThrowA003_When_TokenMismatchWithRedis() {
            // Given
            String differentToken = "different.token.jwt";
            RefreshRequest request = new RefreshRequest(REFRESH_TOKEN);
            
            given(jwtProvider.validateToken(REFRESH_TOKEN)).willReturn(true);
            given(jwtProvider.getUserIdFromToken(REFRESH_TOKEN)).willReturn(USER_ID);
            given(refreshTokenRepository.findByUserId(USER_ID)).willReturn(Optional.of(differentToken));

            // When & Then
            assertThatThrownBy(() -> authService.refresh(request))
                    .isInstanceOf(AuthException.class)
                    .satisfies(ex -> {
                        AuthException authException = (AuthException) ex;
                        assertThat(authException.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
                    });

            verify(jwtProvider, never()).generateAccessToken(anyLong());
            verify(refreshTokenRepository, never()).save(anyLong(), anyString());
        }
    }
}

