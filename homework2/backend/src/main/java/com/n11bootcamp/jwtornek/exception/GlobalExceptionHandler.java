package com.n11bootcamp.jwtornek.exception;

import com.n11bootcamp.jwtornek.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// ✅ Controller'lardan fırlayan exception'ları tek bir yerden yakalıyoruz
// Böylece her controller'da ayrı ayrı try-catch yazmak zorunda kalmıyoruz
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Token geçersiz, süresi dolmuş, yanlış tipte vs. durumlarda
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException e, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "Unauthorized", e.getMessage(), request.getRequestURI()));
    }

    // Yanlış kullanıcı adı veya şifre girildiğinde AuthenticationManager fırlatır
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, "Unauthorized", "Kullanıcı adı veya şifre yanlış", request.getRequestURI()));
    }
}
