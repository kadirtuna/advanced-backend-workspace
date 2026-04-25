package com.n11bootcamp.jwtornek.auth;

import java.io.IOException;
import java.util.ArrayList;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    // ✅ Bu attribute key'i CustomAuthenticationEntryPoint tarafından okunuyor
    // Filter neden auth'u reddettiğini buraya yazıyor, entry point JSON olarak dönüyor
    public static final String AUTH_ERROR_ATTR = "auth_error_reason";

    @Autowired
    private TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        /**
         * "Bearer 123hab2355"
         */
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = tokenManager.getUsernameToken(token);
            } catch (ExpiredJwtException e) {
                // Token süresi dolmuş - nedenini request'e yazıyoruz
                request.setAttribute(AUTH_ERROR_ATTR, "Token süresi dolmuş, lütfen refresh token ile yeni bir access token alın");
            } catch (Exception e) {
                // İmza geçersiz, format bozuk vs.
                request.setAttribute(AUTH_ERROR_ATTR, "Token geçersiz veya bozuk");
            }
        }

        if (username != null && token != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            // ✅ Sadece access token kabul ediyoruz. Refresh token sadece /token
            // endpointinde, grant_type=refresh_token ile kullanılmalı.
            String tokenType = tokenManager.getTokenType(token);
            if (!"access".equals(tokenType)) {
                request.setAttribute(AUTH_ERROR_ATTR,
                        "Refresh token bu endpointlerde kabul edilmez, " +
                        "yalnızca POST /token ile grant_type=refresh_token olarak kullanın");
                filterChain.doFilter(request, response);
                return;
            }

            if (tokenManager.tokenValidate(token)) {
                UsernamePasswordAuthenticationToken upassToken =
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                upassToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(upassToken);
            } else {
                request.setAttribute(AUTH_ERROR_ATTR, "Token doğrulaması başarısız");
            }
        }

        filterChain.doFilter(request, response);
    }
}
