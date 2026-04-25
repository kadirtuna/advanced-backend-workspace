package com.n11bootcamp.jwtornek.request;

import com.fasterxml.jackson.annotation.JsonProperty;

// Token endpoint'ine gelen isteklerin DTO'su
// OAuth2 snake_case isimlendirmesini kullanıyoruz (grant_type, refresh_token)
// grant_type'a göre hangi alanlar dolduğu değişiyor:
//   password grant       -> username + password dolu
//   refresh_token grant  -> refresh_token dolu
public class TokenRequest {

    // @JsonProperty ile JSON'daki snake_case alan adını Java'daki camelCase değişkene bağlıyoruz
    @JsonProperty("grant_type")
    private String grantType;

    private String username;
    private String password;

    @JsonProperty("refresh_token")
    private String refreshToken;

    public TokenRequest() {
    }

    public TokenRequest(String grantType, String username, String password, String refreshToken) {
        this.grantType = grantType;
        this.username = username;
        this.password = password;
        this.refreshToken = refreshToken;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
