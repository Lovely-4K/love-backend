package com.lovely4k.backend.authentication.token;

import com.lovely4k.backend.authentication.CustomSuccessHandler;
import com.lovely4k.backend.authentication.OAuth2UserService;
import com.lovely4k.backend.authentication.exception.AccessDeniedHandlerException;
import com.lovely4k.backend.authentication.exception.AuthenticationEntryPointException;
import com.lovely4k.backend.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtFilter jwtFilter;
    private final AccessDeniedHandlerException accessDeniedHandlerException;
    private final AuthenticationEntryPointException authenticationEntryPointException;

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
            //권한 추가되면 사용
            .authorizeHttpRequests(
                authorize -> authorize
                    .requestMatchers(
                        antMatcher("/docs/**"),
                        antMatcher("/h2-console/**"),
                        antMatcher("/profile"),
                        antMatcher("/oauth2/**"),
                        antMatcher("/v1/members/**"),
                        antMatcher(POST,"/v1/couples/invitation-code"),
                        antMatcher(POST, "/v1/couples"),
                        antMatcher(GET, "/v1/couples"),
                        antMatcher(POST, "/v1/couples/recouple")
                    ).permitAll()
                    .requestMatchers(
                        antMatcher("/v1/**")
                    )
                    .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                    .anyRequest().authenticated()
            );

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
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 필요에 따라 조정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER > ROLE_GUEST");
        return roleHierarchy;
    }
}