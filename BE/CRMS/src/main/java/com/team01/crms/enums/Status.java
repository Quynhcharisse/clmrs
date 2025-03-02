package com.team01.crms.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Status {
    TOKEN_ACTIVE("active"),
    TOKEN_EXPIRED("expired"),
    ACCOUNT_ACTIVE("active"),
    ACCOUNT_BANNED("banned");

    private final String value;
}
