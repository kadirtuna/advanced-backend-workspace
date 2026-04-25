package com.n11bootcamp.jwtornek.config;

import com.n11bootcamp.jwtornek.auth.CustomAccessDeniedHandler;
import com.n11bootcamp.jwtornek.auth.CustomAuthenticationEntryPoint;
import com.n11bootcamp.jwtornek.auth.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebSecurity
// ✅ Method bazlı güvenlik anotasyonlarını (örn. @PreAuthorize) aktif hale
// getirir
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    // JWT token doğrulamasını yapan custom filter sınıfımız (bizim yazdığımız)
    private final JwtTokenFilter jwtTokenFilter;

    // ✅ 401 ve 403 hatalarında boş body yerine JSON döndüren handler'larımız
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    // Constructor injection ile bağımlılıkları alıyoruz
    public WebSecurityConfiguration(JwtTokenFilter jwtTokenFilter,
                                     CustomAuthenticationEntryPoint authenticationEntryPoint,
                                     CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // ✅ Şifreleri encode etmek için BCrypt algoritmasını kullanıyoruz
    // Kullanıcı kayıt olurken/ giriş yaparken şifreler bu encoder ile hashlenir
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ AuthenticationManager, Spring Security'nin kimlik doğrulama sürecini
    // yöneten merkez bileşeni
    // AuthenticationConfiguration üzerinden alınarak @Bean olarak projeye ekleniyor
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // ✅ Uygulamanın güvenlik kurallarını belirleyen ana yapı
    // Burada hangi endpointlere kim erişebilir, session nasıl yönetilir, hangi
    // filterlar devreye girer tanımlıyoruz
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF korumasını devre dışı bırakıyoruz (REST API'lerde genelde
                                                   // kapatılır)
                // ✅ Frontend (localhost:3000) isteklerine izin veriyoruz
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        // "/token" endpointine herkes erişebilir (hem ilk login hem refresh buradan)
                        // "/error" Spring Boot'un hata yönlendirme endpointi, erişilebilir olmalı
                        .requestMatchers("/token", "/error").permitAll()
                        // Diğer tüm istekler kimlik doğrulaması gerektirir
                        .anyRequest().authenticated())
                // ✅ Session yönetimini STATELESS yapıyoruz
                // Çünkü JWT ile çalışırken her istekte token taşınıyor, session tutulmuyor
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ✅ 401 ve 403 hatalarını özelleştiriyoruz
                // Böylece neden hata aldığını JSON olarak açıklayabiliyoruz
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        // ✅ UsernamePasswordAuthenticationFilter'dan önce kendi JWT filter'ımızı
        // çalıştırıyoruz
        // Böylece her request'te Authorization header içindeki token kontrol edilir
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // SecurityFilterChain objesi oluşturulup Spring'e verilir
    }

}
