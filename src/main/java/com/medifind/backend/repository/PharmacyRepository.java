package com.medifind.backend.repository;

import com.medifind.backend.entity.Pharmacy;
import com.medifind.backend.enums.PharmacyApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PharmacyRepository
        extends JpaRepository<Pharmacy, Long> {

    List<Pharmacy> findByApprovalStatus(
            PharmacyApprovalStatus approvalStatus
    );

    Optional<Pharmacy> findByEmail(String email);

    Optional<Pharmacy> findByUserId(Long userId);
}