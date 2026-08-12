package com.medifind.backend.repository;

import com.medifind.backend.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository
        extends JpaRepository<Medicine, Long> {

    List<Medicine> findByActiveTrue();

    List<Medicine> findByMedicineNameContainingIgnoreCaseAndActiveTrue(
            String medicineName
    );

    List<Medicine> findByGenericNameContainingIgnoreCaseAndActiveTrue(
            String genericName
    );
}