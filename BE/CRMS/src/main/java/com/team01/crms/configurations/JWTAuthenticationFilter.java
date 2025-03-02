package com.team01.crms.configurations;

import com.team01.crms.enums.Status;
import com.team01.crms.enums.TokenType;
import com.team01.crms.models.Account;
import com.team01.crms.models.Token;
import com.team01.crms.repositories.AccountRepo;
import com.team01.crms.repositories.TokenRepo;
import com.team01.crms.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    private final TokenRepo tokenRepo;

    private final UserDetailsService userDetailsService;
    private final AccountRepo accountRepo;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        String accountEmail = jwtService.extractEmail(jwt);
        if (accountEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(accountEmail);
            Account account = accountRepo.findByEmail(userDetails.getUsername()).orElse(null);

            if (account == null && !account.getStatus().equalsIgnoreCase(Status.ACCOUNT_ACTIVE.getValue())) {
                filterChain.doFilter(request, response);
                return;
            }

            Token access = jwtService.checkTokenIsValid(account, TokenType.ACCESS.getValue());
            Token refresh = jwtService.checkTokenIsValid(account, TokenType.REFRESH.getValue());

            if (access == null) {
                // if both are null then return 403
                if (refresh == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
                //if access is null but refresh is not ==> create new access
                tokenRepo.save(
                        Token.builder()
                                .value(jwtService.generateAccessToken(account))
                                .type(TokenType.ACCESS.getValue())
                                .account(account)
                                .status(Status.ACCOUNT_ACTIVE.getValue())
                                .build()
                );
            }

            //grant permission
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}
