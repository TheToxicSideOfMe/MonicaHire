package com.monicahire.interview_service.controllers;

import com.monicahire.interview_service.dtos.InterviewTokenDto;
import com.monicahire.interview_service.services.InterviewTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview-tokens")
@RequiredArgsConstructor
public class InterviewTokenController {

    private final InterviewTokenService tokenService;

    // Called by candidate-service when a candidate applies
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody InterviewTokenDto.GenerateRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(tokenService.generateToken(request));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate token");
        }
    }

    // Called by candidate-service on interview submit — validates and consumes the token
    @PostMapping("/validate")
    public ResponseEntity<InterviewTokenDto.ValidationResponse> validate(
            @RequestBody InterviewTokenDto.ValidateRequest request) {
        return ResponseEntity.ok(tokenService.validateToken(request.getToken()));
    }

    // Peek at token info without consuming it
    @GetMapping("/{token}")
    public ResponseEntity<?> getTokenInfo(@PathVariable String token) {
        try {
            return ResponseEntity.ok(tokenService.getTokenInfo(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}