package com.example.catchingdata.config.AuthConfig;

import com.example.catchingdata.models.UserModel.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    private final String ACCESS_KEY_SECRET =
            "50645367566B59703373367639792442264529482B4D6251655468576D5A7134";
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }
    public String generateToken(UserDetails userDetails, User user) {
        return generateAccessToken(new HashMap<>(), userDetails, user);
    }
    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUserEmail(token);
        return (username.equals(userDetails.getUsername())) && !isExpiredToken(token);
    }
    public Long getUserIdFromToken(String token) {
        Jws<Claims> claims = Jwts
                .parser()
                .setSigningKey(getSigningAccessKey())
                .parseClaimsJws(token);
        return claims.getBody().get("userId", Long.class);
    }

    // PRIVATE
    private boolean isExpiredToken(String token) {
        return extractExpired(token).before(new Date());
    }
    private Date extractExpired(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningAccessKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private String generateAccessToken(
            HashMap<String, Object> extraClaims,
            UserDetails userDetails,
            User user
    ) {
        extraClaims.put("userId", user.getId());
        extraClaims.put("userEmail", user.getEmail());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningAccessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningAccessKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_KEY_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
