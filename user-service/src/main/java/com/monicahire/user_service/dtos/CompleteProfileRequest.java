package com.monicahire.user_service.dtos;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
 
@Data
public class CompleteProfileRequest {
 
    @NotBlank(message = "Company name is required")
    private String name;
 
    private String website;
    private String logoUrl;
    private String country;
    private String city;
 
    // Company Identity fields
    @NotBlank(message = "Industry is required")
    private String industry;
 
    @NotBlank(message = "Company size is required")
    private String companySize;
 
    @NotBlank(message = "Mission is required")
    private String mission;
 
    @NotBlank(message = "Culture is required")
    private String culture;
 
    @NotBlank(message = "Values are required")
    private String values;
 
    @NotBlank(message = "Work environment is required")
    private String workEnvironment;
 
    @NotBlank(message = "Tone is required")
    private String tone;
}
 