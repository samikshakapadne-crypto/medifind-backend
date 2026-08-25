package com.medifind.backend.controller;

import com.medifind.backend.dto.request.MedicineRequest;
import com.medifind.backend.dto.response.MedicineResponse;
import com.medifind.backend.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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

    // ==========================================
    // CREATE MEDICINE
    // ==========================================

    @PostMapping
    public ResponseEntity<MedicineResponse> createMedicine(
            @Valid @RequestBody MedicineRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        medicineService.createMedicine(request)
                );
    }


    // ==========================================
    // OLD API - GET ALL MEDICINES
    // ==========================================

    @GetMapping
    public ResponseEntity<List<MedicineResponse>> getAllMedicines() {

        return ResponseEntity.ok(
                medicineService.getAllMedicines()
        );
    }


    // ==========================================
    // PAGINATION + SEARCH + CATEGORY FILTER
    // ==========================================

    @GetMapping("/paged")
    public ResponseEntity<Page<MedicineResponse>> getMedicinesPaged(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "12")
            int size,

            @RequestParam(required = false)
            String category,

            @RequestParam(required = false)
            String search

    ) {

        return ResponseEntity.ok(
                medicineService.getMedicines(
                        page,
                        size,
                        category,
                        search
                )
        );
    }


    // ==========================================
    // GET ALL CATEGORIES
    // ==========================================

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {

        return ResponseEntity.ok(
                medicineService.getAllCategories()
        );
    }


    // ==========================================
    // SEARCH MEDICINES - OLD API
    // ==========================================

    @GetMapping("/search")
    public ResponseEntity<List<MedicineResponse>> searchMedicines(
            @RequestParam String query
    ) {

        return ResponseEntity.ok(
                medicineService.searchMedicines(query)
        );
    }


    // ==========================================
    // GET MEDICINE BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                medicineService.getMedicineById(id)
        );
    }


    // ==========================================
    // UPDATE MEDICINE
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request
    ) {

        return ResponseEntity.ok(
                medicineService.updateMedicine(
                        id,
                        request
                )
        );
    }


    // ==========================================
    // DELETE / DEACTIVATE MEDICINE
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateMedicine(
            @PathVariable Long id
    ) {

        medicineService.deactivateMedicine(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}