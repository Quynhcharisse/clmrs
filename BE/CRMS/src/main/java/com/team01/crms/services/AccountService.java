package com.team01.crms.services;

import com.team01.crms.dto.request.LoginRequest;
import com.team01.crms.dto.response.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    ResponseEntity<ResponseObject> login(LoginRequest request);
}
