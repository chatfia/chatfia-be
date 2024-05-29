package com.project.chatfiabe.global.config;

import com.project.chatfiabe.domain.user.repository.AccessTokenRepository;
import com.project.chatfiabe.domain.user.jwt.JwtProvider;
import com.project.chatfiabe.domain.user.repository.AccessLogRepository;
import com.project.chatfiabe.domain.user.repository.RefreshTokenRepository;
import com.project.chatfiabe.domain.user.security.JwtAuthenticationFilter;
import com.project.chatfiabe.domain.user.security.JwtAuthorizationFilter;
import com.project.chatfiabe.domain.user.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AccessLogRepository accessLogRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Security 에서 제공하는 비밀번호 암호화 인터페이스의 구현체를 Bean 으로 등록
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 의 인증개체들을 관리하는 Manager Bean 등록
     * @param configuration : Security 인증관리 설정 Bean
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Login & JWT 생성을 담당하는 Filter Bean 등록
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, accessLogRepository, accessTokenRepository, refreshTokenRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    /**
     * JWT 검증과 인가를 담당하는 Filter Bean 등록
     * @return JwtAuthorizationFilter
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtProvider, userDetailsService);
    }

    /**
     * Security 에서 인증/인가 제어를 위해 관리하는 Filter 설정
     * @param security SecurityFilterChain 인터페이스 구현체
     * @return HttpSecurity
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        // CSRF(사이트 간 요청 위조) 설정 비활성화 : B/C 세션 방식이 아닌 JWT 방식을 사용
        security.csrf((csrf) -> csrf.disable());
        security.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        security.cors(Customizer.withDefaults()); // CORS 설정 추가

        // API 제어 설정 : 요청된 URI(URL) 기반으로 인증/인가 제어
        security.authorizeHttpRequests((request) ->
                        request.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용
                                .requestMatchers("/api/auth/**", "/login", "/signup").permitAll() // 로그인 & 회원가입 & Refresh Token 갱신 허가 (WHITE_LIST)
                                .requestMatchers("/actuator/health").permitAll() // health check API 허가 (WHITE_LIST)
                                .anyRequest().permitAll()
//                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리 진행
        );

        // Security 의 기본 설정인 Session 방식이 아닌 JWT 방식을 사용하기 위한 설정
        security.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // JWT 방식의 REST API 서버이기 때문에 FormLogin 방식, HttpBasic 방식 비활성화
        security.formLogin((formLogin) -> formLogin.disable())
                .httpBasic((httpBasic) -> httpBasic.disable());

        // JWT 필터 등록
        security.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        security.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return security.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("https://www.chatfia.online");
        config.addAllowedOriginPattern("https://chatfia.online");
        config.addAllowedOriginPattern("https://api.chatfia.online");
        config.addAllowedOriginPattern("https://chatfia.vercel.app");
        config.addAllowedOriginPattern("http://localhost:3000");
        config.addAllowedOriginPattern("http://localhost:3001");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
