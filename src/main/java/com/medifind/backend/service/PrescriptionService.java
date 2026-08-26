package com.medifind.backend.service;

import com.medifind.backend.dto.request.PrescriptionRequest;
import com.medifind.backend.dto.response.PrescriptionResponse;
import com.medifind.backend.entity.Pharmacy;
import com.medifind.backend.entity.Prescription;
import com.medifind.backend.entity.User;
import com.medifind.backend.enums.PrescriptionStatus;
import com.medifind.backend.repository.PharmacyRepository;
import com.medifind.backend.repository.PrescriptionRepository;
import com.medifind.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            UserRepository userRepository,
            PharmacyRepository pharmacyRepository
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
    }


    // ==========================================
    // CUSTOMER UPLOADS PRESCRIPTION
    // ==========================================

    @Transactional
    public PrescriptionResponse uploadPrescription(
            String customerEmail,
            PrescriptionRequest request
    ) {

        User customer = userRepository
                .findByEmail(
                        customerEmail.toLowerCase().trim()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found"
                        )
                );

        Pharmacy pharmacy = pharmacyRepository
                .findById(request.getPharmacyId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pharmacy not found"
                        )
                );

        Prescription prescription = new Prescription();

        prescription.setCustomer(customer);
        prescription.setPharmacy(pharmacy);

        prescription.setPrescriptionUrl(
                request.getPrescriptionUrl().trim()
        );

        prescription.setStatus(
                PrescriptionStatus.PENDING
        );

        prescription.setRejectionReason(null);
        prescription.setUploadedAt(LocalDateTime.now());
        prescription.setReviewedAt(null);

        Prescription savedPrescription =
                prescriptionRepository.save(prescription);

        return mapToResponse(savedPrescription);
    }


    // ==========================================
    // CUSTOMER VIEWS OWN PRESCRIPTIONS
    // ==========================================

    public List<PrescriptionResponse> getMyPrescriptions(
            String customerEmail
    ) {

        User customer = userRepository
                .findByEmail(
                        customerEmail.toLowerCase().trim()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found"
                        )
                );

        return prescriptionRepository
                .findByCustomerIdOrderByUploadedAtDesc(
                        customer.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ==========================================
    // PHARMACY VIEWS PENDING PRESCRIPTIONS
    // ==========================================

    public List<PrescriptionResponse> getPendingPrescriptions(
            String pharmacyEmail
    ) {

        Pharmacy pharmacy = pharmacyRepository
                .findByEmail(pharmacyEmail.toLowerCase().trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pharmacy not found"
                        )
                );

        return prescriptionRepository
                .findByPharmacyIdAndStatus(
                        pharmacy.getId(),
                        PrescriptionStatus.PENDING
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ==========================================
    // PHARMACY APPROVES PRESCRIPTION
    // ==========================================

    @Transactional
    public PrescriptionResponse approvePrescription(
            String pharmacyEmail,
            Long prescriptionId
    ) {

        Pharmacy pharmacy = pharmacyRepository
                .findByEmail(pharmacyEmail.toLowerCase().trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pharmacy not found"
                        )
                );

        Prescription prescription = prescriptionRepository
                .findById(prescriptionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Prescription not found"
                        )
                );

        if (!prescription.getPharmacy()
                .getId()
                .equals(pharmacy.getId())) {

            throw new IllegalArgumentException(
                    "This prescription does not belong to your pharmacy"
            );
        }

        if (prescription.getStatus()
                != PrescriptionStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Only pending prescriptions can be approved"
            );
        }

        prescription.setStatus(
                PrescriptionStatus.APPROVED
        );

        prescription.setRejectionReason(null);
        prescription.setReviewedAt(LocalDateTime.now());

        return mapToResponse(
                prescriptionRepository.save(prescription)
        );
    }


    // ==========================================
    // PHARMACY REJECTS PRESCRIPTION
    // ==========================================

    @Transactional
    public PrescriptionResponse rejectPrescription(
            String pharmacyEmail,
            Long prescriptionId,
            String reason
    ) {

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Rejection reason is required"
            );
        }

        Pharmacy pharmacy = pharmacyRepository
                .findByEmail(pharmacyEmail.toLowerCase().trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pharmacy not found"
                        )
                );

        Prescription prescription = prescriptionRepository
                .findById(prescriptionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Prescription not found"
                        )
                );

        if (!prescription.getPharmacy()
                .getId()
                .equals(pharmacy.getId())) {

            throw new IllegalArgumentException(
                    "This prescription does not belong to your pharmacy"
            );
        }

        if (prescription.getStatus()
                != PrescriptionStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Only pending prescriptions can be rejected"
            );
        }

        prescription.setStatus(
                PrescriptionStatus.REJECTED
        );

        prescription.setRejectionReason(
                reason.trim()
        );

        prescription.setReviewedAt(
                LocalDateTime.now()
        );

        return mapToResponse(
                prescriptionRepository.save(prescription)
        );
    }


    // ==========================================
    // ENTITY -> RESPONSE
    // ==========================================

    private PrescriptionResponse mapToResponse(
            Prescription prescription
    ) {

        return new PrescriptionResponse(
                prescription.getId(),

                prescription.getCustomer().getId(),
                prescription.getCustomer().getFullName(),

                prescription.getPharmacy().getId(),
                prescription.getPharmacy().getPharmacyName(),

                prescription.getPrescriptionUrl(),
                prescription.getStatus(),
                prescription.getRejectionReason(),
                prescription.getUploadedAt(),
                prescription.getReviewedAt()
        );
    }
}