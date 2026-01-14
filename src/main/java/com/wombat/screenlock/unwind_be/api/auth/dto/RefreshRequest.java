package com.wombat.screenlock.unwind_be.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 요청 DTO
 * 
 * <p>Access Token 갱신 시 전달되는 요청 데이터를 담는 클래스입니다.
 * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.</p>
 * 
 * <h3>Validation 규칙</h3>
 * <ul>
 *   <li>refreshToken: 필수</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.api.auth.controller.AuthController
 */
public record RefreshRequest(
    /**
     * Refresh Token
     * <p>이전에 발급받은 Refresh Token을 사용하여 새 토큰을 발급받습니다</p>
     */
    @NotBlank(message = "Refresh Token은 필수입니다")
    String refreshToken
) {}


