package com.wombat.screenlock.unwind_be.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 * 
 * <p>사용자 로그인 시 전달되는 요청 데이터를 담는 클래스입니다.
 * 이메일과 비밀번호를 통해 인증을 수행합니다.</p>
 * 
 * <h3>Validation 규칙</h3>
 * <ul>
 *   <li>email: 필수, 이메일 형식</li>
 *   <li>password: 필수</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.api.auth.controller.AuthController
 */
public record LoginRequest(
    /**
     * 사용자 이메일 (로그인 ID)
     */
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    String email,

    /**
     * 비밀번호 (평문)
     * <p>서버에서 저장된 BCrypt 해시와 비교하여 검증됩니다</p>
     */
    @NotBlank(message = "비밀번호는 필수입니다")
    String password
) {}


