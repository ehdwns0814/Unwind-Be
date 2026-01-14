package com.wombat.screenlock.unwind_be.api.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wombat.screenlock.unwind_be.api.auth.dto.LoginRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.RefreshRequest;
import com.wombat.screenlock.unwind_be.domain.user.entity.Role;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtProvider;
import com.wombat.screenlock.unwind_be.infrastructure.redis.RefreshTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 인증 API 통합 테스트
 * 
 * <p>MockMvc를 사용하여 AuthController의 엔드포인트를 통합 테스트합니다.</p>
 * 
 * <h3>테스트 범위</h3>
 * <ul>
 *   <li>POST /api/auth/login - 로그인 성공/실패</li>
 *   <li>POST /api/auth/refresh - 토큰 갱신 성공/실패</li>
 * </ul>
 * 
 * <h3>테스트 환경</h3>
 * <ul>
 *   <li>H2 인메모리 데이터베이스</li>
 *   <li>Redis는 MockBean으로 대체</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("인증 API 통합 테스트")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    // 테스트 픽스처
    private static final String TEST_EMAIL = "integration@test.com";
    private static final String TEST_PASSWORD = "password123";
    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        String hashedPassword = passwordEncoder.encode(TEST_PASSWORD);
        testUser = User.builder()
                .email(TEST_EMAIL)
                .passwordHash(hashedPassword)
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        // Redis Mock 설정
        doNothing().when(refreshTokenRepository).save(anyLong(), anyString());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // ========== 로그인 API 테스트 ==========

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginApi {

        @Test
        @DisplayName("유효한 인증 정보로 로그인 성공")
        void should_ReturnToken_When_ValidCredentials() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.expiresIn").isNumber())
                    .andExpect(jsonPath("$.error").doesNotExist());
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시 401 반환")
        void should_Return401_When_UserNotFound() throws Exception {
            // Given
            LoginRequest request = new LoginRequest("nonexistent@test.com", TEST_PASSWORD);

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.error.code").value("A001"));
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시 401 반환")
        void should_Return401_When_PasswordMismatch() throws Exception {
            // Given
            LoginRequest request = new LoginRequest(TEST_EMAIL, "wrongpassword");

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("A001"));
        }

        @Test
        @DisplayName("이메일 형식이 잘못된 경우 400 반환")
        void should_Return400_When_InvalidEmailFormat() throws Exception {
            // Given
            LoginRequest request = new LoginRequest("invalid-email", TEST_PASSWORD);

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("C001"));
        }
    }

    // ========== 토큰 갱신 API 테스트 ==========

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class RefreshApi {

        @Test
        @DisplayName("유효한 Refresh Token으로 토큰 갱신 성공")
        void should_ReturnNewToken_When_ValidRefreshToken() throws Exception {
            // Given - 먼저 로그인하여 토큰 발급
            String refreshToken = jwtProvider.generateRefreshToken(testUser.getId());
            
            // Redis에 토큰이 있는 것처럼 Mock 설정
            given(refreshTokenRepository.findByUserId(testUser.getId()))
                    .willReturn(Optional.of(refreshToken));

            RefreshRequest request = new RefreshRequest(refreshToken);

            // When & Then
            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.expiresIn").isNumber());
        }

        @Test
        @DisplayName("유효하지 않은 Refresh Token으로 갱신 시 401 반환")
        void should_Return401_When_InvalidRefreshToken() throws Exception {
            // Given
            RefreshRequest request = new RefreshRequest("invalid.refresh.token");

            // When & Then
            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("A003"));
        }

        @Test
        @DisplayName("Redis에 없는 토큰으로 갱신 시 401 반환")
        void should_Return401_When_TokenNotInRedis() throws Exception {
            // Given
            String refreshToken = jwtProvider.generateRefreshToken(testUser.getId());
            
            // Redis에 토큰이 없는 것처럼 Mock 설정
            given(refreshTokenRepository.findByUserId(testUser.getId()))
                    .willReturn(Optional.empty());

            RefreshRequest request = new RefreshRequest(refreshToken);

            // When & Then
            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("A003"));
        }

        @Test
        @DisplayName("Refresh Token이 비어있는 경우 400 반환")
        void should_Return400_When_RefreshTokenEmpty() throws Exception {
            // Given
            RefreshRequest request = new RefreshRequest("");

            // When & Then
            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("C001"));
        }
    }
}

