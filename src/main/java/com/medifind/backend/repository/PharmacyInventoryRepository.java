package com.medifind.backend.repository;

import com.medifind.backend.entity.PharmacyInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyInventoryRepository
        extends JpaRepository<PharmacyInventory, Long> {

    List<PharmacyInventory> findByPharmacyIdAndActiveTrue(
            Long pharmacyId
    );

    List<PharmacyInventory>
    findByMedicineMedicineNameContainingIgnoreCaseAndPharmacyCityContainingIgnoreCaseAndActiveTrue(
            String medicineName,
            String city
    );

    boolean existsByPharmacyIdAndMedicineIdAndBatchNumberIgnoreCase(
            Long pharmacyId,
            Long medicineId,
            String batchNumber
    );
}