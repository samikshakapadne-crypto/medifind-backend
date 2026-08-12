package com.medifind.backend.repository;

import com.medifind.backend.entity.RareMedicineRequest;
import com.medifind.backend.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RareMedicineRepository
        extends JpaRepository<RareMedicineRequest, Long> {

    /**
     * Get all requests of a customer
     */
    List<RareMedicineRequest> findByCustomerIdOrderByRequestedAtDesc(
            Long customerId
    );

    /**
     * Get all pending/approved/rejected requests
     */
    List<RareMedicineRequest> findByStatusOrderByRequestedAtAsc(
            RequestStatus status
    );

    /**
     * Check if the customer has already requested
     * the same medicine while the request is still pending
     */
    Optional<RareMedicineRequest> findByCustomerIdAndMedicineNameIgnoreCaseAndStatus(
            Long customerId,
            String medicineName,
            RequestStatus status
    );

    /**
     * Search requests by medicine name
     */
    List<RareMedicineRequest> findByMedicineNameContainingIgnoreCase(
            String medicineName
    );

    /**
     * Search requests by generic name
     */
    List<RareMedicineRequest> findByGenericNameContainingIgnoreCase(
            String genericName
    );

    /**
     * Count requests by status
     */
    long countByStatus(RequestStatus status);
}