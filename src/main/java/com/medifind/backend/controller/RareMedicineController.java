package com.medifind.backend.controller;

import com.medifind.backend.dto.request.RareMedicineRequestDto;
import com.medifind.backend.dto.response.RareMedicineResponse;
import com.medifind.backend.service.RareMedicineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rare-medicine")
public class RareMedicineController {

    private final RareMedicineService rareMedicineService;

    public RareMedicineController(
            RareMedicineService rareMedicineService
    ) {
        this.rareMedicineService = rareMedicineService;
    }

    @PostMapping
    public ResponseEntity<RareMedicineResponse> createRequest(
            Authentication authentication,
            @Valid @RequestBody RareMedicineRequestDto request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        rareMedicineService.createRequest(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<RareMedicineResponse>> getMyRequests(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                rareMedicineService.getMyRequests(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RareMedicineResponse>> getPendingRequests() {

        return ResponseEntity.ok(
                rareMedicineService.getAllPendingRequests()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RareMedicineResponse> getRequestById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                rareMedicineService.getRequestById(id)
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<RareMedicineResponse> approveRequest(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                rareMedicineService.approveRequest(id)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<RareMedicineResponse> rejectRequest(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                rareMedicineService.rejectRequest(id)
        );
    }
}