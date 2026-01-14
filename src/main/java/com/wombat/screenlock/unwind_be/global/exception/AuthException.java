package com.wombat.screenlock.unwind_be.global.exception;

/**
 * 인증 관련 예외
 * 
 * <p>인증 및 인가 과정에서 발생하는 예외를 나타냅니다.
 * BusinessException을 상속하여 표준화된 에러 처리를 제공합니다.</p>
 * 
 * <h3>사용 예시</h3>
 * <pre>
 * throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
 * throw new AuthException(ErrorCode.EMAIL_ALREADY_EXISTS);
 * throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
 * </pre>
 * 
 * @see BusinessException
 * @see ErrorCode
 * @see com.wombat.screenlock.unwind_be.global.handler.GlobalExceptionHandler
 */
public class AuthException extends BusinessException {

    /**
     * AuthException 생성자
     * 
     * @param errorCode 인증 관련 에러 코드 (A001, A002, A003 등)
     */
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}


