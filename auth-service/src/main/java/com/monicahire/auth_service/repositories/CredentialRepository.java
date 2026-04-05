package com.monicahire.auth_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monicahire.auth_service.models.Credential;

public interface CredentialRepository extends JpaRepository<Credential,String>{

}
