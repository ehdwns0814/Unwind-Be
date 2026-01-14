package com.wombat.screenlock.unwind_be.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 Enum
 * 
 * <p>API 응답에서 사용되는 표준화된 에러 코드를 정의합니다.
 * HTTP 상태 코드와 함께 사용자에게 명확한 에러 정보를 제공합니다.</p>
 * 
 * <h3>에러 코드 형식</h3>
 * <ul>
 *   <li>공통 에러: C001, C002, ...</li>
 *   <li>인증 에러: A001, A002, A003, ...</li>
 *   <li>사용자 에러: U001, U002, ...</li>
 * </ul>
 * 
 * @see ErrorResponse
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ========== 공통 에러 (Common) ==========
    /**
     * 유효하지 않은 입력값
     */
    INVALID_INPUT(400, "C001", "유효하지 않은 입력값입니다"),

    /**
     * 서버 내부 오류
     */
    INTERNAL_SERVER_ERROR(500, "S001", "서버 내부 오류가 발생했습니다"),

    // ========== 인증 에러 (Auth) ==========
    /**
     * 이메일 또는 비밀번호가 일치하지 않음
     */
    INVALID_CREDENTIALS(401, "A001", "이메일 또는 비밀번호가 일치하지 않습니다"),

    /**
     * 이미 사용 중인 이메일
     */
    EMAIL_ALREADY_EXISTS(409, "A002", "이미 사용 중인 이메일입니다"),

    /**
     * 유효하지 않거나 만료된 Refresh Token
     */
    INVALID_REFRESH_TOKEN(401, "A003", "유효하지 않거나 만료된 토큰입니다");

    /**
     * HTTP 상태 코드
     */
    private final int status;

    /**
     * 에러 코드 (예: A001, C001)
     */
    private final String code;

    /**
     * 에러 메시지
     */
    private final String message;
}

