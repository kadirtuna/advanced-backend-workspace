package com.n11bootcamp.jwtornek.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.jwtornek.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// ✅ Kimlik doğrulaması var ama yetkisi yoksa (403) devreye giriyor
// Örneğin ileride rol tabanlı yetkilendirme eklenirse ve kullanıcının
// o role erişimi yoksa bu handler çalışır
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse body = new ErrorResponse(
                403,
                "Forbidden",
                "Bu kaynağa erişim yetkiniz yok",
                request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
