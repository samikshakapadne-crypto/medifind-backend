package com.medifind.backend.service;

import com.medifind.backend.dto.request.CreatePharmacyRequest;
import com.medifind.backend.dto.response.PharmacyResponse;
import com.medifind.backend.entity.Pharmacy;
import com.medifind.backend.entity.User;
import com.medifind.backend.enums.PharmacyApprovalStatus;
import com.medifind.backend.enums.Role;
import com.medifind.backend.repository.PharmacyRepository;
import com.medifind.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PharmacyService(
            PharmacyRepository pharmacyRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.pharmacyRepository = pharmacyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PharmacyResponse createPharmacy(
            CreatePharmacyRequest request
    ) {

        String normalizedEmail =
                request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException(
                    "Phone number is already registered"
            );
        }

        User user = new User();

        user.setFullName(request.getOwnerName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(request.getPhone());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.PHARMACY);

        User savedUser = userRepository.save(user);

        Pharmacy pharmacy = new Pharmacy();

        pharmacy.setPharmacyName(
                request.getPharmacyName().trim()
        );

        pharmacy.setOwnerName(
                request.getOwnerName().trim()
        );

        pharmacy.setEmail(normalizedEmail);
        pharmacy.setPhone(request.getPhone());

        pharmacy.setAddress(request.getAddress());
        pharmacy.setCity(request.getCity());
        pharmacy.setState(request.getState());
        pharmacy.setPincode(request.getPincode());

        pharmacy.setLatitude(request.getLatitude());
        pharmacy.setLongitude(request.getLongitude());

        pharmacy.setApprovalStatus(
                PharmacyApprovalStatus.PENDING
        );

        pharmacy.setRejectionReason(null);

        pharmacy.setUser(savedUser);

        Pharmacy savedPharmacy =
                pharmacyRepository.save(pharmacy);

        return mapToResponse(savedPharmacy);
    }

    public List<PharmacyResponse> getApprovedPharmacies() {

        return pharmacyRepository
                .findByApprovalStatus(
                        PharmacyApprovalStatus.APPROVED
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PharmacyResponse getApprovedPharmacyById(Long id) {

        Pharmacy pharmacy =
                pharmacyRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pharmacy not found"
                                )
                        );

        if (pharmacy.getApprovalStatus()
                != PharmacyApprovalStatus.APPROVED) {

            throw new IllegalArgumentException(
                    "Pharmacy is not approved"
            );
        }

        return mapToResponse(pharmacy);
    }

    public List<PharmacyResponse> getPendingPharmacies() {

        return pharmacyRepository
                .findByApprovalStatus(
                        PharmacyApprovalStatus.PENDING
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PharmacyResponse approvePharmacy(Long id) {

        Pharmacy pharmacy =
                pharmacyRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pharmacy not found"
                                )
                        );

        if (pharmacy.getApprovalStatus()
                != PharmacyApprovalStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Only pending pharmacies can be approved"
            );
        }

        pharmacy.setApprovalStatus(
                PharmacyApprovalStatus.APPROVED
        );

        pharmacy.setRejectionReason(null);

        return mapToResponse(
                pharmacyRepository.save(pharmacy)
        );
    }

    public PharmacyResponse rejectPharmacy(
            Long id,
            String reason
    ) {

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Rejection reason is required"
            );
        }

        Pharmacy pharmacy =
                pharmacyRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pharmacy not found"
                                )
                        );

        if (pharmacy.getApprovalStatus()
                != PharmacyApprovalStatus.PENDING) {

            throw new IllegalArgumentException(
                    "Only pending pharmacies can be rejected"
            );
        }

        pharmacy.setApprovalStatus(
                PharmacyApprovalStatus.REJECTED
        );

        pharmacy.setRejectionReason(reason.trim());

        return mapToResponse(
                pharmacyRepository.save(pharmacy)
        );
    }

    private PharmacyResponse mapToResponse(
            Pharmacy pharmacy
    ) {

        return new PharmacyResponse(
                pharmacy.getId(),
                pharmacy.getPharmacyName(),
                pharmacy.getOwnerName(),
                pharmacy.getCity(),
                pharmacy.getApprovalStatus()
        );
    }
}