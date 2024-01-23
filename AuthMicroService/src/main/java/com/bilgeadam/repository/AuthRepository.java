package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth,Long> {
    Optional<Auth> findOptionalByEmailAndPassword(String email,String password);
    Optional<Auth> findOptionalByEmail(String email);
    Optional<Auth> findOptionalById(Long id);
}
