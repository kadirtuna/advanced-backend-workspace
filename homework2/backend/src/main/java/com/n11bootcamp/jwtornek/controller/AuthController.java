package com.n11bootcamp.jwtornek.controller;

import com.n11bootcamp.jwtornek.request.TokenRequest;
import com.n11bootcamp.jwtornek.response.TokenResponse;
import com.n11bootcamp.jwtornek.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// ✅ OAuth2'deki gibi tek bir /token endpointimiz var, grant_type'a
// göre dallanıyor: password -> ilk login, refresh_token -> token yenileme
// Controller sadece HTTP katmanını yönetiyor, iş mantığı AuthService'te
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
        // grant_type null gelirse (örn. JSON'da hiç yazılmamış) NPE yerine 400 dönüyoruz
        if (request.getGrantType() == null) {
            return ResponseEntity.badRequest().build();
        }

        return switch (request.getGrantType()) {
            case "password" -> ResponseEntity.ok(authService.login(request));
            case "refresh_token" -> ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
            default -> ResponseEntity.badRequest().build();
        };
    }
}
