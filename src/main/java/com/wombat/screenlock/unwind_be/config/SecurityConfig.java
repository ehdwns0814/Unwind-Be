package com.wombat.screenlock.unwind_be.config;

import com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * 
 * <p>JWT 기반 인증을 위한 Security FilterChain을 구성합니다 (308 규칙 준수).</p>
 * 
 * <h3>보안 설정</h3>
 * <ul>
 *   <li>CSRF: 비활성화 (Stateless API)</li>
 *   <li>Session: STATELESS (세션 사용 안함)</li>
 *   <li>PasswordEncoder: BCryptPasswordEncoder</li>
 * </ul>
 * 
 * <h3>엔드포인트 접근 정책</h3>
 * <table>
 *   <tr><th>경로</th><th>접근 권한</th><th>설명</th></tr>
 *   <tr><td>/api/auth/**</td><td>PUBLIC</td><td>회원가입, 로그인, 토큰 갱신</td></tr>
 *   <tr><td>/api/docs/**</td><td>PUBLIC</td><td>Swagger 문서</td></tr>
 *   <tr><td>/swagger-ui/**</td><td>PUBLIC</td><td>Swagger UI</td></tr>
 *   <tr><td>/v3/api-docs/**</td><td>PUBLIC</td><td>OpenAPI 스펙</td></tr>
 *   <tr><td>/api/** (그 외)</td><td>PROTECTED</td><td>JWT 인증 필요</td></tr>
 * </table>
 * 
 * @see com.wombat.screenlock.unwind_be.infrastructure.jwt.JwtAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Security FilterChain Bean
     * 
     * <p>HTTP 요청에 대한 보안 필터 체인을 구성합니다.</p>
     * 
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 설정 오류 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 비활성화 (Stateless API)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 세션 사용 안함 (308 규칙: STATELESS)
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 경로별 인증 설정
                .authorizeHttpRequests(auth -> auth
                        // Public 엔드포인트: 인증 없이 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        
                        // 그 외 모든 엔드포인트: JWT 인증 필요
                        .anyRequest().authenticated()
                )
                
                // JWT 필터 추가 (UsernamePasswordAuthenticationFilter 이전에 실행)
                .addFilterBefore(jwtAuthenticationFilter, 
                        UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    /**
     * PasswordEncoder Bean
     * 
     * <p>BCrypt 알고리즘을 사용한 비밀번호 인코더입니다.
     * 비밀번호는 절대 평문으로 저장되지 않습니다 (308 규칙).</p>
     * 
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

