package com.medifind.backend.controller;

import com.medifind.backend.dto.request.MedicineRequest;
import com.medifind.backend.dto.response.MedicineResponse;
import com.medifind.backend.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping
    public ResponseEntity<MedicineResponse> createMedicine(
            @Valid @RequestBody MedicineRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(medicineService.createMedicine(request));
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {

        return ResponseEntity.ok(
                medicineService.getAllMedicines()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                medicineService.getMedicineById(id)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicineResponse>> searchMedicines(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(
                medicineService.searchMedicines(query)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request
    ) {
        return ResponseEntity.ok(
                medicineService.updateMedicine(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateMedicine(
            @PathVariable Long id
    ) {
        medicineService.deactivateMedicine(id);
        return ResponseEntity.noContent().build();
    }
}