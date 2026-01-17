package com.wombat.screenlock.unwind_be.api.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wombat.screenlock.unwind_be.api.stats.dto.CompletionRequest;
import com.wombat.screenlock.unwind_be.api.stats.dto.ForceQuitRequest;
import com.wombat.screenlock.unwind_be.domain.stats.entity.DailyStatistics;
import com.wombat.screenlock.unwind_be.domain.stats.repository.DailyStatisticsRepository;
import com.wombat.screenlock.unwind_be.domain.user.entity.Role;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 통계 API 통합 테스트
 * 
 * <p>MockMvc를 사용하여 StatsController의 엔드포인트를 통합 테스트합니다.</p>
 * 
 * <h3>테스트 범위</h3>
 * <ul>
 *   <li>POST /api/stats/completion - 완료 통계 전송</li>
 *   <li>POST /api/stats/force-quit - 강제 종료 카운트</li>
 *   <li>GET /api/stats/summary - 통계 요약 조회</li>
 * </ul>
 * 
 * <h3>테스트 환경</h3>
 * <ul>
 *   <li>H2 인메모리 데이터베이스</li>
 *   <li>실제 JWT 토큰 사용</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("통계 API 통합 테스트")
class StatsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyStatisticsRepository dailyStatisticsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    // 테스트 픽스처
    private static final String TEST_EMAIL = "stats@test.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String SCHEDULE_ID = "550e8400-e29b-41d4-a716-446655440000";

    private User testUser;
    private String accessToken;

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

        // JWT 토큰 생성
        accessToken = jwtProvider.generateAccessToken(testUser.getId());
    }

    @AfterEach
    void tearDown() {
        dailyStatisticsRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Authorization 헤더 생성 헬퍼
     */
    private String getAuthorizationHeader() {
        return "Bearer " + accessToken;
    }

    // ========== POST /api/stats/completion 테스트 ==========

    @Nested
    @DisplayName("POST /api/stats/completion")
    class CompletionTest {

        @Test
        @DisplayName("완료 통계 전송 성공")
        void should_Return200_When_ValidRequest() throws Exception {
            // Given
            String today = LocalDate.now().toString();
            CompletionRequest request = new CompletionRequest(
                    SCHEDULE_ID, true, 1800, false, today
            );

            // When & Then
            mockMvc.perform(post("/api/stats/completion")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.recorded").value(true))
                    .andExpect(jsonPath("$.data.dailyStats.date").exists())
                    .andExpect(jsonPath("$.data.dailyStats.totalSchedules").value(1))
                    .andExpect(jsonPath("$.data.dailyStats.completedSchedules").value(1))
                    .andExpect(jsonPath("$.data.dailyStats.totalFocusTime").value(1800))
                    .andExpect(jsonPath("$.error").doesNotExist());
        }

        @Test
        @DisplayName("동일 날짜 통계 누적")
        void should_AccumulateStats_When_SameDate() throws Exception {
            // Given
            String today = LocalDate.now().toString();
            CompletionRequest request1 = new CompletionRequest(
                    SCHEDULE_ID, true, 1800, false, today
            );
            CompletionRequest request2 = new CompletionRequest(
                    SCHEDULE_ID + "2", true, 2400, false, today
            );

            // 첫 번째 요청
            mockMvc.perform(post("/api/stats/completion")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request1)))
                    .andExpect(status().isOk());

            // 두 번째 요청 (누적 확인)
            mockMvc.perform(post("/api/stats/completion")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request2)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.dailyStats.totalSchedules").value(2))
                    .andExpect(jsonPath("$.data.dailyStats.completedSchedules").value(2))
                    .andExpect(jsonPath("$.data.dailyStats.totalFocusTime").value(4200));
        }

        @Test
        @DisplayName("유효하지 않은 요청 시 400 반환")
        void should_Return400_When_InvalidRequest() throws Exception {
            // Given - 필수 필드 누락
            String invalidRequest = """
                    {
                        "scheduleId": "invalid-uuid",
                        "completed": true
                    }
                    """;

            // When & Then
            mockMvc.perform(post("/api/stats/completion")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").exists());
        }

        @Test
        @DisplayName("인증 토큰 없을 때 401 반환")
        void should_Return401_When_NoToken() throws Exception {
            // Given
            String today = LocalDate.now().toString();
            CompletionRequest request = new CompletionRequest(
                    SCHEDULE_ID, true, 1800, false, today
            );

            // When & Then
            mockMvc.perform(post("/api/stats/completion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    // ========== POST /api/stats/force-quit 테스트 ==========

    @Nested
    @DisplayName("POST /api/stats/force-quit")
    class ForceQuitTest {

        @Test
        @DisplayName("강제 종료 카운트 기록 성공")
        void should_Return200_When_ValidRequest() throws Exception {
            // Given - ISO 8601 형식: 2026-01-17T14:30:00Z
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) + "Z";
            ForceQuitRequest request = new ForceQuitRequest(timestamp);

            // When & Then
            mockMvc.perform(post("/api/stats/force-quit")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.recorded").value(true))
                    .andExpect(jsonPath("$.data.forceQuitCount").value(1))
                    .andExpect(jsonPath("$.error").doesNotExist());
        }

        @Test
        @DisplayName("강제 종료 카운트 누적")
        void should_AccumulateForceQuit_When_MultipleRequests() throws Exception {
            // Given - ISO 8601 형식: 2026-01-17T14:30:00Z
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) + "Z";
            ForceQuitRequest request = new ForceQuitRequest(timestamp);

            // 첫 번째 요청
            mockMvc.perform(post("/api/stats/force-quit")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            // 두 번째 요청 (누적 확인)
            mockMvc.perform(post("/api/stats/force-quit")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.forceQuitCount").value(2));
        }

        @Test
        @DisplayName("유효하지 않은 타임스탬프 시 400 반환")
        void should_Return400_When_InvalidTimestamp() throws Exception {
            // Given
            String invalidRequest = """
                    {
                        "timestamp": "invalid-timestamp"
                    }
                    """;

            // When & Then
            mockMvc.perform(post("/api/stats/force-quit")
                            .header("Authorization", getAuthorizationHeader())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    // ========== GET /api/stats/summary 테스트 ==========

    @Nested
    @DisplayName("GET /api/stats/summary")
    class SummaryTest {

        @Test
        @DisplayName("통계 요약 조회 성공 (데이터 없음)")
        void should_Return200_When_NoStats() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/stats/summary")
                            .header("Authorization", getAuthorizationHeader()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.currentStreak").value(0))
                    .andExpect(jsonPath("$.data.longestStreak").value(0))
                    .andExpect(jsonPath("$.data.weeklyCompletionRate").value(0.0))
                    .andExpect(jsonPath("$.data.monthlyCompletionRate").value(0.0))
                    .andExpect(jsonPath("$.data.totalFocusTimeThisWeek").value(0))
                    .andExpect(jsonPath("$.data.totalFocusTimeThisMonth").value(0))
                    .andExpect(jsonPath("$.data.recentDays").isArray())
                    .andExpect(jsonPath("$.error").doesNotExist());
        }

        @Test
        @DisplayName("통계 요약 조회 성공 (데이터 있음)")
        void should_Return200_When_HasStats() throws Exception {
            // Given - 통계 데이터 생성
            LocalDate today = LocalDate.now();
            DailyStatistics stats1 = DailyStatistics.builder()
                    .user(testUser)
                    .date(today)
                    .build();
            stats1.recordCompletion(true, 1800, false);
            stats1.recordCompletion(true, 2400, false);
            dailyStatisticsRepository.save(stats1);

            DailyStatistics stats2 = DailyStatistics.builder()
                    .user(testUser)
                    .date(today.minusDays(1))
                    .build();
            stats2.recordCompletion(true, 1800, false);
            dailyStatisticsRepository.save(stats2);

            // When & Then
            mockMvc.perform(get("/api/stats/summary")
                            .header("Authorization", getAuthorizationHeader()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.currentStreak").exists())
                    .andExpect(jsonPath("$.data.longestStreak").exists())
                    .andExpect(jsonPath("$.data.weeklyCompletionRate").exists())
                    .andExpect(jsonPath("$.data.monthlyCompletionRate").exists())
                    .andExpect(jsonPath("$.data.totalFocusTimeThisWeek").exists())
                    .andExpect(jsonPath("$.data.totalFocusTimeThisMonth").exists())
                    .andExpect(jsonPath("$.data.recentDays").isArray())
                    .andExpect(jsonPath("$.error").doesNotExist());
        }

        @Test
        @DisplayName("인증 토큰 없을 때 401 반환")
        void should_Return401_When_NoToken() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/stats/summary"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }
}

