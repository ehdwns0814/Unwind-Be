package com.wombat.screenlock.unwind_be.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 Provider
 * 
 * <p>Access Token과 Refresh Token을 생성하고 검증합니다.
 * Secret Key는 환경변수에서 로드됩니다.</p>
 * 
 * <h3>토큰 스펙</h3>
 * <ul>
 *   <li>Access Token: 30분 만료 (1800초)</li>
 *   <li>Refresh Token: 7일 만료 (604800초)</li>
 *   <li>알고리즘: HS256</li>
 *   <li>Subject: userId (Long 타입을 String으로 변환)</li>
 * </ul>
 * 
 * <h3>보안 주의사항</h3>
 * <ul>
 *   <li>Secret Key는 최소 256bit (32자 이상)이어야 합니다</li>
 *   <li>환경변수 JWT_SECRET_KEY에서 로드됩니다</li>
 *   <li>민감한 정보(비밀번호, 토큰 전체)는 로깅하지 않습니다</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.config.SecurityConfig
 */
@Component
@Slf4j
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    /**
     * JwtProvider 생성자
     * 
     * @param secret Secret Key 문자열 (환경변수에서 주입)
     * @param accessTokenExpiration Access Token 만료 시간 (초 단위)
     * @param refreshTokenExpiration Refresh Token 만료 시간 (초 단위)
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration * 1000; // 초 → 밀리초 변환
        this.refreshTokenExpiration = refreshTokenExpiration * 1000;
    }

    /**
     * Access Token 생성
     * 
     * <p>사용자 ID를 Subject로 하여 30분 만료 Access Token을 생성합니다.</p>
     * 
     * @param userId 사용자 고유 ID
     * @return JWT Access Token 문자열
     */
    public String generateAccessToken(Long userId) {
        return generateToken(userId, accessTokenExpiration);
    }

    /**
     * Refresh Token 생성
     * 
     * <p>사용자 ID를 Subject로 하여 7일 만료 Refresh Token을 생성합니다.</p>
     * 
     * @param userId 사용자 고유 ID
     * @return JWT Refresh Token 문자열
     */
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, refreshTokenExpiration);
    }

    /**
     * 토큰에서 UserId 추출
     * 
     * <p>JWT의 Subject 클레임에서 사용자 ID를 추출합니다.</p>
     * 
     * @param token JWT 토큰 문자열
     * @return 사용자 고유 ID
     * @throws io.jsonwebtoken.JwtException 토큰 파싱 실패 시
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰 유효성 검증
     * 
     * <p>다음 항목들을 검증합니다:</p>
     * <ul>
     *   <li>서명 유효성 (Signature)</li>
     *   <li>만료 시간 (Expiration)</li>
     *   <li>토큰 형식 (Malformed)</li>
     *   <li>지원 여부 (Unsupported)</li>
     * </ul>
     * 
     * @param token 검증할 JWT 토큰 문자열
     * @return 유효 여부 (true: 유효, false: 무효)
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰");
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰");
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰");
        } catch (SignatureException e) {
            log.warn("유효하지 않은 JWT 서명");
        } catch (Exception e) {
            log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Access Token 만료 시간 반환 (초 단위)
     * 
     * @return Access Token 만료 시간 (초)
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpiration / 1000;
    }

    /**
     * JWT 토큰 생성 (Private Helper)
     * 
     * @param userId 사용자 고유 ID
     * @param expirationMs 만료 시간 (밀리초)
     * @return JWT 토큰 문자열
     */
    private String generateToken(Long userId, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰 파싱하여 Claims 추출 (Private Helper)
     * 
     * @param token JWT 토큰 문자열
     * @return Claims 객체
     * @throws io.jsonwebtoken.JwtException 토큰 파싱 실패 시
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

