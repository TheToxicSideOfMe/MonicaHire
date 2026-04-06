package com.monicahire.user_service.models;

import java.time.LocalDateTime;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {

    @Id
    private String id;                // same UUID from auth-service, not generated here

    private String name;
    private String email;             // duplicated from auth intentionally
    private String website;
    private String logoUrl;
    private String country;
    private String city;

    @Enumerated(EnumType.STRING)
    private ProfileStatus status;     // INCOMPLETE, COMPLETE

    @Embedded
    private CompanyIdentity companyIdentity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public enum ProfileStatus {
        INCOMPLETE,   // just registered, hasn't filled profile yet
        COMPLETE      // filled company identity, RAG embedding triggered
    }
    
}