package com.lovely4k.backend.authentication.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovely4k.backend.authentication.RefreshToken;
import com.lovely4k.backend.authentication.exception.InvalidateTokenResponseWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    public static String AUTHORIZATION_HEADER = "Authorization";    // NOSONAR
    public static String BEARER_PREFIX = "Bearer ";     // NOSONAR
    private static final String REFRESH_HEADER = "Refresh-Token";
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String SECRET_KEY;      // NOSONAR

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {

        String refreshKey = resolveRefresh(request);
        if (StringUtils.hasText(refreshKey)) {
            sendAccessToken(response, refreshKey);
            return;
        }

        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt)) {
            try {
                byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
                Key key = Keys.hmacShaKeyFor(keyBytes);
                InvalidateTokenResponseWriter.write(key, jwt, response, objectMapper); //바디에 잘못된 토큰 쓰기
                log.debug("if 분기문 안 로직 ");
                Claims claims = resolveClaims(jwt); //jwt 토큰으로부터 Claim을 얻어 옴 Claim 내에는 사용자의 email이 들어있음
                updateSecurityContext(claims, jwt); //claim에 들어있는 email로 멤버를 조회해서 SecurityContext에 Member 넣기
            } catch (Exception e) {
                return; //예외는 InvalidateTokenResponseWriter.write(key, jwt, response, objectMapper); 여기서 상세 메시지 바디에 쓰고 예외 던짐
            }
        }
        filterChain.doFilter(request, response);
    }

    private void updateSecurityContext(Claims claims, String jwt) {
        String subject = claims.getSubject();

        log.debug("subject = " + subject);
        UserDetails principal = userDetailsService.loadUserByUsername(subject);
        log.debug("principal = " + principal);
        log.debug("authority =  " + principal.getAuthorities());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Claims resolveClaims(String jwt) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        } catch (ExpiredJwtException e) {
            log.debug("토큰 만료 예외 clamims: {}", e.getClaims());
            claims = e.getClaims();
        }
        return claims;
    }

    private void sendAccessToken(HttpServletResponse response, String refreshKey) throws IOException {
        RefreshToken refreshToken = tokenProvider.findRefreshTokenByKeyValue(refreshKey);
        String jwt = tokenProvider.generateAccessToken(refreshToken.getMember());
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().println(
            objectMapper.writeValueAsString(
                Map.of("accessToken", jwt)
            )
        );
        log.debug("업데이트 된 jwt: {}", jwt);
    }

    private String resolveToken(HttpServletRequest request) {
        log.debug("인증 헤더: {}", request.getHeader(AUTHORIZATION_HEADER));
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String resolveRefresh(HttpServletRequest request) {
        log.debug("리프레시 토큰 헤더: {}", request.getHeader(REFRESH_HEADER));
        String refreshToken = request.getHeader(REFRESH_HEADER);
        if (StringUtils.hasText(refreshToken)) {
            return refreshToken;
        }
        return null;
    }
}