package com.monicahire.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monicahire.auth_service.models.Credential;
import com.monicahire.auth_service.models.RefreshToken;





@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByCredential(Credential credential);
    void deleteByToken(String token);
}
