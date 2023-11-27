package com.lovely4k.backend.authentication.token;

import com.lovely4k.backend.authentication.CustomSuccessHandler;
import com.lovely4k.backend.authentication.OAuth2UserService;
import com.lovely4k.backend.authentication.exception.AccessDeniedHandlerException;
import com.lovely4k.backend.authentication.exception.AuthenticationEntryPointException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;
    private final JwtFilter jwtFilter;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // h2-console 사용 && csrf 비활성화
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        ;

        // Authorize of url
        http
            .authorizeHttpRequests(
                authorize -> authorize
                    .anyRequest().permitAll()
            );
        //권한 추가되면 사용
//        .authorizeHttpRequests(
//            authorize -> authorize
//                .requestMatchers(
//                    antMatcher("/v1/**")
//                ).hasRole(Role.USER.name())
//                .anyRequest().permitAll()
//        );

        // 로그아웃 설정
        http
            .logout(
                logoutConfigurer -> logoutConfigurer
                    .logoutSuccessUrl("/")
                    .logoutUrl("/logout")
                    .clearAuthentication(true))
        ;

        // CORS
        http
            .cors(
                cors -> cors
                    .configurationSource(corsConfigurationSource())
            );

        // oauth2 설정
        http
            .oauth2Login(
                loginConfigurer -> loginConfigurer
                    .userInfoEndpoint(uI -> uI.userService(oAuth2UserService))
                    .successHandler(customSuccessHandler)
            )
        ;

        // exception handling 설정
        http
            .exceptionHandling(
                exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(authenticationEntryPointException);
                    exceptionHandling.accessDeniedHandler(accessDeniedHandlerException);
                }
            )
        ;

        http
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        // Session 설정
        http
            .sessionManagement((
                sessionManagement -> sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)     // session 사용 안함 설정
            ));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://wooisac.netlify.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 필요에 따라 조정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}