package com.monicahire.auth_service.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monicahire.auth_service.dtos.LoginRequest;
import com.monicahire.auth_service.dtos.LoginResponse;
import com.monicahire.auth_service.dtos.RegisterRequest;
import com.monicahire.auth_service.dtos.RegisterResponse;
import com.monicahire.auth_service.exceptions.InvalidCredentialsException;
import com.monicahire.auth_service.exceptions.UserAlreadyExistsException;
import com.monicahire.auth_service.kafka.UserRegisteredEvent;
import com.monicahire.auth_service.kafka.UserRegisteredProducer;
import com.monicahire.auth_service.models.Credential;
import com.monicahire.auth_service.models.Credential.Role;
import com.monicahire.auth_service.repositories.CredentialRepository;
import com.monicahire.auth_service.repositories.RefreshTokenRepository;
import com.monicahire.auth_service.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRegisteredProducer userRegisteredProducer;
    
    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {
        if (credentialRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        if (credentialRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already taken");
        }
    
        Credential newCredentials = new Credential();
        newCredentials.setUsername(request.getUsername());
        newCredentials.setEmail(request.getEmail());
        newCredentials.setPassword(passwordEncoder.encode(request.getPassword()));
        newCredentials.setRole(Role.COMPANY);
        newCredentials.setCreatedAt(LocalDateTime.now());
        newCredentials.setVerified(false);
    
        Credential savedCredentials = credentialRepository.save(newCredentials);
    
        // Publish event to Kafka
        UserRegisteredEvent event = new UserRegisteredEvent(
            savedCredentials.getId(),
            savedCredentials.getEmail(),
            savedCredentials.getUsername(),
            savedCredentials.getRole().name(),
            savedCredentials.getCreatedAt().toString()
        );
        userRegisteredProducer.publish(event);
    
        return RegisterResponse.from(savedCredentials);
    }

    
    
    @Transactional
    public LoginResponse loginUser(LoginRequest request){
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        //load user
        Credential user = credentialRepository.findByUsername(request.getUsername())
                        .orElseThrow(()-> new InvalidCredentialsException("User Not Found"));
        String accessToken = jwtUtil.generateAccessToken(request.getUsername(),user.getRole().name(),user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());

        refreshTokenService.saveRefreshToken(user, refreshToken);

        return new LoginResponse(
            accessToken,
            refreshToken,
            "Bearer",
            15 * 60 * 1000L, 
            RegisterResponse.from(user)
        );

    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }


    @Transactional
    public LoginResponse refreshTokens(String token){

        System.out.println(token);

        String username = jwtUtil.extractUsername(token);

        System.out.println(username);

        Credential user = credentialRepository.findByUsername(username)
            .orElseThrow(()-> new InvalidCredentialsException("User Not Found"));

        if (jwtUtil.validateRefreshToken(token, username)==false) {
            throw new InvalidCredentialsException("Invalid Refresh Token");
        }
        

        //generate new access and refresh tokens
        String accessToken = jwtUtil.generateAccessToken(username, user.getRole().name(),user.getId());;
        String refreshToken=jwtUtil.generateRefreshToken(username);
        refreshTokenService.saveRefreshToken(user, refreshToken);

        return new LoginResponse(
            accessToken,
            refreshToken,
            "Bearer",
            15 * 60 * 1000L, 
            RegisterResponse.from(user)
        );

    }
}
