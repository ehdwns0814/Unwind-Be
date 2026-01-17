package com.wombat.screenlock.unwind_be.infrastructure.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtProvider 단위 테스트
 * 
 * <p>
 * JWT 토큰 생성 및 검증 로직을 테스트합니다.
 * </p>
 * 
 * <h3>테스트 범위</h3>
 * <ul>
 * <li>토큰 생성: Access Token, Refresh Token</li>
 * <li>토큰 검증: 유효한 토큰, 만료된 토큰, 잘못된 형식, 잘못된 서명</li>
 * <li>UserId 추출</li>
 * </ul>
 */
@DisplayName("JwtProvider 테스트")
class JwtProviderTest {

    private JwtProvider jwtProvider;

    // 테스트용 Secret Key (32자 이상)
    private static final String SECRET = "test-secret-key-must-be-at-least-32-characters-long-for-hs256";
    private static final long ACCESS_TOKEN_EXPIRATION = 1800L; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 604800L; // 7일
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, ACCESS_TOKEN_EXPIRATION, REFRESH_TOKEN_EXPIRATION);
    }

    // ========== 토큰 생성 테스트 ==========

    @Nested
    @DisplayName("[REQ-FUNC-024/025/031] 토큰 생성")
    class TokenGeneration {

        @Test
        @DisplayName("[TC-041/043/045] Access Token 생성 성공")
        void should_GenerateValidAccessToken() {
            // When
            String accessToken = jwtProvider.generateAccessToken(USER_ID);

            // Then
            assertThat(accessToken).isNotNull();
            assertThat(accessToken).isNotBlank();
            assertThat(accessToken.split("\\.")).hasSize(3); // JWT는 3개 부분으로 구성
        }

        @Test
        @DisplayName("[TC-041/043/045] Refresh Token 생성 성공")
        void should_GenerateValidRefreshToken() {
            // When
            String refreshToken = jwtProvider.generateRefreshToken(USER_ID);

            // Then
            assertThat(refreshToken).isNotNull();
            assertThat(refreshToken).isNotBlank();
            assertThat(refreshToken.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("[TC-041/043/045] 생성된 토큰에서 UserId 추출 성공")
        void should_ExtractUserIdFromToken() {
            // Given
            String accessToken = jwtProvider.generateAccessToken(USER_ID);

            // When
            Long extractedUserId = jwtProvider.getUserIdFromToken(accessToken);

            // Then
            assertThat(extractedUserId).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("[TC-041/043/045] Access Token 만료 시간 반환 성공")
        void should_ReturnAccessTokenExpirationSeconds() {
            // When
            long expirationSeconds = jwtProvider.getAccessTokenExpirationSeconds();

            // Then
            assertThat(expirationSeconds).isEqualTo(ACCESS_TOKEN_EXPIRATION);
        }
    }

    // ========== 토큰 검증 테스트 ==========

    @Nested
    @DisplayName("[REQ-FUNC-031] 토큰 검증")
    class TokenValidation {

        @Test
        @DisplayName("[TC-046] 유효한 토큰 검증 성공")
        void should_ReturnTrue_When_TokenValid() {
            // Given
            String validToken = jwtProvider.generateAccessToken(USER_ID);

            // When
            boolean isValid = jwtProvider.validateToken(validToken);

            // Then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("[TC-046] 만료된 토큰 검증 실패")
        void should_ReturnFalse_When_TokenExpired() {
            // Given - 만료된 토큰 직접 생성
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            Date now = new Date();
            Date expiredDate = new Date(now.getTime() - 1000); // 1초 전에 만료

            String expiredToken = Jwts.builder()
                    .subject(String.valueOf(USER_ID))
                    .issuedAt(new Date(now.getTime() - 2000))
                    .expiration(expiredDate)
                    .signWith(key)
                    .compact();

            // When
            boolean isValid = jwtProvider.validateToken(expiredToken);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("[TC-046] 잘못된 형식의 토큰 검증 실패")
        void should_ReturnFalse_When_TokenMalformed() {
            // Given
            String malformedToken = "not.a.valid.jwt.token";

            // When
            boolean isValid = jwtProvider.validateToken(malformedToken);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("[TC-046] 잘못된 서명의 토큰 검증 실패")
        void should_ReturnFalse_When_SignatureInvalid() {
            // Given - 다른 Secret Key로 생성된 토큰
            String differentSecret = "different-secret-key-must-be-at-least-32-characters-long";
            SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));

            String tokenWithDifferentSignature = Jwts.builder()
                    .subject(String.valueOf(USER_ID))
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 3600000))
                    .signWith(differentKey)
                    .compact();

            // When
            boolean isValid = jwtProvider.validateToken(tokenWithDifferentSignature);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("[TC-046] null 토큰 검증 실패")
        void should_ReturnFalse_When_TokenIsNull() {
            // When
            boolean isValid = jwtProvider.validateToken(null);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("[TC-046] 빈 문자열 토큰 검증 실패")
        void should_ReturnFalse_When_TokenIsEmpty() {
            // When
            boolean isValid = jwtProvider.validateToken("");

            // Then
            assertThat(isValid).isFalse();
        }
    }
}
