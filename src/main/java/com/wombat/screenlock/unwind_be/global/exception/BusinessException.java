package com.wombat.screenlock.unwind_be.global.exception;

import lombok.Getter;

/**
 * 비즈니스 예외 기본 클래스
 * 
 * <p>애플리케이션의 비즈니스 로직에서 발생하는 예외를 나타냅니다.
 * ErrorCode를 포함하여 표준화된 에러 응답을 제공합니다.</p>
 * 
 * <h3>사용 예시</h3>
 * <pre>
 * throw new BusinessException(ErrorCode.USER_NOT_FOUND);
 * </pre>
 * 
 * @see ErrorCode
 * @see com.wombat.screenlock.unwind_be.global.handler.GlobalExceptionHandler
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 에러 코드 */
    private final ErrorCode errorCode;

    /**
     * BusinessException 생성자
     * 
     * @param errorCode 에러 코드 Enum
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}


