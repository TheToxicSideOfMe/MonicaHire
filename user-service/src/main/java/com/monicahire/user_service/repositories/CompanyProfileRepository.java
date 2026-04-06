package com.monicahire.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monicahire.user_service.models.CompanyProfile;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile,String>{

}
