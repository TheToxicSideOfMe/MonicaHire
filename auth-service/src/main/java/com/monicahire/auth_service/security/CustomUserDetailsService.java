package com.monicahire.auth_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.monicahire.auth_service.models.Credential;
import com.monicahire.auth_service.repositories.CredentialRepository;




@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CredentialRepository credentialRepository;


@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Credential credential = credentialRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    System.out.println("Found user: " + credential.getUsername());
    System.out.println("Stored hash: " + credential.getPassword());
    return new CustomUserDetails(credential);
}

}