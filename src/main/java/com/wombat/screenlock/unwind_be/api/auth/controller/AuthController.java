package com.wombat.screenlock.unwind_be.api.auth.controller;

import com.wombat.screenlock.unwind_be.api.auth.dto.LoginRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.RefreshRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.SignUpRequest;
import com.wombat.screenlock.unwind_be.api.auth.dto.TokenResponse;
import com.wombat.screenlock.unwind_be.application.auth.AuthService;
import com.wombat.screenlock.unwind_be.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 API Controller
 * 
 * <p>사용자 인증 관련 API를 제공합니다.
 * 회원가입, 로그인, 토큰 갱신 기능을 포함합니다.</p>
 * 
 * <h3>엔드포인트</h3>
 * <ul>
 *   <li>POST /api/auth/signup - 회원가입</li>
 *   <li>POST /api/auth/login - 로그인</li>
 *   <li>POST /api/auth/refresh - 토큰 갱신</li>
 * </ul>
 * 
 * <h3>보안</h3>
 * <p>이 Controller의 모든 엔드포인트는 Spring Security에서 public으로 설정되어야 합니다.
 * (SecurityConfig에서 /api/auth/** 경로를 permitAll()로 설정)</p>
 * 
 * @see com.wombat.screenlock.unwind_be.api.auth.dto.SignUpRequest
 * @see com.wombat.screenlock.unwind_be.api.auth.dto.LoginRequest
 * @see com.wombat.screenlock.unwind_be.api.auth.dto.RefreshRequest
 * @see com.wombat.screenlock.unwind_be.api.auth.dto.TokenResponse
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * 
     * <p>새 사용자 계정을 생성하고 Access Token과 Refresh Token을 발급합니다.</p>
     * 
     * <h3>요청</h3>
     * <ul>
     *   <li>email: 이메일 (필수, 이메일 형식, 최대 255자)</li>
     *   <li>password: 비밀번호 (필수, 8~50자)</li>
     * </ul>
     * 
     * <h3>응답</h3>
     * <ul>
     *   <li>201 Created: 회원가입 성공</li>
     *   <li>400 Bad Request: 유효성 검증 실패</li>
     *   <li>409 Conflict: 이메일 중복</li>
     * </ul>
     * 
     * @param request 회원가입 요청 DTO
     * @return 201 Created + TokenResponse
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(
            @Valid @RequestBody SignUpRequest request) {
        TokenResponse tokenResponse = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tokenResponse));
    }

    /**
     * 로그인 API
     * 
     * <p>이메일과 비밀번호로 사용자를 인증하고 Access Token과 Refresh Token을 발급합니다.</p>
     * 
     * <h3>요청</h3>
     * <ul>
     *   <li>email: 이메일 (필수, 이메일 형식)</li>
     *   <li>password: 비밀번호 (필수)</li>
     * </ul>
     * 
     * <h3>응답</h3>
     * <ul>
     *   <li>200 OK: 로그인 성공</li>
     *   <li>400 Bad Request: 유효성 검증 실패</li>
     *   <li>401 Unauthorized: 인증 실패 (이메일/비밀번호 불일치)</li>
     * </ul>
     * 
     * @param request 로그인 요청 DTO
     * @return 200 OK + TokenResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    /**
     * 토큰 갱신 API
     * 
     * <p>Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.</p>
     * 
     * <h3>요청</h3>
     * <ul>
     *   <li>refreshToken: Refresh Token (필수)</li>
     * </ul>
     * 
     * <h3>응답</h3>
     * <ul>
     *   <li>200 OK: 토큰 갱신 성공</li>
     *   <li>400 Bad Request: 유효성 검증 실패</li>
     *   <li>401 Unauthorized: 유효하지 않거나 만료된 Refresh Token</li>
     * </ul>
     * 
     * @param request 토큰 갱신 요청 DTO
     * @return 200 OK + TokenResponse
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) {
        TokenResponse tokenResponse = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}

