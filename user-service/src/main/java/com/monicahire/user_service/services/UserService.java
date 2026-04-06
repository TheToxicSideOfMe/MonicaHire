package com.monicahire.user_service.services;

import com.monicahire.user_service.dtos.CompleteProfileRequest;
import com.monicahire.user_service.dtos.CompanyProfileResponse;
import com.monicahire.user_service.models.CompanyIdentity;
import com.monicahire.user_service.models.CompanyProfile;
import com.monicahire.user_service.models.CompanyProfile.ProfileStatus;
import com.monicahire.user_service.repositories.CompanyProfileRepository;
import org.springframework.stereotype.Service;
 
import java.time.LocalDateTime;
 
@Service
public class UserService {
 
    private final CompanyProfileRepository profileRepository;
 
    public UserService(CompanyProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
 
    public CompanyProfileResponse getProfile(String userId) {
        CompanyProfile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return toResponse(profile);
    }
 
    public CompanyProfileResponse updateProfile(String userId, CompleteProfileRequest request) {
        CompanyProfile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
 
        profile.setName(request.getName());
        profile.setWebsite(request.getWebsite());
        profile.setLogoUrl(request.getLogoUrl());
        profile.setCountry(request.getCountry());
        profile.setCity(request.getCity());
        profile.setUpdatedAt(LocalDateTime.now());
 
        CompanyIdentity identity = new CompanyIdentity(
                request.getIndustry(),
                request.getCompanySize(),
                request.getMission(),
                request.getCulture(),
                request.getValues(),
                request.getWorkEnvironment(),
                request.getTone()
        );
        profile.setCompanyIdentity(identity);
        profile.setStatus(ProfileStatus.COMPLETE);
 
        CompanyProfile saved = profileRepository.save(profile);
        return toResponse(saved);
    }
 
    // Called by Kafka consumer when user.registered event is received
    public void createInitialProfile(String userId, String email) {
        // Avoid duplicates in case of Kafka redelivery
        if (profileRepository.existsById(userId)) {
            return;
        }
 
        CompanyProfile profile = new CompanyProfile();
        profile.setId(userId);
        profile.setEmail(email);
        profile.setStatus(ProfileStatus.INCOMPLETE);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
 
        profileRepository.save(profile);
    }
 
    private CompanyProfileResponse toResponse(CompanyProfile profile) {
        return new CompanyProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getEmail(),
                profile.getWebsite(),
                profile.getLogoUrl(),
                profile.getCountry(),
                profile.getCity(),
                profile.getStatus(),
                profile.getCompanyIdentity(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}