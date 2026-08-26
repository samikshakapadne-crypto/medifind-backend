package com.medifind.backend.repository;

import com.medifind.backend.entity.Prescription;
import com.medifind.backend.enums.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription> findByCustomerIdOrderByUploadedAtDesc(Long customerId);

    List<Prescription> findByPharmacyIdAndStatus(
            Long pharmacyId,
            PrescriptionStatus status
    );
}