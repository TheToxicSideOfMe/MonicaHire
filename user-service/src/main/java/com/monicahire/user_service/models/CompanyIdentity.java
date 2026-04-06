package com.monicahire.user_service.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyIdentity {

    private String industry;
    private String companySize;       // e.g. "1-10", "11-50", "51-200"
    private String mission;
    private String culture;
    private String values;
    private String workEnvironment;   // e.g. "remote", "hybrid", "on-site"
    private String tone;              // e.g. "formal", "casual", "startup"
}