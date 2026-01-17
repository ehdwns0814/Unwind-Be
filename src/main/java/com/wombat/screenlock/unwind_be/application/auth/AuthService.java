package com.wombat.screenlock.unwind_be.application.auth;

import com.wombat.screenlock.unwind_be.api.auth.dto.LoginRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.RefreshRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.SignUpRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.TokenResponse;
import com.wombat.screenlock.unwind_be.domain.user.entity.User;
import com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository;
import com.wombat.screenlock.unwind_be.global.exception.AuthException;
import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtProvider;
import com.wombat.screenlock.unwind_be.infrastructure.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 * 
 * <p>
 * 회원가입, 로그인, 토큰 갱신 비즈니스 로직을 담당합니다.
 * </p>
 * 
 * <h3>관련 요구사항</h3>
 * <ul>
 * <li>[REQ-FUNC-024] 회원가입</li>
 * <li>[REQ-FUNC-025] 로그인</li>
 * <li>[REQ-FUNC-031] 토큰 갱신</li>
 * </ul>
 * 
 * <h3>관련 이슈</h3>
 * <ul>
 * <li>[BE-003] 인증 로직 및 보안 설정</li>
 * </ul>
 * 
 * <h3>트랜잭션 관리</h3>
 * <ul>
 * <li>클래스 레벨: @Transactional(readOnly = true)</li>
 * <li>데이터 변경 메서드: @Transactional로 오버라이드</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.domain.user.repository.UserRepository
 * @see com.wombat.screenlock.unwind_be.infrastructure.redis.RefreshTokenRepository
 * @see com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtProvider
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입
     * 
     * <h3>로직 순서</h3>
     * <ol>
     * <li>이메일 중복 체크</li>
     * <li>비밀번호 BCrypt 해시</li>
     * <li>User 엔티티 저장</li>
     * <li>Access/Refresh Token 발급</li>
     * <li>Refresh Token Redis 저장</li>
     * </ol>
     * 
     * <h3>예외</h3>
     * <ul>
     * <li>A002: EMAIL_ALREADY_EXISTS (이메일 중복)</li>
     * </ul>
     * 
     * @param request 회원가입 요청 DTO
     * @return TokenResponse (accessToken, refreshToken, expiresIn)
     * @throws AuthException EMAIL_ALREADY_EXISTS (A002)
     */
    @Transactional
    public TokenResponse signup(SignUpRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 비밀번호 BCrypt 해시
        String hashedPassword = passwordEncoder.encode(request.password());

        // 3. User 엔티티 저장
        User user = User.builder()
                .email(request.email())
                .passwordHash(hashedPassword)
                .build();
        User savedUser = userRepository.save(user);

        log.info("회원가입 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // 4-5. Token 발급 및 저장
        return generateAndSaveTokens(savedUser.getId());
    }

    /**
     * 로그인
     * 
     * <h3>로직 순서</h3>
     * <ol>
     * <li>이메일로 사용자 조회</li>
     * <li>비밀번호 BCrypt 검증</li>
     * <li>Access/Refresh Token 발급</li>
     * <li>Refresh Token Redis 저장</li>
     * </ol>
     * 
     * <h3>예외</h3>
     * <ul>
     * <li>A001: INVALID_CREDENTIALS (사용자 없음 또는 비밀번호 불일치)</li>
     * </ul>
     * 
     * @param request 로그인 요청 DTO
     * @return TokenResponse
     * @throws AuthException INVALID_CREDENTIALS (A001)
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_CREDENTIALS));

        // 2. 비밀번호 BCrypt 검증
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }

        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        // 3-4. Token 발급 및 저장
        return generateAndSaveTokens(user.getId());
    }

    /**
     * 토큰 갱신
     * 
     * <h3>로직 순서</h3>
     * <ol>
     * <li>Refresh Token 유효성 검증</li>
     * <li>Token에서 UserId 추출</li>
     * <li>Redis 저장 토큰과 비교</li>
     * <li>새 Access/Refresh Token 발급</li>
     * <li>새 Refresh Token Redis 저장</li>
     * </ol>
     * 
     * <h3>예외</h3>
     * <ul>
     * <li>A003: INVALID_REFRESH_TOKEN (토큰 무효, 만료, 또는 Redis 불일치)</li>
     * </ul>
     * 
     * @param request 토큰 갱신 요청 DTO
     * @return TokenResponse
     * @throws AuthException INVALID_REFRESH_TOKEN (A003)
     */
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        // 1. Refresh Token 유효성 검증
        if (!jwtProvider.validateToken(request.refreshToken())) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Token에서 UserId 추출
        Long userId = jwtProvider.getUserIdFromToken(request.refreshToken());

        // 3. Redis 저장 토큰과 비교
        String storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!storedToken.equals(request.refreshToken())) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        log.info("토큰 갱신: userId={}", userId);

        // 4-5. 새 Token 발급 및 저장
        return generateAndSaveTokens(userId);
    }

    /**
     * Token 생성 및 Redis 저장 (Private Helper)
     * 
     * <p>
     * Access Token과 Refresh Token을 생성하고,
     * Refresh Token을 Redis에 저장합니다.
     * </p>
     * 
     * @param userId 사용자 고유 ID
     * @return TokenResponse
     */
    private TokenResponse generateAndSaveTokens(Long userId) {
        String accessToken = jwtProvider.generateAccessToken(userId);
        String refreshToken = jwtProvider.generateRefreshToken(userId);

        // Redis에 Refresh Token 저장 (TTL: 7일)
        refreshTokenRepository.save(userId, refreshToken);

        return new TokenResponse(
                accessToken,
                refreshToken,
                jwtProvider.getAccessTokenExpirationSeconds());
    }
}
