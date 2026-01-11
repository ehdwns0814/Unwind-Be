package com.wombat.screenlock.unwind_be.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * RefreshToken Redis Repository
 * 
 * <p>JWT Refresh Token의 저장, 조회, 삭제를 담당합니다.
 * Redis를 사용하여 빠른 토큰 검증과 자동 만료(TTL)를 지원합니다.</p>
 * 
 * <h3>저장 구조</h3>
 * <ul>
 *   <li>Key: refresh_token:{userId}</li>
 *   <li>Value: JWT Refresh Token 문자열</li>
 *   <li>TTL: 7일 (604800초)</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.config.RedisConfig
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenRepository {

    /** Redis Key 접두사 */
    private static final String KEY_PREFIX = "refresh_token:";
    
    /** TTL: 7일 (초 단위) */
    private static final long TTL_SECONDS = 604800L;

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * RefreshToken 저장
     * 
     * <p>기존 토큰이 있으면 덮어쓰며, TTL이 재설정됩니다.</p>
     * 
     * @param userId 사용자 ID
     * @param token JWT Refresh Token
     */
    public void save(Long userId, String token) {
        String key = generateKey(userId);
        redisTemplate.opsForValue().set(key, token, TTL_SECONDS, TimeUnit.SECONDS);
        log.debug("RefreshToken 저장 완료: userId={}", userId);
    }

    /**
     * RefreshToken 조회
     * 
     * @param userId 사용자 ID
     * @return RefreshToken Optional (존재하지 않거나 만료된 경우 empty)
     */
    public Optional<String> findByUserId(Long userId) {
        String key = generateKey(userId);
        String token = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(token);
    }

    /**
     * RefreshToken 삭제
     * 
     * <p>로그아웃 시 호출하여 토큰을 무효화합니다.</p>
     * 
     * @param userId 사용자 ID
     */
    public void delete(Long userId) {
        String key = generateKey(userId);
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.debug("RefreshToken 삭제 완료: userId={}", userId);
        }
    }

    /**
     * RefreshToken 존재 여부 확인
     * 
     * @param userId 사용자 ID
     * @return 존재 여부 (true: 존재, false: 미존재 또는 만료)
     */
    public boolean exists(Long userId) {
        String key = generateKey(userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * RefreshToken TTL 갱신
     * 
     * <p>토큰 갱신 시 TTL을 다시 7일로 연장합니다.</p>
     * 
     * @param userId 사용자 ID
     * @return 갱신 성공 여부
     */
    public boolean refreshTtl(Long userId) {
        String key = generateKey(userId);
        Boolean result = redisTemplate.expire(key, TTL_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    /**
     * Redis Key 생성
     * 
     * @param userId 사용자 ID
     * @return 형식: "refresh_token:{userId}"
     */
    private String generateKey(Long userId) {
        return KEY_PREFIX + userId;
    }
}

