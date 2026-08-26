package com.medifind.backend.service;

import com.medifind.backend.dto.request.MedicineRequest;
import com.medifind.backend.dto.response.MedicineResponse;
import com.medifind.backend.entity.Medicine;
import com.medifind.backend.repository.MedicineRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    // ============================
    // CREATE MEDICINE
    // ============================

    public MedicineResponse createMedicine(
            MedicineRequest request
    ) {

        Medicine medicine = new Medicine();

        medicine.setMedicineName(
                request.getMedicineName().trim()
        );

        medicine.setGenericName(
                request.getGenericName()
        );

        medicine.setBrandName(
                request.getBrandName()
        );

        medicine.setManufacturer(
                request.getManufacturer().trim()
        );

        medicine.setCategory(
                request.getCategory().trim()
        );

        medicine.setStrength(
                request.getStrength()
        );

        medicine.setDosageForm(
                request.getDosageForm().trim()
        );

        medicine.setComposition(
                request.getComposition()
        );

        medicine.setDescription(
                request.getDescription()
        );

        medicine.setPrescriptionRequired(
                request.isPrescriptionRequired()
        );

        medicine.setRareMedicine(
                request.isRareMedicine()
        );

        medicine.setImageUrl(
                request.getImageUrl()
        );

        medicine.setActive(true);

        return mapToResponse(
                medicineRepository.save(medicine)
        );
    }


    // ============================
    // GET ALL MEDICINES
    // ============================

    public List<MedicineResponse> getAllMedicines() {

        return medicineRepository
                .findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================
    // PAGINATION + CATEGORY + SEARCH
    // ============================

    public Page<MedicineResponse> getMedicines(
            int page,
            int size,
            String category,
            String search
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        boolean hasCategory =
                category != null
                        && !category.isBlank()
                        && !category.equalsIgnoreCase("ALL");

        boolean hasSearch =
                search != null
                        && !search.isBlank();

        Page<Medicine> medicinePage;

        // Category + Search
        if (hasCategory && hasSearch) {

            medicinePage =
                    medicineRepository
                            .searchActiveMedicinesByCategory(
                                    search.trim(),
                                    category.trim(),
                                    pageable
                            );
        }

        // Category only
        else if (hasCategory) {

            medicinePage =
                    medicineRepository
                            .findByCategoryIgnoreCaseAndActiveTrue(
                                    category.trim(),
                                    pageable
                            );
        }

        // Search only
        else if (hasSearch) {

            medicinePage =
                    medicineRepository
                            .searchActiveMedicines(
                                    search.trim(),
                                    pageable
                            );
        }

        // All medicines
        else {

            medicinePage =
                    medicineRepository
                            .findByActiveTrue(pageable);
        }

        return medicinePage
                .map(this::mapToResponse);
    }


    // ============================
    // GET ALL CATEGORIES
    // ============================

    public List<String> getAllCategories() {

        return medicineRepository
                .findDistinctActiveCategories();
    }


    // ============================
    // GET MEDICINE BY ID
    // ============================

    public MedicineResponse getMedicineById(
            Long id
    ) {

        Medicine medicine =
                medicineRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Medicine not found"
                                )
                        );

        if (!medicine.isActive()) {

            throw new IllegalArgumentException(
                    "Medicine is inactive"
            );
        }

        return mapToResponse(medicine);
    }


    // ============================
    // OLD SEARCH API
    // ============================

    public List<MedicineResponse> searchMedicines(
            String query
    ) {

        if (query == null || query.isBlank()) {
            return getAllMedicines();
        }

        List<Medicine> byName =
                medicineRepository
                        .findByMedicineNameContainingIgnoreCaseAndActiveTrue(
                                query
                        );

        List<Medicine> byGeneric =
                medicineRepository
                        .findByGenericNameContainingIgnoreCaseAndActiveTrue(
                                query
                        );

        return java.util.stream.Stream
                .concat(
                        byName.stream(),
                        byGeneric.stream()
                )
                .distinct()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================
    // UPDATE MEDICINE
    // ============================

    public MedicineResponse updateMedicine(
            Long id,
            MedicineRequest request
    ) {

        Medicine medicine =
                medicineRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Medicine not found"
                                )
                        );

        medicine.setMedicineName(
                request.getMedicineName().trim()
        );

        medicine.setGenericName(
                request.getGenericName()
        );

        medicine.setBrandName(
                request.getBrandName()
        );

        medicine.setManufacturer(
                request.getManufacturer().trim()
        );

        medicine.setCategory(
                request.getCategory().trim()
        );

        medicine.setStrength(
                request.getStrength()
        );

        medicine.setDosageForm(
                request.getDosageForm().trim()
        );

        medicine.setComposition(
                request.getComposition()
        );

        medicine.setDescription(
                request.getDescription()
        );

        medicine.setPrescriptionRequired(
                request.isPrescriptionRequired()
        );

        medicine.setRareMedicine(
                request.isRareMedicine()
        );

        medicine.setImageUrl(
                request.getImageUrl()
        );

        return mapToResponse(
                medicineRepository.save(medicine)
        );
    }


    // ============================
    // DEACTIVATE MEDICINE
    // ============================

    public void deactivateMedicine(
            Long id
    ) {

        Medicine medicine =
                medicineRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Medicine not found"
                                )
                        );

        medicine.setActive(false);

        medicineRepository.save(medicine);
    }


    // ============================
    // ENTITY -> RESPONSE
    // ============================

    private MedicineResponse mapToResponse(
            Medicine medicine
    ) {

        return new MedicineResponse(
                medicine.getId(),
                medicine.getMedicineName(),
                medicine.getGenericName(),
                medicine.getBrandName(),
                medicine.getManufacturer(),
                medicine.getCategory(),
                medicine.getStrength(),
                medicine.getDosageForm(),
                medicine.getComposition(),
                medicine.getDescription(),
                medicine.isPrescriptionRequired(),
                medicine.isRareMedicine(),
                medicine.getImageUrl(),
                medicine.isActive()
        );
    }
}