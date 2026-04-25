package com.n11bootcamp.jwtornek.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class TokenManager {

    // ✅ Refresh token mantığı: access token'ı kısa tutuyoruz ki çalınırsa
    // zararı minimum olsun. Süresi bitince kullanıcı tekrar şifre girmesin
    // diye daha uzun ömürlü bir refresh token veriyoruz. O da expire olunca
    // tekrar login olmak gerekiyor.
    // Demo için 1 dakikaya düşürdük — otomatik yenilemeyi hızlıca göstermek için
    private static final int accessValidity = 60 * 1000;            // 1 dakika (ms)
    private static final int refreshValidity = 60 * 60 * 1000;     // 60 dakika (ms)

    // TokenResponse'da expires_in alanını saniye cinsinden doldurmak için
    public static final long ACCESS_VALIDITY_SECONDS = accessValidity / 1000;

    // ✅ Anahtarı application.properties'ten okuyoruz
    // Önceden Keys.secretKeyFor() ile her başlatmada rastgele anahtar üretiliyordu,
    // bu yüzden uygulama restart edilince eski tokenlar geçersiz oluyordu
    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ Access token üretiyoruz - korumalı endpoint'lere erişmek için kullanılır
    // tokenType claim'i ile bu token'ın "access" olduğunu işaretliyoruz
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("www.opendart.com")
                .claim("tokenType", "access")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessValidity))
                .signWith(key)
                .compact();
    }

    // ✅ Refresh token üretiyoruz - sadece yeni access token almak için kullanılır
    // Daha uzun ömürlü, ama korumalı endpoint'lerde kabul edilmiyor
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("www.opendart.com")
                .claim("tokenType", "refresh")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshValidity))
                .signWith(key)
                .compact();
    }

    public boolean tokenValidate(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        // İmza yanlış, token süresi dolmuş, format bozuk gibi durumlarda
        // exception fırlatmak yerine false dönüyoruz - üst katman 401 döner
        try {
            return getUsernameToken(token) != null && isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsernameToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean isExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().after(new Date(System.currentTimeMillis()));
    }

    // Token tipini claim'den okuyoruz. Böylece access mi refresh mi
    // olduğunu anlayabiliyoruz - refresh token'la korumalı endpoint'e
    // erişilmesini istemiyoruz, access token'la da refresh istenmesini.
    public String getTokenType(String token) {
        Claims claims = getClaims(token);
        return (String) claims.get("tokenType");
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

}
