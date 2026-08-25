package com.medifind.backend.service;

import com.medifind.backend.dto.request.LoginRequest;
import com.medifind.backend.dto.request.RegisterRequest;
import com.medifind.backend.dto.response.LoginResponse;
import com.medifind.backend.dto.response.RegisterResponse;

import com.medifind.backend.entity.Pharmacy;
import com.medifind.backend.entity.User;

import com.medifind.backend.enums.PharmacyApprovalStatus;
import com.medifind.backend.enums.Role;

import com.medifind.backend.repository.PharmacyRepository;
import com.medifind.backend.repository.UserRepository;

import com.medifind.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PharmacyRepository pharmacyRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    // ==========================================
    // CUSTOMER REGISTRATION
    // ==========================================

    public RegisterResponse registerCustomer(
            RegisterRequest request
    ) {

        String normalizedEmail = request.getEmail()
                .toLowerCase()
                .trim();

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

        user.setFullName(
                request.getFullName().trim()
        );

        user.setEmail(normalizedEmail);

        user.setPhone(
                request.getPhone()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                "Customer registered successfully"
        );
    }


    // ==========================================
    // ADMIN REGISTRATION
    // ==========================================

    public RegisterResponse registerAdmin(
            RegisterRequest request
    ) {

        String normalizedEmail = request.getEmail()
                .toLowerCase()
                .trim();

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

        user.setFullName(
                request.getFullName().trim()
        );

        user.setEmail(normalizedEmail);

        user.setPhone(
                request.getPhone()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(Role.ADMIN);

        User savedUser =
                userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                "Admin registered successfully"
        );
    }


    // ==========================================
    // LOGIN
    // ==========================================

    public LoginResponse login(
            LoginRequest request
    ) {

        String normalizedEmail = request.getEmail()
                .toLowerCase()
                .trim();

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );


        // Check password
        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }


        // ======================================
        // PHARMACY APPROVAL CHECK
        // ======================================

        if (user.getRole() == Role.PHARMACY) {

            Pharmacy pharmacy =
                    pharmacyRepository
                            .findByUserId(user.getId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Pharmacy profile not found"
                                    )
                            );


            // Pending pharmacy cannot login
            if (pharmacy.getApprovalStatus()
                    == PharmacyApprovalStatus.PENDING) {

                throw new IllegalArgumentException(
                        "Your pharmacy registration is pending admin approval"
                );
            }


            // Rejected pharmacy cannot login
            if (pharmacy.getApprovalStatus()
                    == PharmacyApprovalStatus.REJECTED) {

                throw new IllegalArgumentException(
                        "Your pharmacy registration has been rejected"
                );
            }


            // Only APPROVED pharmacy reaches here
        }


        // ======================================
        // GENERATE JWT
        // ======================================

        String token =
                jwtService.generateToken(user);


        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}