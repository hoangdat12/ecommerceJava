package com.example.catchingdata.config.AuthConfig;

import com.example.catchingdata.models.UserModel.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class RefreshTokenService {
    private final String REFRESH_KEY_SECRET =
            "6251655468576D5A7133743677397A24432646294A404E635266556A586E3272";
    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }
    public String generateToken(UserDetails userDetails, User user) {
        return generateToken(new HashMap<>(), userDetails, user);
    }
    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUserEmail(token);
        return (username.equals(userDetails.getUsername())) && !isExpiredToken(token);
    }
    public Long getUserIdFromToken(String token) {
        Jws<Claims> claims = Jwts
                .parser()
                .setSigningKey(getSigningRefreshKey())
                .parseClaimsJws(token);
        return claims.getBody().get("userId", Long.class);
    }

    // PRIVATE
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningRefreshKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private String generateToken(
          HashMap<String, Object> extraClaims,
          UserDetails userDetails,
          User user
    ) {
        extraClaims.put("userId", user.getId());
        extraClaims.put("userEmail", user.getEmail());
        return Jwts.builder()
                .signWith(getSigningRefreshKey())
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 7))
                .compact();
    }

    private boolean isExpiredToken(String token) {
        Date timeExpired = extractClaim(token, Claims::getExpiration);
        return timeExpired.before(new Date());
    }
    private Key getSigningRefreshKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(REFRESH_KEY_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
