package com.wombat.screenlock.unwind_be.api.auth.dto;

/**
 * 토큰 응답 DTO
 * 
 * <p>인증 성공 시 반환되는 토큰 정보를 담는 클래스입니다.
 * 회원가입, 로그인, 토큰 갱신 API에서 공통으로 사용됩니다.</p>
 * 
 * <h3>응답 예시</h3>
 * <pre>
 * {
 *   "success": true,
 *   "data": {
 *     "accessToken": "eyJhbGciOiJIUzI1NiIs...",
 *     "refreshToken": "dGhpcyBpcyByZWZyZXNo...",
 *     "expiresIn": 1800
 *   },
 *   "error": null
 * }
 * </pre>
 * 
 * @see com.wombat.screenlock.unwind_be.api.auth.controller.AuthController
 */
public record TokenResponse(
    /**
     * Access Token (JWT)
     * <p>API 인증에 사용되는 토큰 (기본 만료 시간: 30분)</p>
     */
    String accessToken,

    /**
     * Refresh Token (JWT)
     * <p>Access Token 갱신에 사용되는 토큰 (만료 시간: 7일)</p>
     */
    String refreshToken,

    /**
     * Access Token 만료 시간 (초 단위)
     * <p>기본값: 1800초 (30분)</p>
     */
    Long expiresIn
) {}

