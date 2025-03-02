package com.team01.crms.service_implementors;

import com.team01.crms.dto.request.LoginRequest;
import com.team01.crms.dto.response.ResponseObject;
import com.team01.crms.enums.Status;
import com.team01.crms.enums.TokenType;
import com.team01.crms.models.Account;
import com.team01.crms.models.Token;
import com.team01.crms.repositories.AccountRepo;
import com.team01.crms.repositories.TokenRepo;
import com.team01.crms.services.AccountService;
import com.team01.crms.services.JWTService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements AccountService {

    AccountRepo accountRepo;

    TokenRepo tokenRepo;

    JWTService jwtService;

    @Override
    public ResponseEntity<ResponseObject> login(LoginRequest request) {

        Account acc = accountRepo.findByEmailAndPassword(request.getEmail(), request.getPassword()).orElse(null);

        if (acc == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseObject.builder()
                            .message("Email or password is incorrect")
                            .body(null)
                            .build()
            );
        }

        //revoke all active tokens
        revokeAllActiveTokens(acc);

        //create new access token
        Token access = tokenRepo.save(
                Token.builder()
                        .account(acc)
                        .status(Status.TOKEN_ACTIVE.getValue())
                        .type(TokenType.ACCESS.getValue())
                        .value(jwtService.generateAccessToken(acc))
                        .build()
        );

        //Create the response data for code line 113
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", access.getValue());

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Login successfully")
                        .body(responseData)
                        .build()
        );
    }
    private void revokeAllActiveTokens(Account account) {
        List<Token> validTokens = tokenRepo.findAll().stream()
                .filter(token -> token.getAccount().getId().equals(account.getId())
                && token.getStatus().equals(Status.TOKEN_ACTIVE.getValue()))
                .toList();

        if(validTokens.isEmpty()) return;

        validTokens.forEach(token -> {
            token.setStatus(Status.TOKEN_EXPIRED.getValue());
            tokenRepo.save(token);
        });
    }
}
