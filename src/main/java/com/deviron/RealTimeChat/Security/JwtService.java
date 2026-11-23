package com.deviron.RealTimeChat.Security;

import com.deviron.RealTimeChat.Entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {
        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
        this.expiration = expiration;
    }

    public String generateToken(User user){
        Date issuedAt =new Date();

        Date expirationDate = new Date(issuedAt.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(signingKey)
                .compact();
    }
    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);

        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expirationDate.before(new Date());
    }
}
