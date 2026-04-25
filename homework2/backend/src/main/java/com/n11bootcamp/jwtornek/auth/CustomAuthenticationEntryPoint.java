package com.n11bootcamp.jwtornek.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.jwtornek.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// ✅ Kimlik doğrulaması başarısız olduğunda (401) devreye giriyor
// Spring Security normalde boş body döner, biz buraya neden 401 aldığını
// açıklayan bir JSON ekliyoruz
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // JwtTokenFilter, neden auth'un reddedildiğini request attribute olarak bırakıyor
        // Eğer attribute yoksa genel bir mesaj yazıyoruz
        String reason = (String) request.getAttribute("auth_error_reason");
        if (reason == null) {
            reason = "Kimlik doğrulaması gerekiyor, lütfen geçerli bir access token gönderin";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse body = new ErrorResponse(401, "Unauthorized", reason, request.getRequestURI());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
