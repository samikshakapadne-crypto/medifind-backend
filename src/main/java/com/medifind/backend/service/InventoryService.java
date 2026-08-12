package com.medifind.backend.service;

import com.medifind.backend.dto.request.InventoryRequest;
import com.medifind.backend.dto.response.InventoryResponse;
import com.medifind.backend.entity.Medicine;
import com.medifind.backend.entity.Pharmacy;
import com.medifind.backend.entity.PharmacyInventory;
import com.medifind.backend.enums.PharmacyApprovalStatus;
import com.medifind.backend.repository.MedicineRepository;
import com.medifind.backend.repository.PharmacyInventoryRepository;
import com.medifind.backend.repository.PharmacyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InventoryService {

    private final PharmacyInventoryRepository inventoryRepository;
    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;

    public InventoryService(
            PharmacyInventoryRepository inventoryRepository,
            PharmacyRepository pharmacyRepository,
            MedicineRepository medicineRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.medicineRepository = medicineRepository;
    }

    public InventoryResponse addInventory(InventoryRequest request) {

        Pharmacy pharmacy = pharmacyRepository.findById(request.getPharmacyId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Pharmacy not found")
                );

        if (pharmacy.getApprovalStatus() != PharmacyApprovalStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "Only approved pharmacies can add medicine stock"
            );
        }

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Medicine not found")
                );

        if (!medicine.isActive()) {
            throw new IllegalArgumentException("Medicine is inactive");
        }

        if (request.getExpiryDate().isBefore(LocalDate.now())
                || request.getExpiryDate().isEqual(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Expired medicine cannot be added"
            );
        }

        boolean batchAlreadyExists =
                inventoryRepository
                        .existsByPharmacyIdAndMedicineIdAndBatchNumberIgnoreCase(
                                request.getPharmacyId(),
                                request.getMedicineId(),
                                request.getBatchNumber().trim()
                        );

        if (batchAlreadyExists) {
            throw new IllegalArgumentException(
                    "This medicine batch already exists in the pharmacy"
            );
        }

        PharmacyInventory inventory = new PharmacyInventory();

        inventory.setPharmacy(pharmacy);
        inventory.setMedicine(medicine);
        inventory.setBatchNumber(request.getBatchNumber().trim());
        inventory.setQuantity(request.getQuantity());
        inventory.setSellingPrice(request.getSellingPrice());
        inventory.setExpiryDate(request.getExpiryDate());
        inventory.setMinimumStockLevel(
                request.getMinimumStockLevel() == null
                        ? 10
                        : request.getMinimumStockLevel()
        );
        inventory.setActive(true);

        return mapToResponse(inventoryRepository.save(inventory));
    }

    public List<InventoryResponse> getPharmacyInventory(Long pharmacyId) {

        return inventoryRepository
                .findByPharmacyIdAndActiveTrue(pharmacyId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<InventoryResponse> searchAvailableMedicine(
            String query,
            String city
    ) {
        return inventoryRepository
                .findByMedicineMedicineNameContainingIgnoreCaseAndPharmacyCityContainingIgnoreCaseAndActiveTrue(
                        query,
                        city
                )
                .stream()
                .filter(inventory ->
                        inventory.getQuantity() > 0
                                && inventory.getExpiryDate().isAfter(LocalDate.now())
                                && inventory.getPharmacy().getApprovalStatus()
                                == PharmacyApprovalStatus.APPROVED
                )
                .map(this::mapToResponse)
                .toList();
    }

    public InventoryResponse updateInventory(
            Long inventoryId,
            InventoryRequest request
    ) {
        PharmacyInventory inventory = inventoryRepository
                .findById(inventoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Inventory not found")
                );

        if (request.getQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative"
            );
        }

        if (!request.getExpiryDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Expiry date must be in the future"
            );
        }

        inventory.setQuantity(request.getQuantity());
        inventory.setSellingPrice(request.getSellingPrice());
        inventory.setExpiryDate(request.getExpiryDate());
        inventory.setMinimumStockLevel(
                request.getMinimumStockLevel() == null
                        ? 10
                        : request.getMinimumStockLevel()
        );

        return mapToResponse(inventoryRepository.save(inventory));
    }

    private InventoryResponse mapToResponse(
            PharmacyInventory inventory
    ) {
        boolean lowStock =
                inventory.getQuantity()
                        <= inventory.getMinimumStockLevel();

        return new InventoryResponse(
                inventory.getId(),
                inventory.getPharmacy().getId(),
                inventory.getPharmacy().getPharmacyName(),
                inventory.getPharmacy().getCity(),
                inventory.getMedicine().getId(),
                inventory.getMedicine().getMedicineName(),
                inventory.getMedicine().getGenericName(),
                inventory.getMedicine().getStrength(),
                inventory.getBatchNumber(),
                inventory.getQuantity(),
                inventory.getSellingPrice(),
                inventory.getExpiryDate(),
                inventory.getMinimumStockLevel(),
                lowStock,
                inventory.isActive()
        );
    }
}