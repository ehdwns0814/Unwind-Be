package com.wombat.screenlock.unwind_be.global.handler;

import com.wombat.screenlock.unwind_be.global.exception.BusinessException;
import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import com.wombat.screenlock.unwind_be.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 핸들러
 * 
 * <p>API 예외를 표준 응답 포맷으로 변환합니다 (307 규칙 준수).</p>
 * 
 * <h3>처리 예외</h3>
 * <ul>
 *   <li>BusinessException: 비즈니스 예외 (4xx) → 정의된 HTTP Status</li>
 *   <li>MethodArgumentNotValidException: Validation 실패 → 400 Bad Request</li>
 *   <li>Exception: 그 외 예외 (5xx) → 500 Internal Server Error</li>
 * </ul>
 * 
 * <h3>로깅 전략</h3>
 * <ul>
 *   <li>4xx 예외: WARN 레벨 (메시지만 로깅)</li>
 *   <li>5xx 예외: ERROR 레벨 (전체 스택트레이스 로깅)</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.global.exception.BusinessException
 * @see com.wombat.screenlock.unwind_be.global.response.ApiResponse
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리 (BusinessException)
     * 
     * <p>애플리케이션의 비즈니스 로직에서 발생하는 예외를 처리합니다.
     * ErrorCode에 정의된 HTTP Status를 사용합니다.</p>
     * 
     * @param e BusinessException
     * @return ApiResponse<Void> (에러 응답)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        
        // 4xx 예외는 WARN 레벨로 로깅 (스택트레이스 제외)
        log.warn("비즈니스 예외 발생: code={}, message={}", 
                errorCode.getCode(), errorCode.getMessage());
        
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * Validation 예외 처리 (MethodArgumentNotValidException)
     * 
     * <p>@Valid 어노테이션으로 검증 실패 시 발생하는 예외를 처리합니다.</p>
     * 
     * @param e MethodArgumentNotValidException
     * @return ApiResponse<Void> (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        
        log.warn("Validation 예외 발생: {}", e.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT));
    }

    /**
     * 그 외 예외 처리 (Exception)
     * 
     * <p>예상치 못한 서버 오류를 처리합니다.
     * 전체 스택트레이스를 로깅하여 디버깅에 활용합니다.</p>
     * 
     * @param e Exception
     * @return ApiResponse<Void> (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        // 5xx 예외는 ERROR 레벨로 전체 스택트레이스 로깅
        log.error("서버 에러 발생", e);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}

