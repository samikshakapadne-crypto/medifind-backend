package com.medifind.backend.controller;

import com.medifind.backend.dto.request.PrescriptionRequest;
import com.medifind.backend.dto.response.PrescriptionResponse;
import com.medifind.backend.service.PrescriptionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService
    ) {
        this.prescriptionService = prescriptionService;
    }

    // CUSTOMER uploads prescription
    @PostMapping
    public ResponseEntity<PrescriptionResponse> uploadPrescription(
            Authentication authentication,
            @Valid @RequestBody PrescriptionRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        prescriptionService.uploadPrescription(
                                authentication.getName(),
                                request
                        )
                );
    }

    // CUSTOMER views own prescriptions
    @GetMapping("/my")
    public ResponseEntity<List<PrescriptionResponse>> getMyPrescriptions(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                prescriptionService.getMyPrescriptions(
                        authentication.getName()
                )
        );
    }
}