package com.example.Assurance.Service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "j5IaxJK7ot0vlQzvmaOugcclctuorApLGwxq9h/A0d0=";
    private static final long EXPIRATION_TIME = 86400000; // 24h

    private final Key key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()).getBytes());

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token expiré : " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token non supporté : " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token mal formé : " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Signature invalide : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Token vide ou incorrect : " + e.getMessage());
        }
        return false;
    }
}
