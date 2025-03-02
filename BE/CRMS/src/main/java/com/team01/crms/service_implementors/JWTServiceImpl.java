package com.team01.crms.service_implementors;

import com.team01.crms.enums.Status;
import com.team01.crms.models.Account;
import com.team01.crms.models.Token;
import com.team01.crms.repositories.TokenRepo;
import com.team01.crms.services.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.secret}")
    String secretKey;

    @Value("${jwt.access.expiration}") // 1 day, the value is millisecond
    long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}") // 5 months, the value is millisecond
    long refreshTokenExpiration;

    final TokenRepo tokenRepo;

    @Override
    public String extractEmail(String token) {
        return getClaim(token, Claims::getSubject);
    }

    @Override
    public String generateAccessToken(UserDetails user) {
        return generateToken(new HashMap<>(), user, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return generateToken(new HashMap<>(), user, refreshTokenExpiration);
    }

    @Override
    public Token checkTokenIsValid(Account acc, String tokenType) {
        Token t = tokenRepo.findByAccount_IdAndStatusAndType(
                acc.getId(), Status.TOKEN_ACTIVE.getValue(), tokenType).orElse(null);

        if (t != null && getClaim(t.getValue(), Claims::getExpiration).before(new Date())) {
            t.setStatus(Status.TOKEN_EXPIRED.getValue());
            tokenRepo.save(t);
            return null;
        }
        return t;
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaimsForToken(token));
    }

    private Claims extractAllClaimsForToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    String generateToken(Map<String, Object> extractClaims, UserDetails user, long expiredTime) {
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(getSigningKey())
                .compact();
    }


}
