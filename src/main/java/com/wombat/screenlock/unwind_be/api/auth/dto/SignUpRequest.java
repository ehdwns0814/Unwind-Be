package com.wombat.screenlock.unwind_be.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO
 * 
 * <p>사용자 회원가입 시 전달되는 요청 데이터를 담는 클래스입니다.
 * 이메일 형식과 비밀번호 길이에 대한 유효성 검증이 포함됩니다.</p>
 * 
 * <h3>Validation 규칙</h3>
 * <ul>
 *   <li>email: 필수, 이메일 형식, 최대 255자</li>
 *   <li>password: 필수, 8~50자</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.api.auth.controller.AuthController
 */
public record SignUpRequest(
    /**
     * 사용자 이메일 (로그인 ID)
     * <p>Unique 제약조건이 적용되어 중복 불가</p>
     */
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다")
    String email,

    /**
     * 비밀번호 (평문)
     * <p>서버에서 BCrypt로 해시되어 저장됩니다</p>
     */
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 50, message = "비밀번호는 8~50자여야 합니다")
    String password
) {}

