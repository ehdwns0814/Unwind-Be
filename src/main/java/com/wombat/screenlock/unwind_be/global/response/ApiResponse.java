package com.wombat.screenlock.unwind_be.global.response;

import com.wombat.screenlock.unwind_be.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 표준 응답 래퍼
 * 
 * <p>모든 API 응답을 표준화된 형식으로 래핑하는 제네릭 클래스입니다.
 * 성공/실패 여부와 데이터 또는 에러 정보를 포함합니다.</p>
 * 
 * <h3>성공 응답 예시</h3>
 * <pre>
 * {
 *   "success": true,
 *   "data": { ... },
 *   "error": null
 * }
 * </pre>
 * 
 * <h3>에러 응답 예시</h3>
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
 * @param <T> 응답 데이터 타입
 * @see ErrorResponse
 * @see ErrorCode
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    /**
     * 요청 성공 여부
     */
    private boolean success;

    /**
     * 응답 데이터 (성공 시)
     */
    private T data;

    /**
     * 에러 정보 (실패 시)
     */
    private ErrorResponse error;

    /**
     * 성공 응답 생성
     * 
     * @param <T> 응답 데이터 타입
     * @param data 응답 데이터
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 에러 응답 생성
     * 
     * @param <T> 응답 데이터 타입
     * @param errorCode 에러 코드 Enum
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, ErrorResponse.of(errorCode));
    }
    
    /**
     * 커스텀 에러 응답 생성
     * 
     * @param <T> 응답 데이터 타입
     * @param code 에러 코드
     * @param message 에러 메시지
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, ErrorResponse.of(code, message));
    }
}


