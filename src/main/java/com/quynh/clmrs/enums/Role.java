package com.quynh.clmrs.enums;

import com.quynh.clmrs.models.entity.Account;
import jakarta.servlet.http.HttpSession;

public class Role {
    public static final String ADMIN = "admin";
    public static final String FINANCE = "finance";
    public static final String APPROVER = "approver";
    public static final String CLAIMER = "claimer";

    public static Account getCurrentLoggedAccount(HttpSession session) {
        return session.getAttribute("account") != null ? (Account) session.getAttribute("account") : null;
    }
}
