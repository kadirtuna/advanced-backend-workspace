package com.n11bootcamp.jwtornek.exception;

// Token doğrulama ve kimlik doğrulama hatalarını temsil eden exception
// RuntimeException olarak tanımlandı çünkü controller'ın bunu try-catch'e
// almasına gerek yok, GlobalExceptionHandler yakalıyor
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}
