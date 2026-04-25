package com.n11bootcamp.jwtornek.service;

import com.n11bootcamp.jwtornek.auth.TokenManager;
import com.n11bootcamp.jwtornek.exception.AuthException;
import com.n11bootcamp.jwtornek.request.TokenRequest;
import com.n11bootcamp.jwtornek.response.TokenResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

// ✅ Token işlemlerine ait iş mantığı burada toplanıyor
// Controller sadece HTTP katmanıyla ilgileniyor, asıl kararlar burada veriliyor
@Service
public class AuthService {

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Kullanıcı adı ve şifreyi doğrulayıp iki token birden üretiyoruz
    public TokenResponse login(TokenRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = tokenManager.generateAccessToken(request.getUsername());
        String refreshToken = tokenManager.generateRefreshToken(request.getUsername());

        return new TokenResponse(accessToken, refreshToken, "Bearer", TokenManager.ACCESS_VALIDITY_SECONDS);
    }

    // ✅ Refresh token ile yeni token çifti alıyoruz (token rotation)
    // Sadece "refresh" tipindeki token burada kabul ediliyor
    // Hata durumunda null yerine AuthException fırlatıyoruz ki GlobalExceptionHandler
    // nedenini JSON olarak dönsün - önceden 401 dönüyordu ama body yoktu
    public TokenResponse refresh(String incomingRefreshToken) {
        if (incomingRefreshToken == null || incomingRefreshToken.isBlank()) {
            throw new AuthException("refresh_token alanı zorunlu, lütfen gönderin");
        }

        try {
            // ✅ getTokenType zaten token'ı parse ediyor, yani imza ve format
            // kontrolü burada gerçekleşiyor - ayrıca tokenValidate çağırmaya gerek yok
            String tokenType = tokenManager.getTokenType(incomingRefreshToken);

            if (!"refresh".equals(tokenType)) {
                throw new AuthException("Geçersiz token tipi: bu endpoint sadece refresh token kabul eder, access token göndermeyin");
            }

            if (!tokenManager.isExpired(incomingRefreshToken)) {
                throw new AuthException("Refresh token süresi dolmuş, lütfen tekrar giriş yapın");
            }

            String username = tokenManager.getUsernameToken(incomingRefreshToken);
            String newAccessToken = tokenManager.generateAccessToken(username);
            String newRefreshToken = tokenManager.generateRefreshToken(username);

            return new TokenResponse(newAccessToken, newRefreshToken, "Bearer", TokenManager.ACCESS_VALIDITY_SECONDS);

        } catch (AuthException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new AuthException("Refresh token süresi dolmuş, lütfen tekrar giriş yapın");
        } catch (SignatureException e) {
            throw new AuthException("Token imzası geçersiz, lütfen tekrar giriş yapın ve yeni token alın");
        } catch (JwtException e) {
            throw new AuthException("Token geçersiz veya bozuk");
        }
    }
}
