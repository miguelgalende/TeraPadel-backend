package com.TeraPadel.AplicacionReservaPadel.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretProperty;

    @Value("${jwt.expiration-ms:900000}")
    private long expirationMs;

    private Key signingKey;

    @PostConstruct
    private void init() {
        if (secretProperty == null || secretProperty.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "jwt.secret debe estar configurado y tener al menos 32 bytes. " +
                            "Define la variable de entorno JWT_SECRET.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretProperty.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String emailUsuario) {
        return Jwts.builder()
                .setSubject(emailUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public String obtenerEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerUsername(String token) {
        return obtenerEmail(token);
    }
}
