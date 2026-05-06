
package com.rest.user_service.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    @Value("${spring.app.jwtCookie}")
    private String jwtCookie;

    public String generateTokenFromUsername(String username) {
        return Jwts
                .builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public ResponseCookie generateJwtCookie(String username) {
        return ResponseCookie.from(jwtCookie, generateTokenFromUsername(username))
                .path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(false)
                .build();
    }

    public ResponseCookie getCleanCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
    }

    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if(cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public String getUsernameFromJwt(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {}

        return false;
    }

}
