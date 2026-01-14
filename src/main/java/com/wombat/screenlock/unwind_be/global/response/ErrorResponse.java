package com.wombat.screenlock.unwind_be.global.response;

import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 에러 응답 DTO
 * 
 * <p>API 에러 발생 시 반환되는 표준화된 에러 정보를 담는 클래스입니다.
 * ErrorCode Enum을 기반으로 생성됩니다.</p>
 * 
 * <h3>응답 예시</h3>
 * <pre>
 * {
 *   "success": false,
 *   "data": null,
 *   "error": {
 *     "code": "A001",
 *     "message": "이메일 또는 비밀번호가 일치하지 않습니다"
 *   }
 * }
 * </pre>
 * 
 * @see ErrorCode
 * @see ApiResponse
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    /**
     * 에러 코드 (예: A001, C001)
     */
    private String code;

    /**
     * 에러 메시지
     */
    private String message;

    /**
     * ErrorCode로부터 ErrorResponse 생성
     * 
     * @param errorCode 에러 코드 Enum
     * @return ErrorResponse 인스턴스
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
    
    /**
     * 커스텀 메시지로 ErrorResponse 생성
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @return ErrorResponse 인스턴스
     */
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
}


