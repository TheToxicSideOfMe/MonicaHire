package com.monicahire.user_service.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.monicahire.user_service.models.CompanyIdentity;
import com.monicahire.user_service.models.CompanyProfile.ProfileStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {

    private String id;
    private String name;
    private String email;
    private String website;
    private String logoUrl;
    private String country;
    private String city;
    private ProfileStatus status;
    private CompanyIdentity companyIdentity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}