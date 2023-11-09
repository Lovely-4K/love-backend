package com.lovely4k.backend.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // h2-console 사용 && csrf 비활성화
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        ;

        // Authorize of url
        http.authorizeHttpRequests(
            authorize -> authorize
                .requestMatchers(
                    antMatcher("/h2-console"),
                    antMatcher("/v1/**")
                ).permitAll()

//                .requestMatchers(
//                    antMatcher("/v1/**")
//                ).hasRole(Role.USER.name())

                .anyRequest().permitAll()
        );

        // 로그아웃 설정
        http
            .logout(l -> l
                .logoutSuccessUrl("/")
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true))
        ;

        // CORS
        http.cors(
            cors -> cors.configurationSource(corsConfigurationSource())
        ); // CORS 설정 추가

        // oauth2 설정
        http.oauth2Login(ol -> ol.userInfoEndpoint(uI -> uI.userService(oAuth2UserService)));

        http.sessionManagement((sessionManagement -> sessionManagement
            .sessionFixation().changeSessionId() // session fixation 방지
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 전략
            .invalidSessionUrl("/") // 유효하지 않은 세션에서 요청시 리다이렉트 되는 url
            .maximumSessions(2) // 2개의 로그인만 가능
            .maxSessionsPreventsLogin(false))
        );// 새로운 로그인 발생시 기존 로그인이 아닌 새로운 로그인 허용

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