package com.monicahire.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monicahire.auth_service.models.Credential;

public interface CredentialRepository extends JpaRepository<Credential,String>{
    Optional<Credential> findByUsername(String username);
    Optional<Credential> findByEmail(String email);
}
