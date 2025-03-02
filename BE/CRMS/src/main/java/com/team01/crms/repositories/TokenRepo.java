package com.team01.crms.repositories;

import com.team01.crms.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Integer> {

    Optional<Token> findByValue(String value);

    Optional<Token> findByAccount_IdAndStatusAndType(int accountId, String status, String type);
}
