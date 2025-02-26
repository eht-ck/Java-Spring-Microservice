package com.teatreats.purchase.service;


import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    private final String secretkey =
            "bfc80827f07c171a3e1f0661a1abfd52ab1fec954283772e19c1a673efa58e41";

    Map<String, Object> claims = new HashMap<>();

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token has expired", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            final String role = extractRole(token);
            System.out.println(role);
            return "ADMIN".equals(role) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token is expired");
        }
    }
    private  int extractUserId(String token){
        return  extractClaim(token, claims -> claims.get("userId", Integer.class));

    }

    public int getUserId(String token){
        try{
            final int userId = extractUserId(token);
            System.out.println("User ID "+ userId);
            return  userId;
        }catch (ExpiredJwtException e) {
            throw new RuntimeException("User Id not found");
        }

    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }
}
