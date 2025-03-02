package com.team01.crms.services;

import com.team01.crms.models.Account;
import com.team01.crms.models.Token;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String extractEmail(String token);

    String generateAccessToken(UserDetails user);

    String generateRefreshToken(UserDetails user);

    Token checkTokenIsValid(Account acc, String tokenType);

}
