package com.monicahire.user_service.controllers;


import com.monicahire.user_service.dtos.CompleteProfileRequest;
import com.monicahire.user_service.dtos.CompanyProfileResponse;
import com.monicahire.user_service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get own profile
    @GetMapping("/profile")
    public ResponseEntity<CompanyProfileResponse> getProfile(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    // Complete/update profile
    @PutMapping("/profile")
    public ResponseEntity<CompanyProfileResponse> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CompleteProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    // Get any company profile by id (used internally by other services)
    @GetMapping("/{id}")
    public ResponseEntity<CompanyProfileResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }
}