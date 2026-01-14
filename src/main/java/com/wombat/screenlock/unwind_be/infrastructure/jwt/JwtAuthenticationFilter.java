package com.wombat.screenlock.unwind_be.infrastructure.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터
 * 
 * <p>Authorization 헤더에서 JWT를 추출하여 인증을 수행합니다.
 * UsernamePasswordAuthenticationFilter 이전에 실행됩니다.</p>
 * 
 * <h3>동작 흐름</h3>
 * <ol>
 *   <li>HTTP 요청의 Authorization 헤더에서 Bearer 토큰 추출</li>
 *   <li>토큰이 존재하고 유효하면 JwtProvider로 검증</li>
 *   <li>검증 성공 시 SecurityContext에 Authentication 설정</li>
 *   <li>검증 실패 시 무시하고 다음 필터로 진행 (인증 실패로 처리되지 않음)</li>
 * </ol>
 * 
 * <h3>보안 주의사항</h3>
 * <ul>
 *   <li>토큰이 없거나 무효한 경우에도 필터 체인을 계속 진행합니다</li>
 *   <li>인증이 필요한 경로는 SecurityConfig에서 별도로 처리됩니다</li>
 *   <li>민감한 정보(토큰 전체)는 로깅하지 않습니다</li>
 * </ul>
 * 
 * @see com.wombat.screenlock.unwind_be.config.SecurityConfig
 * @see com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtProvider
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Authorization 헤더 이름 */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    /** Bearer 토큰 접두사 */
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    /**
     * 필터 실행 메서드
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO 예외
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 추출
        String token = extractToken(request);

        // 2. 토큰이 존재하고 유효하면 인증 설정
        if (token != null && jwtProvider.validateToken(token)) {
            Long userId = jwtProvider.getUserIdFromToken(token);
            
            // 3. Authentication 객체 생성
            // 인증된 사용자로 설정 (권한은 빈 리스트, 필요시 확장 가능)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of());
            
            // 4. 요청 정보 설정 (선택사항, 디버깅용)
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 5. SecurityContext에 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("JWT 인증 성공: userId={}", userId);
        }

        // 6. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     * 
     * <p>헤더 형식: "Authorization: Bearer {token}"</p>
     * 
     * @param request HTTP 요청
     * @return JWT 토큰 문자열 (없으면 null)
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}

