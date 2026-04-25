package com.n11bootcamp.jwtornek.controller;

import com.n11bootcamp.jwtornek.response.WelcomeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/message")
public class MessageController {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @GetMapping
    public ResponseEntity<WelcomeResponse> getMessage(Authentication authentication) {
        String username = authentication.getName();
        WelcomeResponse body = new WelcomeResponse(
                username,
                "Merhaba, " + username + "! JWT doğrulaması başarılı.",
                "N11 Backend Bootcamp – JWT Demo",
                "Kadir Tuna – Software Engineer",
                LocalDateTime.now().format(FORMATTER)
        );
        return ResponseEntity.ok(body);
    }
}

