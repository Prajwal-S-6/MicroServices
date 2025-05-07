package com.pm.auth_service.Utils;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JWTUtils.class);
    private final Key secretKey;

    public JWTUtils(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role) {
        return Jwts.builder().subject(email).claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (1000*60*60)))
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try {
            LOG.info(token);
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT");
        }
    }


}
