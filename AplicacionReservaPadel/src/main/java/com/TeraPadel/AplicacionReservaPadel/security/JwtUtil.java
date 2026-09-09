package com.TeraPadel.AplicacionReservaPadel.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "MiClaveSecretaSuperSeguraInfalible123!";

    public String generarToken(String emailUsuario) {
        return Jwts.builder()
                .setSubject(emailUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public String obtenerEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY.getBytes()).build().parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerUsername(String token) {
        return obtenerEmail(token);
    }
}
