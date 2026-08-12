package com.medifind.backend.controller;

import com.medifind.backend.dto.response.PharmacyResponse;
import com.medifind.backend.service.PharmacyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pharmacies")
public class AdminPharmacyController {

    private final PharmacyService pharmacyService;

    public AdminPharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PharmacyResponse>> getPendingPharmacies() {
        return ResponseEntity.ok(
                pharmacyService.getPendingPharmacies()
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<PharmacyResponse> approvePharmacy(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                pharmacyService.approvePharmacy(id)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<PharmacyResponse> rejectPharmacy(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        return ResponseEntity.ok(
                pharmacyService.rejectPharmacy(id, reason)
        );
    }
}