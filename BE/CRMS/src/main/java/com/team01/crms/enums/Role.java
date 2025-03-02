package com.team01.crms.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public enum Role {
    ADMIN,
    CLAIMER,
    APPROVER,
    FINANCE;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + this.name().toLowerCase())
        );
    }
}
