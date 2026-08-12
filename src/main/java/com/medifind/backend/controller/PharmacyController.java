package com.medifind.backend.controller;

import com.medifind.backend.dto.request.CreatePharmacyRequest;
import com.medifind.backend.dto.response.PharmacyResponse;
import com.medifind.backend.service.PharmacyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @PostMapping
    public ResponseEntity<PharmacyResponse> createPharmacy(
            @Valid @RequestBody CreatePharmacyRequest request
    ) {
        PharmacyResponse response =
                pharmacyService.createPharmacy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PharmacyResponse>>
    getApprovedPharmacies() {

        return ResponseEntity.ok(
                pharmacyService.getApprovedPharmacies()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyResponse> getPharmacyById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                pharmacyService.getApprovedPharmacyById(id)
        );
    }
}