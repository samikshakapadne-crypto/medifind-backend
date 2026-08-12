package com.medifind.backend.service;

import com.medifind.backend.dto.request.MedicineRequest;
import com.medifind.backend.dto.response.MedicineResponse;
import com.medifind.backend.entity.Medicine;
import com.medifind.backend.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public MedicineResponse createMedicine(MedicineRequest request) {

        Medicine medicine = new Medicine();

        medicine.setMedicineName(request.getMedicineName().trim());
        medicine.setGenericName(request.getGenericName());
        medicine.setBrandName(request.getBrandName());
        medicine.setManufacturer(request.getManufacturer().trim());
        medicine.setCategory(request.getCategory().trim());
        medicine.setStrength(request.getStrength());
        medicine.setDosageForm(request.getDosageForm().trim());
        medicine.setComposition(request.getComposition());
        medicine.setDescription(request.getDescription());
        medicine.setPrescriptionRequired(request.isPrescriptionRequired());
        medicine.setRareMedicine(request.isRareMedicine());
        medicine.setImageUrl(request.getImageUrl());
        medicine.setActive(true);

        return mapToResponse(medicineRepository.save(medicine));
    }

    public List<MedicineResponse> getAllMedicines() {

        return medicineRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MedicineResponse getMedicineById(Long id) {

        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Medicine not found")
                );

        if (!medicine.isActive()) {
            throw new IllegalArgumentException("Medicine is inactive");
        }

        return mapToResponse(medicine);
    }

    public List<MedicineResponse> searchMedicines(String query) {

        if (query == null || query.isBlank()) {
            return getAllMedicines();
        }

        List<Medicine> byName =
                medicineRepository
                        .findByMedicineNameContainingIgnoreCaseAndActiveTrue(query);

        List<Medicine> byGeneric =
                medicineRepository
                        .findByGenericNameContainingIgnoreCaseAndActiveTrue(query);

        return java.util.stream.Stream
                .concat(byName.stream(), byGeneric.stream())
                .distinct()
                .map(this::mapToResponse)
                .toList();
    }

    public MedicineResponse updateMedicine(
            Long id,
            MedicineRequest request
    ) {

        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Medicine not found")
                );

        medicine.setMedicineName(request.getMedicineName().trim());
        medicine.setGenericName(request.getGenericName());
        medicine.setBrandName(request.getBrandName());
        medicine.setManufacturer(request.getManufacturer().trim());
        medicine.setCategory(request.getCategory().trim());
        medicine.setStrength(request.getStrength());
        medicine.setDosageForm(request.getDosageForm().trim());
        medicine.setComposition(request.getComposition());
        medicine.setDescription(request.getDescription());
        medicine.setPrescriptionRequired(request.isPrescriptionRequired());
        medicine.setRareMedicine(request.isRareMedicine());
        medicine.setImageUrl(request.getImageUrl());

        return mapToResponse(medicineRepository.save(medicine));
    }

    public void deactivateMedicine(Long id) {

        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Medicine not found")
                );

        medicine.setActive(false);
        medicineRepository.save(medicine);
    }

    private MedicineResponse mapToResponse(Medicine medicine) {

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